/*
    Sketchy
    Copyright (C) 2015 Matthew Havlovick
    http://www.quickdrawbot.com

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

Contact Info:

Matt Havlovick
QuickDraw
470 I St
Washougal, WA 98671

matt@quickdrawbot.com
http://www.quickdrawbot.com

This General Public License does not permit incorporating your program into
proprietary programs.  If your program is a subroutine library, you may
consider it more useful to permit linking proprietary applications with the
library.  If this is what you want to do, use the GNU Lesser General
Public License instead of this License.
*/

package com.sketchy.plotter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sketchy.metadata.MetaDataObject;
import com.sketchy.utils.ClassScanner;


public abstract class PlotterController {

	// This is to ensure that the Constructor in subclasses is created
	protected PlotterController(PlotterControllerProperties properties){}
	
	private static PlotterController _this = null;
	
	public abstract PlotterControllerProperties getProperties();
	
	public synchronized static PlotterController newInstance(PlotterControllerProperties properties) throws Exception {
		Class<? extends PlotterController> controllerClass = properties.getImplementationClass();
		
		try{
			if (_this!=null){
				// existing instance
				_this.shutDown();
				_this=null;
			}
			
			Constructor<? extends PlotterController> constructor = controllerClass.getConstructor(new Class[]{properties.getClass()});
			_this=constructor.newInstance(new Object[]{properties});
		} catch (IllegalAccessException e){
			throw new Exception("Error Getting PlotterController Instance from Class '" + controllerClass.getName() + "'! " + e.toString());
		} catch (InstantiationException e){
			throw new Exception("Error Getting PlotterController Instance from Class '" + controllerClass.getName() + "'!" + e.toString());
		} catch (InvocationTargetException e){
			throw new Exception(e.getTargetException().getMessage());
		}
		
		return _this;
	}

	public abstract void shutDown();
	
	public abstract void home() throws Exception;
	
	public abstract double getCurrentBackYPos(); 
	public abstract double getCurrentBackXPos();
	public abstract double getCurrentFrontYPos();
	public abstract double getCurrentFrontXPos();
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getPlotterControllerClassMap() throws Exception { 
		List<Class<?>> classes = ClassScanner.getClassesForPackage("com.sketchy.plotter.impl", MetaDataObject.class);
		Map<String, String> classMap = new LinkedHashMap<String, String>();
		for (Class<?> clazz:classes){
			Class<PlotterControllerProperties> plotterControllerClass = (Class<PlotterControllerProperties>) clazz;
			Class<PlotterController> implementationClass = (Class<PlotterController>) plotterControllerClass.newInstance().getImplementationClass();
			classMap.put(implementationClass.getName(), clazz.getName());
		}
		return classMap;
	}

	public abstract void moveTo(double newFrontXPos, double newFrontYPos, double newBackXPos, double newBackYPos) throws Exception;
	public abstract void drawTo(double newFrontXPos, double newFrontYPos, double newBackXPos, double newBackYPos) throws Exception;

	
}
	
