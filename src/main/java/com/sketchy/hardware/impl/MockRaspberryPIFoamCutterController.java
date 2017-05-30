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

package com.sketchy.hardware.impl;

import com.sketchy.hardware.Direction;
import com.sketchy.hardware.HardwareController;


public class MockRaspberryPIFoamCutterController extends HardwareController {
	
	private long lastMotorTimeNanos=0;
	private MockRaspberryPIFoamCutterControllerProperties properties = null;
	
	public MockRaspberryPIFoamCutterController(MockRaspberryPIFoamCutterControllerProperties properties) {
		super(properties);
		this.properties=properties;
		setupPins();
	}
	
	@Override
	public MockRaspberryPIFoamCutterControllerProperties getProperties(){
		return properties;
	}
	
	public synchronized void stepMotors(Direction frontLeftMotorDirection, Direction frontRightMotorDirection, Direction backLeftMotorDirection, Direction backRightMotorDirection, long delayInMicroSeconds){
		// take the Max of any of the delays: Minimum Left, Minimum Right, or delayInMicroSeconds
		if ((frontLeftMotorDirection==Direction.NONE) && (frontRightMotorDirection==Direction.NONE)
			&& (backLeftMotorDirection==Direction.NONE)&& (backRightMotorDirection==Direction.NONE)) return;
		
		long minDelayInMicroseconds = 0;

		if (frontLeftMotorDirection!=Direction.NONE){ // only need to get minimum if moving
			minDelayInMicroseconds = properties.getFrontLeftMotorMinStepPeriodInMicroseconds();
		}
		if (frontRightMotorDirection!=Direction.NONE){ // only need to get minimum if moving
			minDelayInMicroseconds = Math.max(minDelayInMicroseconds, properties.getFrontRightMotorMinStepPeriodInMicroseconds());
		}

		if (backLeftMotorDirection!=Direction.NONE){ // only need to get minimum if moving
			minDelayInMicroseconds = Math.max(minDelayInMicroseconds, properties.getBackLeftMotorMinStepPeriodInMicroseconds());
		}

		if (backRightMotorDirection!=Direction.NONE){ // only need to get minimum if moving
			minDelayInMicroseconds = Math.max(minDelayInMicroseconds, properties.getBackRightMotorMinStepPeriodInMicroseconds());
		}
		
		delayInMicroSeconds = Math.max(delayInMicroSeconds,minDelayInMicroseconds);
		
		long delay = delayInMicroSeconds-((System.nanoTime()-lastMotorTimeNanos)/1000);
		if (delay>0){
			//Gpio.delayMicroseconds(delay);
		}
		if (frontLeftMotorDirection!=Direction.NONE){
			if (properties.isFrontLeftMotorInvertDirection() == (frontLeftMotorDirection==Direction.FORWARD)){
			//	Gpio.digitalWrite(properties.getFrontLeftMotorDirectionPinNumber(), true);
			} else {
			//	Gpio.digitalWrite(properties.getFrontLeftMotorDirectionPinNumber(), false);
			}
		//	Gpio.delayMicroseconds(2);
		//	Gpio.digitalWrite(properties.getFrontLeftMotorStepPinNumber(), true);
		//	Gpio.delayMicroseconds(2);
			//Gpio.digitalWrite(properties.getFrontLeftMotorStepPinNumber(), false);
		}

		if (frontRightMotorDirection!=Direction.NONE){
			if (properties.isFrontRightMotorInvertDirection() == (frontRightMotorDirection==Direction.FORWARD)){
		//		Gpio.digitalWrite(properties.getFrontRightMotorDirectionPinNumber(), true);
			} else {
		//		Gpio.digitalWrite(properties.getFrontRightMotorDirectionPinNumber(), false);
			}
		//	Gpio.delayMicroseconds(2);
		//	Gpio.digitalWrite(properties.getFrontRightMotorStepPinNumber(), true);
		//	Gpio.delayMicroseconds(2);
		//	Gpio.digitalWrite(properties.getFrontRightMotorStepPinNumber(), false);
		}
		
		if (backLeftMotorDirection!=Direction.NONE){
			if (properties.isBackLeftMotorInvertDirection() == (backLeftMotorDirection==Direction.FORWARD)){
		//		Gpio.digitalWrite(properties.getBackLeftMotorDirectionPinNumber(), true);
			} else {
		//		Gpio.digitalWrite(properties.getBackLeftMotorDirectionPinNumber(), false);
			}
		////	Gpio.delayMicroseconds(2);
		//	Gpio.digitalWrite(properties.getBackLeftMotorStepPinNumber(), true);
		//	Gpio.delayMicroseconds(2);
		//	Gpio.digitalWrite(properties.getBackLeftMotorStepPinNumber(), false);
		}

		if (backRightMotorDirection!=Direction.NONE){
			if (properties.isBackRightMotorInvertDirection() == (backRightMotorDirection==Direction.FORWARD)){
		//		Gpio.digitalWrite(properties.getBackRightMotorDirectionPinNumber(), true);
			} else {
		//		Gpio.digitalWrite(properties.getBackRightMotorDirectionPinNumber(), false);
			}
		//	Gpio.delayMicroseconds(2);
		//	Gpio.digitalWrite(properties.getBackRightMotorStepPinNumber(), true);
		//	Gpio.delayMicroseconds(2);
		//	Gpio.digitalWrite(properties.getBackRightMotorStepPinNumber(), false);
		}

		lastMotorTimeNanos=System.nanoTime();
	}
	
