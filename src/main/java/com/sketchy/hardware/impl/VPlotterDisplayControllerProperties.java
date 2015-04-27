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

public class VPlotterDisplayControllerProperties extends HardwareControllerProperties {
	
	private static final long serialVersionUID = -8825894320870849739L;

	@Override
	public Class<? extends HardwareController> getImplementationClass() {
		return VPlotterDisplayController.class; 
	}
	
	@Override	
	public MetaData getMetaData() {
		
		MetaData metaData = new MetaData();
		metaData.setHelpImage("/images/VPlotterDisplayController.png");

		MetaDataGroup displayGroup = new MetaDataGroup("Display");
		displayGroup.add(new MetaDataProperty("windowWidth","Window Width",AttributeType.Number));
		displayGroup.add(new MetaDataProperty("windowHeight","Window Height",AttributeType.Number));
		metaData.add(displayGroup);
		
		MetaDataGroup canvasGroup = new MetaDataGroup("Display Properties");
		canvasGroup.add(new MetaDataProperty("frameWidth","Frame Width",AttributeType.Decimal));
		canvasGroup.add(new MetaDataProperty("frameHeight","Frame Height",AttributeType.Decimal));
		canvasGroup.add(new MetaDataProperty("canvasWidth","Canvas Width",AttributeType.Decimal));
		canvasGroup.add(new MetaDataProperty("canvasHeight","Canvas Height",AttributeType.Decimal));
		canvasGroup.add(new MetaDataProperty("yPosOffset","Home Y Position Offset",AttributeType.Decimal,true));
		metaData.add(canvasGroup);

		MetaDataGroup leftMotorGroup = new MetaDataGroup("Left Motor");
		leftMotorGroup.add(new MetaDataProperty("leftMotorStepsPerMM","Steps Per Millimeter",AttributeType.Decimal));
		metaData.add(leftMotorGroup);

		MetaDataGroup rightMotorGroup = new MetaDataGroup("Right Motor");
		rightMotorGroup.add(new MetaDataProperty("rightMotorStepsPerMM","Steps Per Millimeter",AttributeType.Decimal));
		metaData.add(rightMotorGroup);
			
		return metaData;
	}
	
	private int windowWidth=800;
	private int windowHeight=600;
	
	private double yPosOffset=-20;
	
	private double frameWidth=375;  
	private double frameHeight=450; 

	private double canvasWidth=240;
	private double canvasHeight=280;

	private double leftMotorStepsPerMM=32.5;
	private double rightMotorStepsPerMM=32.5;

	public int getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	public int getWindowHeight() {
		return windowHeight;
	}

	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}

	public double getyPosOffset() {
		return yPosOffset;
	}

	public void setyPosOffset(double yPosOffset) {
		this.yPosOffset = yPosOffset;
	}

	public double getFrameWidth() {
		return frameWidth;
	}

	public void setFrameWidth(double frameWidth) {
		this.frameWidth = frameWidth;
	}

	public double getFrameHeight() {
		return frameHeight;
	}

	public void setFrameHeight(double frameHeight) {
		this.frameHeight = frameHeight;
	}

	public double getCanvasWidth() {
		return canvasWidth;
	}

	public void setCanvasWidth(double canvasWidth) {
		this.canvasWidth = canvasWidth;
	}

	public double getCanvasHeight() {
		return canvasHeight;
	}

	public void setCanvasHeight(double canvasHeight) {
		this.canvasHeight = canvasHeight;
	}

	public double getLeftMotorStepsPerMM() {
		return leftMotorStepsPerMM;
	}

	public void setLeftMotorStepsPerMM(double leftMotorStepsPerMM) {
		this.leftMotorStepsPerMM = leftMotorStepsPerMM;
	}

	public double getRightMotorStepsPerMM() {
		return rightMotorStepsPerMM;
	}

	public void setRightMotorStepsPerMM(double rightMotorStepsPerMM) {
		this.rightMotorStepsPerMM = rightMotorStepsPerMM;
	}
}
