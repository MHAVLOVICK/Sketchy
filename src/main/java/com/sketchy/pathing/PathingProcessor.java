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

package com.sketchy.pathing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sketchy.metadata.MetaDataObject;
import com.sketchy.utils.ClassScanner;
import com.sketchy.utils.image.SketchyImage;

public abstract class PathingProcessor {

	public enum Status{
		INIT, DRAWING, PAUSED, CANCELLED, COMPLETED, ERROR;
	}
	
	//protected PathingControllerProperties properties = null; 
	// This is to ensure that the Constructor in subclasses is created
	protected PathingProcessor(PathingProcessorProperties properties){}
	
	private static PathingProcessor _this = null;

	public abstract PathingProcessorProperties getProperties();
	
	public abstract void run(SketchyImage sketchyImage);
	public abstract Status getStatus();
	public abstract String getStatusMessage();
	public abstract int getProgress();
	public abstract void cancelDrawing();
	public abstract void pauseDrawing();
	public abstract void resumeDrawing();

	public synchronized static PathingProcessor getInstance() throws Exception {
		if (_this==null){
			throw new Exception("Instance not created. Use newInstance(PathingControllerProperties properties) first!");
		}
		return _this;
	}
		
	public synchronized static PathingProcessor newInstance(PathingProcessorProperties properties) throws Exception {
		Class<? extends PathingProcessor> controllerClass = properties.getImplementationClass();
		
		try{
			if (_this!=null){
				// existing instance
				_this=null;
			}
			
			Constructor<? extends PathingProcessor> constructor = controllerClass.getConstructor(new Class[]{properties.getClass()});
			_this=constructor.newInstance(new Object[]{properties});
		} catch (IllegalAccessException e){
			throw new Exception("Error Getting PathingController Instance from Class '" + controllerClass.getName() + "'! " + e.toString());
		} catch (InstantiationException e){
			throw new Exception("Error Getting PathingController Instance from Class '" + controllerClass.getName() + "'!" + e.toString());
		} catch (InvocationTargetException e){
			throw new Exception(e.getTargetException().getMessage());
		}
		
		return _this;
	}
	
		
	@SuppressWarnings("unchecked")
	public static Map<String, String> getPathingProcessorClassMap() throws Exception { 
		List<Class<?>> classes = ClassScanner.getClassesForPackage("com.sketchy.pathing.impl", MetaDataObject.class);
		Map<String, String> classMap = new LinkedHashMap<String, String>();
		for (Class<?> clazz:classes){
			Class<PathingProcessorProperties> hardwareControllerClass = (Class<PathingProcessorProperties>) clazz;
			Class<PathingProcessor> implementationClass = (Class<PathingProcessor>) hardwareControllerClass.newInstance().getImplementationClass();
			classMap.put(implementationClass.getName(), clazz.getName());
		}
		return classMap;
	}
	
}

