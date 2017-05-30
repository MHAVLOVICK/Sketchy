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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.sketchy.SketchyContext;
import com.sketchy.plotter.PlotterController;
import com.sketchy.plotter.PlotterControllerProperties;
import com.sketchy.utils.Range;
import com.sketchy.utils.RangeHook;
import com.sketchy.utils.RangeSet;

public class VPlotterController extends PlotterController { 
	
	public VPlotterController(VPlotterControllerProperties properties) {
		super(properties);
		this.properties=properties;
		init();
	}
	
	@Override
	public PlotterControllerProperties getProperties(){
		return properties;
	}
	
	private VPlotterControllerProperties properties = null;
	
	private double canvasLeftPos;
	private double canvasTopPos;
	
	private double homeXPos; // relativeToCanvasArea
	private double homeYPos; 
		
	private double currentFrontXPos=0;
	private double currentFrontYPos=0;
	
	private double currentBackXPos=0;
	private double currentBackYPos=0;
	
	private int frontLeftMotorStepNumber=0;
	private int frontRightMotorStepNumber=0;
	
	private int backLeftMotorStepNumber=0;
	private int backRightMotorStepNumber=0;

	private double unitIncrement=0;


	private void init() {

		double frameWidth=properties.getFrameWidth(); 
		// frameHeight=hardwareProperties.getFrameHeight(); not really needed
		double canvasWidth=properties.getCanvasWidth();

		// Set the canvas left position so that the canvas area is centered
		// Set the canvas top position so that the canvas area is 1/4 the frame width
		// Possibly update later so that it can be user defined
		canvasLeftPos = (frameWidth - canvasWidth)/2;
		canvasTopPos = frameWidth/4; 
		
		// set homeXPos to the center of the canvas area
		// set homeYPos to the canvasTopPosition
		homeXPos = (canvasWidth/2); 
		homeYPos = properties.getyPosOffset();

	    // *** Future Use *** 
		// based on the recommended QuickDraw configuration, the unitIncrement should be about double
		// using a multiplier of 4 instead of 2 for now... can decrease as required if we think it's needed
		unitIncrement = Math.min(1.0/properties.getLeftMotorStepsPerMM(),1.0/properties.getRightMotorStepsPerMM())*4;
		
		resetCurrentPositionToHome();
	}


	// This method is called when the pen is "Home" to initialize the current position to the home values 
	public void resetCurrentPositionToHome(){
	    currentFrontXPos=homeXPos;
	    currentFrontYPos=homeYPos;

	    currentBackXPos=homeXPos;
	    currentBackYPos=homeYPos;
	    
	    // Set stepNumbers
	    double frontLeftMotorLength = getLeftMotorLength(currentFrontXPos, currentFrontYPos);
	    double frontRightMotorLength = getRightMotorLength(currentFrontXPos, currentFrontYPos);

	    // Set stepNumbers
	    double backLeftMotorLength = getLeftMotorLength(currentBackXPos, currentBackYPos);
	    double backRightMotorLength = getRightMotorLength(currentBackXPos, currentBackYPos);
	    
	    // convert motor lengths to steps
	    frontLeftMotorStepNumber = getFrontLeftMotorStepNumber(frontLeftMotorLength);
	    frontRightMotorStepNumber = getFrontRightMotorStepNumber(frontRightMotorLength);

	    backLeftMotorStepNumber = getBackLeftMotorStepNumber(backLeftMotorLength);
	    backRightMotorStepNumber = getBackRightMotorStepNumber(backRightMotorLength);
	}

	public void shutDown(){
	}
	
	private Point2D.Double getAbsolutePoint(double xPos, double yPos){
	    return new Point2D.Double(xPos+canvasLeftPos, yPos+canvasTopPos);
	}

	public synchronized void home() throws Exception {
		moveMotorsTo(homeXPos, homeYPos, homeXPos, homeYPos, properties.getMoveDelayInMicroSeconds());
	}
	    
	@Override
	public synchronized void moveTo(double newFrontXPos, double newFrontYPos, double newBackXPos, double newBackYPos) throws Exception {
		moveMotorsTo(newFrontXPos, newFrontYPos, newBackXPos, newBackYPos, properties.getMoveDelayInMicroSeconds());
		
		currentFrontXPos=newFrontXPos;
	    currentFrontYPos=newFrontYPos;

	    currentBackXPos=newBackXPos;
	    currentBackYPos=newBackYPos;
	}
	
