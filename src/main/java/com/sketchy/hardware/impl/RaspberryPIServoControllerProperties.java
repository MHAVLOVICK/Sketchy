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

import com.sketchy.hardware.HardwareController;
import com.sketchy.hardware.HardwareControllerProperties;
import com.sketchy.metadata.MetaData;
import com.sketchy.metadata.MetaDataGroup;
import com.sketchy.metadata.MetaDataProperty;
import com.sketchy.metadata.MetaDataProperty.AttributeType;

public class RaspberryPIServoControllerProperties extends HardwareControllerProperties {
	
	private static final long serialVersionUID = 7921693211849060599L;

	@Override
	public Class<? extends HardwareController> getImplementationClass() {
		return RaspberryPIServoController.class; 
	}
	
	@Override	
	public MetaData getMetaData() {
		
		MetaData metaData = new MetaData();
		
		MetaDataGroup leftMotorGroup = new MetaDataGroup("Left Motor");
		leftMotorGroup.add(new MetaDataProperty("leftMotorStepPinNumber","Step Pin Number",AttributeType.List, RaspberryPiPins.pins));
		leftMotorGroup.add(new MetaDataProperty("leftMotorDirectionPinNumber","Direction Pin Number",AttributeType.List, RaspberryPiPins.pins));
		leftMotorGroup.add(new MetaDataProperty("leftMotorEnablePinNumber","Enable Pin Number",AttributeType.List, RaspberryPiPins.pins));
		leftMotorGroup.add(new MetaDataProperty("leftMotorInvertDirection","Invert Direction",AttributeType.Boolean));
		leftMotorGroup.add(new MetaDataProperty("leftMotorMinStepPeriodInMicroseconds","Min Step Period(microseconds)",AttributeType.Number));
		leftMotorGroup.add(new MetaDataProperty("leftMotorForward","Motor Forward",AttributeType.Button));		
		leftMotorGroup.add(new MetaDataProperty("leftMotorBackward","Motor Backward",AttributeType.Button));		
		metaData.add(leftMotorGroup);

		MetaDataGroup rightMotorGroup = new MetaDataGroup("Right Motor");
		rightMotorGroup.add(new MetaDataProperty("rightMotorStepPinNumber","Step Pin Number",AttributeType.List, RaspberryPiPins.pins));
		rightMotorGroup.add(new MetaDataProperty("rightMotorDirectionPinNumber","Direction Pin Number",AttributeType.List, RaspberryPiPins.pins));
		rightMotorGroup.add(new MetaDataProperty("rightMotorEnablePinNumber","Enable Pin Number",AttributeType.List, RaspberryPiPins.pins));
		rightMotorGroup.add(new MetaDataProperty("rightMotorInvertDirection","Invert Direction",AttributeType.Boolean));
		rightMotorGroup.add(new MetaDataProperty("rightMotorMinStepPeriodInMicroseconds","Min Step Period(microseconds)",AttributeType.Number));
		rightMotorGroup.add(new MetaDataProperty("rightMotorForward","Motor Forward",AttributeType.Button));		
		rightMotorGroup.add(new MetaDataProperty("rightMotorBackward","Motor Backward",AttributeType.Button));		
		metaData.add(rightMotorGroup);

		MetaDataGroup penGroup = new MetaDataGroup("Pen");
		penGroup.add(new MetaDataProperty("servoDevicePipe","Servo Device Pipe",AttributeType.String, true));

		penGroup.add(new MetaDataProperty("penPinNumber","Pin Number",AttributeType.List, RaspberryPiPins.pins));
		penGroup.add(new MetaDataProperty("penDownPeriodInMilliseconds","Down Stable Period(milliseconds)",AttributeType.Number));
		penGroup.add(new MetaDataProperty("penUpPeriodInMilliseconds","Up Stable Period (milliseconds)",AttributeType.Number));
		penGroup.add(new MetaDataProperty("penDownPosition","Pen Down Position (0-100)",AttributeType.Number));
		penGroup.add(new MetaDataProperty("penUpPosition","Pen Up Position (0-100)",AttributeType.Number));
		penGroup.add(new MetaDataProperty("penUp","Pen Up",AttributeType.Button));	
		penGroup.add(new MetaDataProperty("penDown","Pen Down",AttributeType.Button));	
		penGroup.add(new MetaDataProperty("penCycle","Cycle 5 Times",AttributeType.Button));	
		metaData.add(penGroup);
		
		return metaData;
	}
	
		
	private int leftMotorStepPinNumber=24;
	private int leftMotorDirectionPinNumber=25;
	private int leftMotorEnablePinNumber=23;
	private boolean leftMotorInvertDirection=false;
	private long leftMotorMinStepPeriodInMicroseconds=150;
	