	@Override
	public synchronized void enableMotors() {
	}

	@Override
	public synchronized void disableMotors() {
	}

	@Override
	public synchronized void shutDown() {
		System.out.println("RaspberryPIFoamCutterController: shutDown.");
		tearDownPins();
	}

	private synchronized void setupPins() {
		int retryCount=0;
		while(true){
			try{
				int ret = 0;//Gpio.wiringPiSetupGpio();
				if (ret==0) break;
				System.out.println("Error Initializing GPIO!");
			} catch (Throwable t){
				System.out.println("Error Initializing GPIO! " + toString());
			}
			if (retryCount>3){
				throw new RuntimeException("Count not Initialize GPIO!");
			}
			System.out.println("Trying again!");
			retryCount++;
			try{Thread.sleep(500);} catch (Exception e){}
		}

	/*	Gpio.pinMode(properties.getFrontLeftMotorDirectionPinNumber(), Gpio.OUTPUT);
		Gpio.digitalWrite(properties.getFrontLeftMotorDirectionPinNumber(), false);
			
		Gpio.pinMode(properties.getFrontLeftMotorStepPinNumber(), Gpio.OUTPUT);
		Gpio.digitalWrite(properties.getFrontLeftMotorStepPinNumber(), false);

		Gpio.pinMode(properties.getBackLeftMotorDirectionPinNumber(), Gpio.OUTPUT);
		Gpio.digitalWrite(properties.getBackLeftMotorDirectionPinNumber(), false);

		Gpio.pinMode(properties.getBackLeftMotorStepPinNumber(), Gpio.OUTPUT);
		Gpio.digitalWrite(properties.getBackLeftMotorStepPinNumber(), false);

		Gpio.pinMode(properties.getFrontRightMotorDirectionPinNumber(), Gpio.OUTPUT);
		Gpio.digitalWrite(properties.getFrontRightMotorDirectionPinNumber(), false);
	
		Gpio.pinMode(properties.getFrontRightMotorStepPinNumber(), Gpio.OUTPUT);
		Gpio.digitalWrite(properties.getFrontRightMotorStepPinNumber(), false);

		Gpio.pinMode(properties.getBackRightMotorDirectionPinNumber(), Gpio.OUTPUT);
		Gpio.digitalWrite(properties.getBackRightMotorDirectionPinNumber(), false);
	
		Gpio.pinMode(properties.getBackRightMotorStepPinNumber(), Gpio.OUTPUT);
		Gpio.digitalWrite(properties.getBackRightMotorStepPinNumber(), false);
*/
	}
	
	private synchronized void tearDownPins(){
		// turn off pen
	/*
		Gpio.digitalWrite(properties.getFrontLeftMotorDirectionPinNumber(), false); // turn off old pin			
		Gpio.digitalWrite(properties.getFrontLeftMotorStepPinNumber(), false); // turn off old pin	
		Gpio.digitalWrite(properties.getFrontRightMotorDirectionPinNumber(), false); // turn off old pin			
		Gpio.digitalWrite(properties.getFrontRightMotorStepPinNumber(), false); // turn off old pin	
		Gpio.digitalWrite(properties.getBackLeftMotorDirectionPinNumber(), false); // turn off old pin			
		Gpio.digitalWrite(properties.getBackLeftMotorStepPinNumber(), false); // turn off old pin	
		Gpio.digitalWrite(properties.getBackRightMotorDirectionPinNumber(), false); // turn off old pin			
		Gpio.digitalWrite(properties.getBackRightMotorStepPinNumber(), false); // turn off old pin
		*/	
	}
	
	
}
