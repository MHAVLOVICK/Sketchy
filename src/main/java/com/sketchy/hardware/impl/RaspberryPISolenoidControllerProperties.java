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

public class RaspberryPISolenoidControllerProperties extends HardwareControllerProperties {

	private static final long serialVersionUID = 7043702864264487942L;

	@Override
	public Class<? extends HardwareController> getImplementationClass() {
		return RaspberryPISolenoidController.class; 
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
		leftMotorGroup.add(new MetaDataProperty("leftMotorForward","Motor Forward (1000 steps)",AttributeType.Button));		
		leftMotorGroup.add(new MetaDataProperty("leftMotorBackward","Motor Backward (1000 steps)",AttributeType.Button));			
		metaData.add(leftMotorGroup);

		MetaDataGroup rightMotorGroup = new MetaDataGroup("Right Motor");
		rightMotorGroup.add(new MetaDataProperty("rightMotorStepPinNumber","Step Pin Number",AttributeType.List, RaspberryPiPins.pins));
		rightMotorGroup.add(new MetaDataProperty("rightMotorDirectionPinNumber","Direction Pin Number",AttributeType.List, RaspberryPiPins.pins));
		rightMotorGroup.add(new MetaDataProperty("rightMotorEnablePinNumber","Enable Pin Number",AttributeType.List, RaspberryPiPins.pins));
		rightMotorGroup.add(new MetaDataProperty("rightMotorInvertDirection","Invert Direction",AttributeType.Boolean));
		rightMotorGroup.add(new MetaDataProperty("rightMotorMinStepPeriodInMicroseconds","Min Step Period(microseconds)",AttributeType.Number));
		rightMotorGroup.add(new MetaDataProperty("rightMotorForward","Motor Forward (1000 steps)",AttributeType.Button));		
		rightMotorGroup.add(new MetaDataProperty("rightMotorBackward","Motor Backward (1000 steps)",AttributeType.Button));			
		metaData.add(rightMotorGroup);

		MetaDataGroup penGroup = new MetaDataGroup("Pen");
		penGroup.add(new MetaDataProperty("penPinNumber","Pin Number",AttributeType.List, RaspberryPiPins.pins));
		penGroup.add(new MetaDataProperty("penDownPeriodInMicroseconds","Down Stable Period(microseconds)",AttributeType.Number));
		penGroup.add(new MetaDataProperty("penUpPeriodInMicroseconds","Up Stable Period (microseconds)",AttributeType.Number));
		penGroup.add(new MetaDataProperty("penUpPowerLevel","Power Level(0-100)",AttributeType.Number));
		penGroup.add(new MetaDataProperty("penUpHoldPowerLevel","Hold Power Level(0-100)",AttributeType.Number));
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
	
	private int penPinNumber=18;
	private long penDownPeriodInMicroseconds=100000;
	private long penUpPeriodInMicroseconds=50000;
	private int penUpPowerLevel=30;
	private int penUpHoldPowerLevel=20;

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

	public long getPenDownPeriodInMicroseconds() {
		return penDownPeriodInMicroseconds;
	}

	public void setPenDownPeriodInMicroseconds(long penDownPeriodInMicroseconds) {
		this.penDownPeriodInMicroseconds = penDownPeriodInMicroseconds;
	}

	public long getPenUpPeriodInMicroseconds() {
		return penUpPeriodInMicroseconds;
	}

	public void setPenUpPeriodInMicroseconds(long penUpPeriodInMicroseconds) {
		this.penUpPeriodInMicroseconds = penUpPeriodInMicroseconds;
	}

	public int getPenUpPowerLevel() {
		return penUpPowerLevel;
	}

	public void setPenUpPowerLevel(int penUpPowerLevel) {
		this.penUpPowerLevel = penUpPowerLevel;
	}

	public int getPenUpHoldPowerLevel() {
		return penUpHoldPowerLevel;
	}

	public void setPenUpHoldPowerLevel(int penUpHoldPowerLevel) {
		this.penUpHoldPowerLevel = penUpHoldPowerLevel;
	}


}