	private int rightMotorStepPinNumber=27;
	private int rightMotorDirectionPinNumber=22;
	private int rightMotorEnablePinNumber=17;
	private boolean rightMotorInvertDirection=true;
	private long rightMotorMinStepPeriodInMicroseconds=150;
	
	private String servoDevicePipe = "/dev/pigpio";
	private int penPinNumber=18;
	private long penDownPeriodInMilliseconds=100;
	private long penUpPeriodInMilliseconds=100;
	private int penDownPosition=40;
	private int penUpPosition=60;

	public int getLeftMotorStepPinNumber() {
		return leftMotorStepPinNumber;
	}

	public void setLeftMotorStepPinNumber(int leftMotorStepPinNumber) {
		this.leftMotorStepPinNumber = leftMotorStepPinNumber;
	}

	public int getLeftMotorDirectionPinNumber() {
		return leftMotorDirectionPinNumber;
	}

	public void setLeftMotorDirectionPinNumber(int leftMotorDirectionPinNumber) {
		this.leftMotorDirectionPinNumber = leftMotorDirectionPinNumber;
	}

	public int getLeftMotorEnablePinNumber() {
		return leftMotorEnablePinNumber;
	}

	public void setLeftMotorEnablePinNumber(int leftMotorEnablePinNumber) {
		this.leftMotorEnablePinNumber = leftMotorEnablePinNumber;
	}

	public boolean isLeftMotorInvertDirection() {
		return leftMotorInvertDirection;
	}

	public void setLeftMotorInvertDirection(boolean leftMotorInvertDirection) {
		this.leftMotorInvertDirection = leftMotorInvertDirection;
	}

	public long getLeftMotorMinStepPeriodInMicroseconds() {
		return leftMotorMinStepPeriodInMicroseconds;
	}

	public void setLeftMotorMinStepPeriodInMicroseconds(
			long leftMotorMinStepPeriodInMicroseconds) {
		this.leftMotorMinStepPeriodInMicroseconds = leftMotorMinStepPeriodInMicroseconds;
	}

	public int getRightMotorStepPinNumber() {
		return rightMotorStepPinNumber;
	}

	public void setRightMotorStepPinNumber(int rightMotorStepPinNumber) {
		this.rightMotorStepPinNumber = rightMotorStepPinNumber;
	}

	public int getRightMotorDirectionPinNumber() {
		return rightMotorDirectionPinNumber;
	}

	public void setRightMotorDirectionPinNumber(int rightMotorDirectionPinNumber) {
		this.rightMotorDirectionPinNumber = rightMotorDirectionPinNumber;
	}

	public int getRightMotorEnablePinNumber() {
		return rightMotorEnablePinNumber;
	}

	public void setRightMotorEnablePinNumber(int rightMotorEnablePinNumber) {
		this.rightMotorEnablePinNumber = rightMotorEnablePinNumber;
	}

	public boolean isRightMotorInvertDirection() {
		return rightMotorInvertDirection;
	}

	public void setRightMotorInvertDirection(boolean rightMotorInvertDirection) {
		this.rightMotorInvertDirection = rightMotorInvertDirection;
	}

	public long getRightMotorMinStepPeriodInMicroseconds() {
		return rightMotorMinStepPeriodInMicroseconds;
	}

	public void setRightMotorMinStepPeriodInMicroseconds(
			long rightMotorMinStepPeriodInMicroseconds) {
		this.rightMotorMinStepPeriodInMicroseconds = rightMotorMinStepPeriodInMicroseconds;
	}

	public int getPenPinNumber() {
		return penPinNumber;
	}

	public void setPenPinNumber(int penPinNumber) {
		this.penPinNumber = penPinNumber;
	}

	public long getPenDownPeriodInMilliseconds() {
		return penDownPeriodInMilliseconds;
	}

	public void setPenDownPeriodInMilliseconds(long penDownPeriodInMilliseconds) {
		this.penDownPeriodInMilliseconds = penDownPeriodInMilliseconds;
	}

	public long getPenUpPeriodInMilliseconds() {
		return penUpPeriodInMilliseconds;
	}

	public void setPenUpPeriodInMilliseconds(long penUpPeriodInMilliseconds) {
		this.penUpPeriodInMilliseconds = penUpPeriodInMilliseconds;
	}

	public int getPenDownPosition() {
		return penDownPosition;
	}

	public void setPenDownPosition(int penDownPosition) {
		this.penDownPosition = penDownPosition;
	}

	public int getPenUpPosition() {
		return penUpPosition;
	}

	public void setPenUpPosition(int penUpPosition) {
		this.penUpPosition = penUpPosition;
	}

	public String getServoDevicePipe() {
		return servoDevicePipe;
	}

	public void setServoDevicePipe(String servoDevicePipe) {
		this.servoDevicePipe = servoDevicePipe;
	}
		
}
