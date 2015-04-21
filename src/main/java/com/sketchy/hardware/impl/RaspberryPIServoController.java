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

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.pi4j.wiringpi.Gpio;
import com.sketchy.hardware.Direction;
import com.sketchy.hardware.HardwareController;


public class RaspberryPIServoController extends HardwareController {
	
	private long lastMotorTimeNanos=0;
	
	// we don't know the current state. Don't set until Pen Up/Down methods are called
	private int lastPosition=-1;
	
	// Use StringBuffer for pen commands to reduce object instantiation
	private static StringBuffer sbuf = new StringBuffer();
	
	private RaspberryPIServoControllerProperties properties = null;
	

	private static final int SERVO_MIN_VALUE=1000;  // No less than 1.0ms
	private static final int SERVO_MAX_VALUE=2000;  // No more than 2.0ms
	private File servoPipeFile = null;

	public RaspberryPIServoController(RaspberryPIServoControllerProperties properties) {
		super(properties);
		this.properties=properties;
		setupPins();
	}
	
	@Override
	public RaspberryPIServoControllerProperties getProperties(){
		return properties;
	}
	
	public synchronized void penUp(){
		if (properties.getPenUpPosition()!=lastPosition){
			try{
				int penUpValue=(int) ((SERVO_MAX_VALUE-SERVO_MIN_VALUE)/100.0*properties.getPenUpPosition()+SERVO_MIN_VALUE);
				String servoPenUpCommand = buildServoCommand(properties.getPenPinNumber(), penUpValue);
				FileUtils.writeStringToFile(servoPipeFile, servoPenUpCommand);
			} catch (Exception e){
				throw new RuntimeException(e.getMessage());
			}
			Gpio.delayMicroseconds(properties.getPenUpPeriodInMicroseconds());
			lastPosition=properties.getPenUpPosition();
		}
	}
	
	public synchronized void penDown(){
		if (properties.getPenDownPosition()!=lastPosition){
			try{
				int penDownValue=(int) ((SERVO_MAX_VALUE-SERVO_MIN_VALUE)/100.0*properties.getPenDownPosition()+SERVO_MIN_VALUE);
				String servoPenDownCommand = buildServoCommand(properties.getPenPinNumber(), penDownValue);
				FileUtils.writeStringToFile(servoPipeFile, servoPenDownCommand);
			} catch (Exception e){
				throw new RuntimeException(e.getMessage());
			}
			Gpio.delayMicroseconds(properties.getPenDownPeriodInMicroseconds());
			lastPosition=properties.getPenDownPosition();
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
		System.out.println("RaspberryPIServoController: shutDown.");
		tearDownPins();
	}

	private synchronized static String buildServoCommand(int pinNumber, int value){
		if (sbuf.length()>0) sbuf.delete(0, sbuf.length()-1);
		sbuf.append("s ").append(Integer.toString(pinNumber)).append(" ").append(Integer.toString(value)).append(System.lineSeparator());
		return sbuf.toString();
	}


	private synchronized void setupPins() {
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
		
		servoPipeFile = new File(properties.getServoDevicePipe());
		if ((!servoPipeFile.exists()) || (!servoPipeFile.canWrite())){
			throw new RuntimeException("Can not write to Servo Device Pipe: " + properties.getServoDevicePipe() + "! Make sure the 'pigpiod' service is running!");
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
		// turn off pen
		try{
			String servoPenOffCommand = buildServoCommand(properties.getPenPinNumber(), 0);
			FileUtils.writeStringToFile(servoPipeFile, servoPenOffCommand);
		} catch (Exception e){
			throw new RuntimeException(e.getMessage());
		}
		
		Gpio.pinMode(properties.getPenPinNumber(), Gpio.OUTPUT);
		Gpio.digitalWrite(properties.getLeftMotorEnablePinNumber(), false); // turn off old pin
		Gpio.digitalWrite(properties.getLeftMotorDirectionPinNumber(), false); // turn off old pin			
		Gpio.digitalWrite(properties.getLeftMotorStepPinNumber(), false); // turn off old pin	
		Gpio.digitalWrite(properties.getRightMotorEnablePinNumber(), false); // turn off old pin
		Gpio.digitalWrite(properties.getRightMotorDirectionPinNumber(), false); // turn off old pin			
		Gpio.digitalWrite(properties.getRightMotorStepPinNumber(), false); // turn off old pin	
	}
	
	
}
