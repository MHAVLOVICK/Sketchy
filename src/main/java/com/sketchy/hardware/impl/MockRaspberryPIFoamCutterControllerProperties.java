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

public class MockRaspberryPIFoamCutterControllerProperties extends HardwareControllerProperties {
	
	private static final long serialVersionUID = 7921693211849060599L;

	@Override
	public Class<? extends HardwareController> getImplementationClass() {
		return MockRaspberryPIFoamCutterController.class; 
	}
	
	@Override	
	public MetaData getMetaData() {
		
		MetaData metaData = new MetaData();
		metaData.setHelpUrl("/docs/RaspberryPIFoamCutterController.html");		
		
		MetaDataGroup frontLeftMotorGroup = new MetaDataGroup("Front Left Motor");
		frontLeftMotorGroup.add(new MetaDataProperty("frontLeftMotorStepPinNumber","Step Pin Number",AttributeType.List, RaspberryPiPins.pins));
		frontLeftMotorGroup.add(new MetaDataProperty("frontLeftMotorDirectionPinNumber","Direction Pin Number",AttributeType.List, RaspberryPiPins.pins));
		frontLeftMotorGroup.add(new MetaDataProperty("frontLeftMotorInvertDirection","Invert Direction",AttributeType.Boolean));
		frontLeftMotorGroup.add(new MetaDataProperty("frontLeftMotorMinStepPeriodInMicroseconds","Min Step Period(microseconds)",AttributeType.Number));
		frontLeftMotorGroup.add(new MetaDataProperty("frontLeftMotorForward","Motor Forward",AttributeType.Button));		
		frontLeftMotorGroup.add(new MetaDataProperty("frontLeftMotorBackward","Motor Backward",AttributeType.Button));		
		metaData.add(frontLeftMotorGroup);

		MetaDataGroup frontRightMotorGroup = new MetaDataGroup("Front Right Motor");
		frontRightMotorGroup.add(new MetaDataProperty("frontRightMotorStepPinNumber","Step Pin Number",AttributeType.List, RaspberryPiPins.pins));
		frontRightMotorGroup.add(new MetaDataProperty("frontRightMotorDirectionPinNumber","Direction Pin Number",AttributeType.List, RaspberryPiPins.pins));
		frontRightMotorGroup.add(new MetaDataProperty("frontRightMotorInvertDirection","Invert Direction",AttributeType.Boolean));
		frontRightMotorGroup.add(new MetaDataProperty("frontRightMotorMinStepPeriodInMicroseconds","Min Step Period(microseconds)",AttributeType.Number));
		frontRightMotorGroup.add(new MetaDataProperty("frontRightMotorForward","Motor Forward",AttributeType.Button));		
		frontRightMotorGroup.add(new MetaDataProperty("frontRightMotorBackward","Motor Backward",AttributeType.Button));		
		metaData.add(frontRightMotorGroup);

		
		MetaDataGroup backLeftMotorGroup = new MetaDataGroup("Back Left Motor");
		backLeftMotorGroup.add(new MetaDataProperty("backLeftMotorStepPinNumber","Step Pin Number",AttributeType.List, RaspberryPiPins.pins));
		backLeftMotorGroup.add(new MetaDataProperty("backLeftMotorDirectionPinNumber","Direction Pin Number",AttributeType.List, RaspberryPiPins.pins));
		backLeftMotorGroup.add(new MetaDataProperty("backLeftMotorInvertDirection","Invert Direction",AttributeType.Boolean));
		backLeftMotorGroup.add(new MetaDataProperty("backLeftMotorMinStepPeriodInMicroseconds","Min Step Period(microseconds)",AttributeType.Number));
		backLeftMotorGroup.add(new MetaDataProperty("backLeftMotorForward","Motor Forward",AttributeType.Button));		
		backLeftMotorGroup.add(new MetaDataProperty("backLeftMotorBackward","Motor Backward",AttributeType.Button));		
		metaData.add(backLeftMotorGroup);

		MetaDataGroup backRightMotorGroup = new MetaDataGroup("Back Right Motor");
		backRightMotorGroup.add(new MetaDataProperty("backRightMotorStepPinNumber","Step Pin Number",AttributeType.List, RaspberryPiPins.pins));
		backRightMotorGroup.add(new MetaDataProperty("backRightMotorDirectionPinNumber","Direction Pin Number",AttributeType.List, RaspberryPiPins.pins));
		backRightMotorGroup.add(new MetaDataProperty("backRightMotorInvertDirection","Invert Direction",AttributeType.Boolean));
		backRightMotorGroup.add(new MetaDataProperty("backRightMotorMinStepPeriodInMicroseconds","Min Step Period(microseconds)",AttributeType.Number));
		backRightMotorGroup.add(new MetaDataProperty("backRightMotorForward","Motor Forward",AttributeType.Button));		
		backRightMotorGroup.add(new MetaDataProperty("backRightMotorBackward","Motor Backward",AttributeType.Button));		
		metaData.add(backRightMotorGroup);
		
		return metaData;
	}
	
		
	private int frontLeftMotorStepPinNumber=24;
	private int frontLeftMotorDirectionPinNumber=25;
	private boolean frontLeftMotorInvertDirection=false;
	private long frontLeftMotorMinStepPeriodInMicroseconds=150;
	
	private int frontRightMotorStepPinNumber=27;
	private int frontRightMotorDirectionPinNumber=22;
	private boolean frontRightMotorInvertDirection=true;
	private long frontRightMotorMinStepPeriodInMicroseconds=150;

	private int backLeftMotorStepPinNumber=24;
	private int backLeftMotorDirectionPinNumber=25;
	private boolean backLeftMotorInvertDirection=false;
	private long backLeftMotorMinStepPeriodInMicroseconds=150;
	
