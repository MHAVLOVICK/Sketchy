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

package com.sketchy.hardware;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sketchy.metadata.MetaDataObject;
import com.sketchy.utils.ClassScanner;


public abstract class HardwareController {

	//protected HardwareControllerProperties properties = null; 
	// This is to ensure that the Constructor in subclasses is created
	protected HardwareController(HardwareControllerProperties properties){}
	
	private static HardwareController _this = null;
	
	//private static HardwareController hardwareInterface = null;
	
	public abstract HardwareControllerProperties getProperties();

	public synchronized static HardwareController getInstance() throws Exception {
		if (_this==null){
			throw new Exception("Instance not created. Use newInstance(HardwareControllerProperties properties) first!");
		}
		return _this;
	}
		
	public synchronized static HardwareController newInstance(HardwareControllerProperties properties) throws Exception {
		Class<? extends HardwareController> controllerClass = properties.getImplementationClass();
		
		try{
			if (_this!=null){
				// existing instance
				_this.shutDown();
				_this=null;
			}
			
			Constructor<? extends HardwareController> constructor = controllerClass.getConstructor(new Class[]{properties.getClass()});
			_this=constructor.newInstance(new Object[]{properties});
		} catch (IllegalAccessException e){
			throw new Exception("Error Getting HardwareController Instance from Class '" + controllerClass.getName() + "'! " + e.toString());
		} catch (InstantiationException e){
			throw new Exception("Error Getting HardwareController Instance from Class '" + controllerClass.getName() + "'!" + e.toString());
		} catch (InvocationTargetException e){
			throw new Exception(e.getTargetException().getMessage());
		}
		
		return _this;
	}

	public abstract void penUp();

	public abstract void penDown();
		
	// Based on Bresenham's line algorithm
	public void moveMotors(int leftSteps, int rightSteps, long delayInMicroSeconds) throws Exception {
		// Just return if no steps occurring
		if ((leftSteps==0) && (rightSteps==0)) return;
		
		boolean swapSteps=Math.abs(rightSteps) > Math.abs(leftSteps);
	    if (swapSteps){
	    	int temp=leftSteps;
	    	leftSteps=rightSteps;
	    	rightSteps=temp;
	    }

	    Direction leftMotorDirection =  Direction.fromValue(leftSteps);
	    Direction rightMotorDirection = Direction.fromValue(rightSteps);
		
	    leftSteps = Math.abs(leftSteps);  
	    rightSteps = Math.abs(rightSteps);  
	    
        int error=leftSteps/2;
        for (int steps=0;steps<leftSteps;steps++){
	    	Direction stepRight=Direction.NONE;
        	error-=rightSteps;
        	if(error<0){
        		stepRight=rightMotorDirection;
        		error+=leftSteps;
        	}
        	if (swapSteps){
        		stepMotors(stepRight, leftMotorDirection, delayInMicroSeconds);
        	} else {
        		stepMotors(leftMotorDirection, stepRight, delayInMicroSeconds);
        	}
	    }
	}
	
	public abstract void stepMotors(Direction leftMotorDirection, Direction rightMotorDirection, long delayInMicroSeconds);
	
	public abstract void enableMotors();
	
	public abstract void disableMotors();

	public abstract void shutDown();
		
	@SuppressWarnings("unchecked")
	public static Map<String, String> getHardwareControllerClassMap() throws Exception { 
		List<Class<?>> classes = ClassScanner.getClassesForPackage("com.sketchy.hardware.impl", MetaDataObject.class);
		Map<String, String> classMap = new LinkedHashMap<String, String>();
		for (Class<?> clazz:classes){
			Class<HardwareControllerProperties> hardwareControllerClass = (Class<HardwareControllerProperties>) clazz;
			Class<HardwareController> implementationClass = (Class<HardwareController>) hardwareControllerClass.newInstance().getImplementationClass();
			classMap.put(implementationClass.getName(), clazz.getName());
		}
		return classMap;
	}
	
}
	
