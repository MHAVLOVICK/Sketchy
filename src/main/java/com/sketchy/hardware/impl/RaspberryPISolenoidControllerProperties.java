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

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftPwm;
import com.sketchy.hardware.Direction;
import com.sketchy.hardware.HardwareController;

public class RaspberryPISolenoidControllerProperties extends HardwareController {
	
	private static int PWM_RANGE=100;

	private long lastMotorTimeNanos=0;
	
	private boolean isPenDown=true;
	
	private RaspberryPISolenoidController properties = null;
	
	public RaspberryPISolenoidControllerProperties(RaspberryPISolenoidController properties) {
		super(properties);
		this.properties=properties;
		setupPins();
	}
	
	@Override
	public RaspberryPISolenoidController getProperties(){
		return properties;
	}
	
	public synchronized void penUp(){
		if (isPenDown){
			SoftPwm.softPwmWrite(properties.getPenPinNumber(), properties.getPenUpPowerLevel());
			Gpio.delayMicroseconds(properties.getPenUpPeriodInMicroseconds());
			SoftPwm.softPwmWrite(properties.getPenPinNumber(), properties.getPenUpHoldPowerLevel());
			isPenDown=false;
		}
	}
	
	public synchronized void penDown(){
		if (!isPenDown){
			SoftPwm.softPwmWrite(properties.getPenPinNumber(), 0); // off
			Gpio.delayMicroseconds(properties.getPenDownPeriodInMicroseconds());
			isPenDown=true;
		}
	}
	
	public synchronized void stepMotors(Direction leftMotorDirection, Direction rightMotorDirection, long delayInMicroSeconds){
		// take the Max of any of the delays: Minimum Left, Minimum Right, or delayInMicroSeconds
		if ((leftMotorDirection==Direction.NONE) && (rightMotorDirection==Direction.NONE)) return;
		long minDelayInMicroseconds = 0;
		if (leftMotorDirection!=Direction.NONE){ // only need to get minimum if moving
			minDelayInMicroseconds = properties.getLeftMotorMinStepPeriodInMicroseconds();
		}
		if (rightMotorDirection!=Direction.NONE){ // only need to get minimum if moving
			minDelayInMicroseconds = Math.max(minDelayInMicroseconds, properties.getRightMotorMinStepPeriodInMicroseconds());
		}
		delayInMicroSeconds = Math.max(delayInMicroSeconds,minDelayInMicroseconds);
		
		long delay = delayInMicroSeconds-((System.nanoTime()-lastMotorTimeNanos)/1000);
		if (delay>0){
			Gpio.delayMicroseconds(delay);
		}
		if (leftMotorDirection!=Direction.NONE){
			if (properties.isLeftMotorInvertDirection() == (leftMotorDirection==Direction.FORWARD)){
				Gpio.digitalWrite(properties.getLeftMotorDirectionPinNumber(), true);
			} else {
				Gpio.digitalWrite(properties.getLeftMotorDirectionPinNumber(), false);
			}
			Gpio.digitalWrite(properties.getLeftMotorStepPinNumber(), true);
			Gpio.digitalWrite(properties.getLeftMotorStepPinNumber(), false);
		}

		if (rightMotorDirection!=Direction.NONE){
			if (properties.isRightMotorInvertDirection() == (rightMotorDirection==Direction.FORWARD)){
				Gpio.digitalWrite(properties.getRightMotorDirectionPinNumber(), true);
			} else {
				Gpio.digitalWrite(properties.getRightMotorDirectionPinNumber(), false);
			}
			Gpio.digitalWrite(properties.getRightMotorStepPinNumber(), true);
			Gpio.digitalWrite(properties.getRightMotorStepPinNumber(), false);
		}
		
		lastMotorTimeNanos=System.nanoTime();
	}
	
	@Override
	public synchronized void enableMotors() {
		Gpio.digitalWrite(properties.getLeftMotorEnablePinNumber(), false);
		Gpio.digitalWrite(properties.getRightMotorEnablePinNumber(), false);
	}

	@Override
	public synchronized void disableMotors() {
		Gpio.digitalWrite(properties.getLeftMotorEnablePinNumber(), true);
		Gpio.digitalWrite(properties.getRightMotorEnablePinNumber(), true);		
	}

	@Override
	public synchronized void shutDown() {
		// ensure Pen is down. If using a solenoid, we don't want it to burn up
		penDown();
		tearDownPins();
	}

	private synchronized void setupPins(){
		int retryCount=0;
		while(true){
			try{
				int ret = Gpio.wiringPiSetupGpio();
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
		
		int ret = SoftPwm.softPwmCreate(properties.getPenPinNumber(), 0, PWM_RANGE);
		if (ret!=0){
			throw new RuntimeException("Error Creating PWM Pin!");
		}
			
		Gpio.pinMode(properties.getLeftMotorEnablePinNumber(), Gpio.OUTPUT);
		Gpio.digitalWrite(properties.getLeftMotorEnablePinNumber(), false);

		Gpio.pinMode(properties.getLeftMotorDirectionPinNumber(), Gpio.OUTPUT);
		Gpio.digitalWrite(properties.getLeftMotorDirectionPinNumber(), false);
			
		Gpio.pinMode(properties.getLeftMotorStepPinNumber(), Gpio.OUTPUT);
		Gpio.digitalWrite(properties.getLeftMotorStepPinNumber(), false);
			
		Gpio.pinMode(properties.getRightMotorEnablePinNumber(), Gpio.OUTPUT);
		Gpio.digitalWrite(properties.getRightMotorEnablePinNumber(), false);

		Gpio.pinMode(properties.getRightMotorDirectionPinNumber(), Gpio.OUTPUT);
		Gpio.digitalWrite(properties.getRightMotorDirectionPinNumber(), false);
	
		Gpio.pinMode(properties.getRightMotorStepPinNumber(), Gpio.OUTPUT);
		Gpio.digitalWrite(properties.getRightMotorStepPinNumber(), false);
	}
	
	private synchronized void tearDownPins(){
		SoftPwm.softPwmWrite(properties.getPenPinNumber(), 0);
		// Set pinMode to Output to reset/PWM Pin so it doesn't fail if setup again
		Gpio.pinMode(properties.getPenPinNumber(), Gpio.OUTPUT);
		Gpio.digitalWrite(properties.getLeftMotorEnablePinNumber(), false); // turn off old pin
		Gpio.digitalWrite(properties.getLeftMotorDirectionPinNumber(), false); // turn off old pin			
		Gpio.digitalWrite(properties.getLeftMotorStepPinNumber(), false); // turn off old pin	
		Gpio.digitalWrite(properties.getRightMotorEnablePinNumber(), false); // turn off old pin
		Gpio.digitalWrite(properties.getRightMotorDirectionPinNumber(), false); // turn off old pin			
		Gpio.digitalWrite(properties.getRightMotorStepPinNumber(), false); // turn off old pin	
	}
	
	
}