	private int backRightMotorStepPinNumber=27;
	private int backRightMotorDirectionPinNumber=22;
	private boolean backRightMotorInvertDirection=true;
	private long backRightMotorMinStepPeriodInMicroseconds=150;

	public int getFrontLeftMotorStepPinNumber() {
		return frontLeftMotorStepPinNumber;
	}

	public void setFrontLeftMotorStepPinNumber(int frontLeftMotorStepPinNumber) {
		this.frontLeftMotorStepPinNumber = frontLeftMotorStepPinNumber;
	}

	public int getFrontLeftMotorDirectionPinNumber() {
		return frontLeftMotorDirectionPinNumber;
	}

	public void setFrontLeftMotorDirectionPinNumber(int frontLeftMotorDirectionPinNumber) {
		this.frontLeftMotorDirectionPinNumber = frontLeftMotorDirectionPinNumber;
	}

	public boolean isFrontLeftMotorInvertDirection() {
		return frontLeftMotorInvertDirection;
	}

	public void setFrontLeftMotorInvertDirection(boolean frontLeftMotorInvertDirection) {
		this.frontLeftMotorInvertDirection = frontLeftMotorInvertDirection;
	}

	public long getFrontLeftMotorMinStepPeriodInMicroseconds() {
		return frontLeftMotorMinStepPeriodInMicroseconds;
	}

	public void setFrontLeftMotorMinStepPeriodInMicroseconds(long frontLeftMotorMinStepPeriodInMicroseconds) {
		this.frontLeftMotorMinStepPeriodInMicroseconds = frontLeftMotorMinStepPeriodInMicroseconds;
	}

	public int getFrontRightMotorStepPinNumber() {
		return frontRightMotorStepPinNumber;
	}

	public void setFrontRightMotorStepPinNumber(int frontRightMotorStepPinNumber) {
		this.frontRightMotorStepPinNumber = frontRightMotorStepPinNumber;
	}

	public int getFrontRightMotorDirectionPinNumber() {
		return frontRightMotorDirectionPinNumber;
	}

	public void setFrontRightMotorDirectionPinNumber(int frontRightMotorDirectionPinNumber) {
		this.frontRightMotorDirectionPinNumber = frontRightMotorDirectionPinNumber;
	}

	public boolean isFrontRightMotorInvertDirection() {
		return frontRightMotorInvertDirection;
	}

	public void setFrontRightMotorInvertDirection(boolean frontRightMotorInvertDirection) {
		this.frontRightMotorInvertDirection = frontRightMotorInvertDirection;
	}

	public long getFrontRightMotorMinStepPeriodInMicroseconds() {
		return frontRightMotorMinStepPeriodInMicroseconds;
	}

	public void setFrontRightMotorMinStepPeriodInMicroseconds(long frontRightMotorMinStepPeriodInMicroseconds) {
		this.frontRightMotorMinStepPeriodInMicroseconds = frontRightMotorMinStepPeriodInMicroseconds;
	}

	public int getBackLeftMotorStepPinNumber() {
		return backLeftMotorStepPinNumber;
	}

	public void setBackLeftMotorStepPinNumber(int backLeftMotorStepPinNumber) {
		this.backLeftMotorStepPinNumber = backLeftMotorStepPinNumber;
	}

	public int getBackLeftMotorDirectionPinNumber() {
		return backLeftMotorDirectionPinNumber;
	}

	public void setBackLeftMotorDirectionPinNumber(int backLeftMotorDirectionPinNumber) {
		this.backLeftMotorDirectionPinNumber = backLeftMotorDirectionPinNumber;
	}

	public boolean isBackLeftMotorInvertDirection() {
		return backLeftMotorInvertDirection;
	}

	public void setBackLeftMotorInvertDirection(boolean backLeftMotorInvertDirection) {
		this.backLeftMotorInvertDirection = backLeftMotorInvertDirection;
	}

	public long getBackLeftMotorMinStepPeriodInMicroseconds() {
		return backLeftMotorMinStepPeriodInMicroseconds;
	}

	public void setBackLeftMotorMinStepPeriodInMicroseconds(long backLeftMotorMinStepPeriodInMicroseconds) {
		this.backLeftMotorMinStepPeriodInMicroseconds = backLeftMotorMinStepPeriodInMicroseconds;
	}

	public int getBackRightMotorStepPinNumber() {
		return backRightMotorStepPinNumber;
	}

	public void setBackRightMotorStepPinNumber(int backRightMotorStepPinNumber) {
		this.backRightMotorStepPinNumber = backRightMotorStepPinNumber;
	}

	public int getBackRightMotorDirectionPinNumber() {
		return backRightMotorDirectionPinNumber;
	}

	public void setBackRightMotorDirectionPinNumber(int backRightMotorDirectionPinNumber) {
		this.backRightMotorDirectionPinNumber = backRightMotorDirectionPinNumber;
	}

	public boolean isBackRightMotorInvertDirection() {
		return backRightMotorInvertDirection;
	}

	public void setBackRightMotorInvertDirection(boolean backRightMotorInvertDirection) {
		this.backRightMotorInvertDirection = backRightMotorInvertDirection;
	}

	public long getBackRightMotorMinStepPeriodInMicroseconds() {
		return backRightMotorMinStepPeriodInMicroseconds;
	}

	public void setBackRightMotorMinStepPeriodInMicroseconds(long backRightMotorMinStepPeriodInMicroseconds) {
		this.backRightMotorMinStepPeriodInMicroseconds = backRightMotorMinStepPeriodInMicroseconds;
	}
	
}
