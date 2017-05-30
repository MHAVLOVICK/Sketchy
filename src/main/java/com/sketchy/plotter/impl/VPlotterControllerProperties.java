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

package com.sketchy.plotter.impl;

import com.sketchy.metadata.MetaData;
import com.sketchy.metadata.MetaDataGroup;
import com.sketchy.metadata.MetaDataProperty;
import com.sketchy.metadata.MetaDataProperty.AttributeType;
import com.sketchy.plotter.PlotterController;
import com.sketchy.plotter.PlotterControllerProperties;

public class VPlotterControllerProperties extends PlotterControllerProperties {
	
	private static final long serialVersionUID = -5389046239887838950L;

	@Override
	public Class<? extends PlotterController> getImplementationClass() {
		return VPlotterController.class; 
	}
	
	@Override	
	public MetaData getMetaData() {
		
		MetaData metaData = new MetaData();
		
		metaData.setHelpUrl("/docs/VPlotter.html");
		
		MetaDataGroup frame = new MetaDataGroup("Frame");
		frame.add(new MetaDataProperty("frameWidth","Width",AttributeType.Decimal,true));
		frame.add(new MetaDataProperty("frameHeight","Height",AttributeType.Decimal,true));
		metaData.add(frame);

		MetaDataGroup canvas = new MetaDataGroup("Canvas");
		canvas.add(new MetaDataProperty("canvasWidth","Width",AttributeType.Decimal,true));
		canvas.add(new MetaDataProperty("canvasHeight","Height",AttributeType.Decimal,true));
		canvas.add(new MetaDataProperty("yPosOffset","Home Y Position Offset",AttributeType.Decimal,true));
		metaData.add(canvas);
		
		MetaDataGroup motors = new MetaDataGroup("Motors");
		motors.add(new MetaDataProperty("leftMotorStepsPerMM","Left Motor Steps/MM",AttributeType.Decimal,true));
		motors.add(new MetaDataProperty("rightMotorStepsPerMM","Right Motor Steps/MM",AttributeType.Decimal,true));
		motors.add(new MetaDataProperty("drawSpeed","Motor Draw Speed (MM/Sec)",AttributeType.Number,true));
		motors.add(new MetaDataProperty("moveSpeed","Motor Move Speed (MM/Sec)",AttributeType.Number,true));
		motors.add(new MetaDataProperty("disableMotors","Disable Motors after drawing?",AttributeType.Boolean,false));
		metaData.add(motors);
		

		MetaDataGroup other = new MetaDataGroup("Other");
		other.add(new MetaDataProperty("tensionFactor","Tension Factor",AttributeType.Decimal, false));

		metaData.add(other);
		
		return metaData;
	}
	
	// Tension Factor.. The higher the number, the slower the quickdraw will draw the further from the top
	// and near the edges
	// set to 0.0 to disable tension compensation 
	double tensionFactor = 1; // standard Tension   
	
	private double yPosOffset=-20;
	
	private double frameWidth=375;  
	private double frameHeight=450; 

	private double canvasWidth=240;
	private double canvasHeight=280;

	private double leftMotorStepsPerMM=32.8;
	private double rightMotorStepsPerMM=32.8;

	private double unitIncrement=0;
	
	private int moveSpeed=75;
	private int drawSpeed=75;
	
	private boolean disableMotors=false;
	
	@Override
	public double getFrameWidth() {
		return frameWidth;
	}

	@Override
	public double getFrameHeight() {
		return frameHeight;
	}

	@Override
	public double getCanvasWidth() {
		return canvasWidth;
	}

	@Override
	public double getCanvasHeight() {
		return canvasHeight;
	}

	public double getTensionFactor() {
		return tensionFactor;
	}

	public void setTensionFactor(double tensionFactor) {
		this.tensionFactor = tensionFactor;
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

	public double getUnitIncrement() {
		return unitIncrement;
	}

	public void setUnitIncrement(double unitIncrement) {
		this.unitIncrement = unitIncrement;
	}

	public long getMoveDelayInMicroSeconds() {
		int leftMotorDelay = (int) (1000000/(leftMotorStepsPerMM * moveSpeed));
		int rightMotorDelay = (int) (1000000/(rightMotorStepsPerMM * moveSpeed));
		// pick the highest delay
		return Math.max(leftMotorDelay, rightMotorDelay);
	}
	public long getDrawDelayInMicroSeconds() {
		int leftMotorDelay = (int) (1000000/(leftMotorStepsPerMM * drawSpeed));
		int rightMotorDelay = (int) (1000000/(rightMotorStepsPerMM * drawSpeed));
		// pick the highest delay
		return Math.max(leftMotorDelay, rightMotorDelay);
	}

	@Override
	public double getyPosOffset() {
		return yPosOffset;
	}
	
	public void setyPosOffset(double yPosOffset) {
		this.yPosOffset = yPosOffset;
	}

	public void setFrameWidth(double frameWidth) {
		this.frameWidth = frameWidth;
	}

	public void setFrameHeight(double frameHeight) {
		this.frameHeight = frameHeight;
	}

	public void setCanvasWidth(double canvasWidth) {
		this.canvasWidth = canvasWidth;
	}

	public void setCanvasHeight(double canvasHeight) {
		this.canvasHeight = canvasHeight;
	}

	public int getMoveSpeed() {
		return moveSpeed;
	}

	public void setMoveSpeed(int moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	public int getDrawSpeed() {
		return drawSpeed;
	}

	public void setDrawSpeed(int drawSpeed) {
		this.drawSpeed = drawSpeed;
	}

	public boolean isDisableMotors() {
		return disableMotors;
	}

	public void setDisableMotors(boolean disableMotors) {
		this.disableMotors = disableMotors;
	}

	
}