	@Override
	public synchronized void drawTo(double newFrontXPos, double newFrontYPos, double newBackXPos, double newBackYPos) throws Exception {
		//double unitIncrement = properties.getUnitIncrement();
		
		long drawDelayInMicroSeconds = properties.getDrawDelayInMicroSeconds();
	
		if (unitIncrement<=0){
			long delay=drawDelayInMicroSeconds;
			moveMotorsTo(newFrontXPos, newFrontYPos, newBackXPos, newBackYPos, delay);
			currentFrontXPos=newFrontXPos;
		    currentFrontYPos=newFrontYPos;

		    currentBackXPos=newBackXPos;
		    currentBackYPos=newBackYPos;
		} else {
			double fx1=currentFrontXPos;
		    double fy1=currentFrontYPos;
		    double fx2=newFrontXPos;
		    double fy2=newFrontYPos;

			double bx1=currentBackXPos;
		    double by1=currentBackYPos;
		    double bx2=newBackXPos;
		    double by2=newBackYPos;
			
		    List<RangeSet> rangeSets = new ArrayList<RangeSet>();
			rangeSets.add(new RangeSet(fx1, fx2, unitIncrement));
			rangeSets.add(new RangeSet(fy1, fy2, unitIncrement));
			rangeSets.add(new RangeSet(bx1, bx2, unitIncrement));
			rangeSets.add(new RangeSet(by1, by2, unitIncrement));
			rangeSets.add(new RangeSet(drawDelayInMicroSeconds, drawDelayInMicroSeconds));
			
		    RangeHook hook = new RangeHook() {
		    	@Override
				public void processRange(double[] values) throws Exception {
					moveMotorsTo(values[0], values[1], values[2], values[3], (int) values[4]); 
		    	}
			};

			// it's possible that range was too small and not proccessed.. 
			// only update current x and y if actually processed.
			boolean processedRange = Range.processRanges(rangeSets, hook, true);
			if (processedRange){
				currentFrontXPos=fx2;
			    currentFrontYPos=fy2;	
				currentBackXPos=bx2;
			    currentBackYPos=by2;				    
			}
		}
	}

	private void moveMotorsTo(double frontXPos, double frontYPos, double backXPos, double backYPos, long delay) throws Exception {
	    // This moves the pen from the current Position to the new Position
	    // by taking the current motor lengths and just moving them to their new lengths
	    double newFrontLeftMotorLength = getLeftMotorLength(frontXPos, frontYPos);
	    double newFrontRightMotorLength = getRightMotorLength(frontXPos, frontYPos);

	    double newBackLeftMotorLength = getLeftMotorLength(backXPos, backYPos);
	    double newBackRightMotorLength = getRightMotorLength(backXPos, backYPos);
	    
	    // convert motor lengths to steps
	    int newFrontLeftMotorStepNumber = getFrontLeftMotorStepNumber(newFrontLeftMotorLength);
	    int newFrontRightMotorStepNumber = getFrontRightMotorStepNumber(newFrontRightMotorLength);
	    
	    int newBackLeftMotorStepNumber = getBackLeftMotorStepNumber(newBackLeftMotorLength);
	    int newBackRightMotorStepNumber = getBackRightMotorStepNumber(newBackRightMotorLength);
	    
	    SketchyContext.hardwareController.moveMotors(newFrontLeftMotorStepNumber - frontLeftMotorStepNumber, newFrontRightMotorStepNumber - frontRightMotorStepNumber, newBackLeftMotorStepNumber - backLeftMotorStepNumber, newBackRightMotorStepNumber - backRightMotorStepNumber, delay);

	    frontLeftMotorStepNumber = newFrontLeftMotorStepNumber;
	    frontRightMotorStepNumber = newFrontRightMotorStepNumber;
	    backLeftMotorStepNumber = newBackLeftMotorStepNumber;
	    backRightMotorStepNumber = newBackRightMotorStepNumber;

	}
	
	private double getLeftMotorLength(double xPos, double yPos){
	    Point2D.Double point = getAbsolutePoint(xPos, yPos);
	    return Math.sqrt(Math.pow(point.x, 2) + Math.pow(point.y, 2));
	}

	private double getRightMotorLength(double xPos, double yPos){
		double frameWidth=properties.getFrameWidth();
	    Point2D.Double point = getAbsolutePoint(xPos, yPos);
	    return Math.sqrt(Math.pow(frameWidth-point.x, 2) + Math.pow(point.y, 2));
	}

	
	private int getFrontLeftMotorStepNumber(double length){
		return (int)(properties.getLeftMotorStepsPerMM() * length);
	}
	
	private int getFrontRightMotorStepNumber(double length){
		return (int)(properties.getRightMotorStepsPerMM() * length);
	}

	
	private int getBackLeftMotorStepNumber(double length){
		return (int)(properties.getLeftMotorStepsPerMM() * length);
	}
	
	private int getBackRightMotorStepNumber(double length){
		return (int)(properties.getRightMotorStepsPerMM() * length);
	}

	@Override
	public double getCurrentFrontXPos() {
		return currentFrontXPos;
	}

	@Override
	public double getCurrentFrontYPos() {
		return currentFrontYPos;
	}
	
	@Override
	public double getCurrentBackXPos() {
		return currentBackXPos;
	}

	@Override
	public double getCurrentBackYPos() {
		return currentBackYPos;
	}

}
