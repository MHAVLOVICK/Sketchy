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
		
	private double currentXPos=0;
	private double currentYPos=0;
	
	private int leftMotorStepNumber=0;
	private int rightMotorStepNumber=0;


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
		// unitIncrement = Math.min(1.0/leftMotorStepsPerUnit,1.0/rightMotorStepsPerUnit)*2;
		
		resetCurrentPositionToHome();
	}


	// This method is called when the pen is "Home" to initialize the current position to the home values 
	public void resetCurrentPositionToHome(){
	    currentXPos=homeXPos;
	    currentYPos=homeYPos;

	    // Set stepNumbers
	    double leftMotorLength = getLeftMotorLength(currentXPos, currentYPos);
	    double rightMotorLength = getRightMotorLength(currentXPos, currentYPos);

	    // convert motor lengths to steps
	    leftMotorStepNumber = getLeftMotorStepNumber(leftMotorLength);
	    rightMotorStepNumber = getRightMotorStepNumber(rightMotorLength);
	}

	public void shutDown(){
	}
	    
	public synchronized void penUp(){
		 SketchyContext.hardwareController.penUp();
	}
	
	public synchronized void penDown(){
		 SketchyContext.hardwareController.penDown();
	}
	
	private Point2D.Double getAbsolutePoint(double xPos, double yPos){
	    return new Point2D.Double(xPos+canvasLeftPos, yPos+canvasTopPos);
	}
	
	@Override
	public void enableMotors() throws Exception {
		SketchyContext.hardwareController.enableMotors();
	}
	
	@Override
	public void disableMotors() throws Exception {
		SketchyContext.hardwareController.disableMotors();
	}

	public synchronized void home() throws Exception {
		penUp();
		moveMotorsTo(homeXPos, homeYPos, properties.getMoveDelayInMicroSeconds());
		penDown();
	}
	    
	
	public synchronized void moveTo(double newXPos, double newYPos) throws Exception {
		penUp();
		moveMotorsTo(newXPos, newYPos, properties.getMoveDelayInMicroSeconds());
		
		currentXPos=newXPos;
	    currentYPos=newYPos;
	}
	
	public synchronized void drawTo(double newXPos, double newYPos) throws Exception {
		double unitIncrement = properties.getUnitIncrement();
		long drawDelayInMicroSeconds = properties.getDrawDelayInMicroSeconds();
		double tensionFactor=properties.getTensionFactor();

		penDown();

		if (unitIncrement<=0){
			long delay=drawDelayInMicroSeconds;
			if (tensionFactor>0){
				double minTension = (1-Math.min(getLeftStringTension(newXPos, newYPos),getRightStringTension(newXPos, newYPos)))+1;
		    	delay = (int) (delay*(minTension*tensionFactor));
			}
			moveMotorsTo(newXPos, newYPos, delay);
			currentXPos=newXPos;
		    currentYPos=newYPos;
		} else {
			double x1=currentXPos;
		    double y1=currentYPos;
		    double x2=newXPos;
		    double y2=newYPos;

		    long startDelay = drawDelayInMicroSeconds;
		    long endDelay = drawDelayInMicroSeconds;
		    if (tensionFactor>0){
			    // figure out the tension in the strings
			    // I'm sure this logic isn't 100% correct.. but it seems to work good enough 
			    double startMinTension = (1-Math.min(getLeftStringTension(x1, y1),getRightStringTension(x1, y1)))+1;
			    double endMinTension = (1-Math.min(getLeftStringTension(x2, y2),getRightStringTension(x2, y2)))+1;
		    	startDelay = (int) (startDelay*(startMinTension*tensionFactor));
			    endDelay = (int) (endDelay*(endMinTension*tensionFactor));
			}			    
			
		    List<RangeSet> rangeSets = new ArrayList<RangeSet>();
			rangeSets.add(new RangeSet(x1, x2, unitIncrement));
			rangeSets.add(new RangeSet(y1, y2, unitIncrement));
			rangeSets.add(new RangeSet(startDelay, endDelay));

		    RangeHook hook = new RangeHook() {
		    	@Override
				public void processRange(double[] values) throws Exception {
					moveMotorsTo(values[0], values[1], (int) values[2]); 
		    	}
			};

			// it's possible that range was too small and not proccessed.. 
			// only update current x and y if actually processed.
			boolean processedRange = Range.processRanges(rangeSets, hook, true);
			if (processedRange){
				currentXPos=x2;
			    currentYPos=y2;	
			}
		}
	}

	private void moveMotorsTo(double xPos, double yPos, long delay) throws Exception {
	    // This moves the pen from the current Position to the new Position
	    // by taking the current motor lengths and just moving them to their new lengths
	    double newLeftMotorLength = getLeftMotorLength(xPos, yPos);
	    double newRightMotorLength = getRightMotorLength(xPos, yPos);

	    // convert motor lengths to steps
	    int newLeftMotorStepNumber = getLeftMotorStepNumber(newLeftMotorLength);
	    int newRightMotorStepNumber = getRightMotorStepNumber(newRightMotorLength);
	    
	    SketchyContext.hardwareController.moveMotors(newLeftMotorStepNumber - leftMotorStepNumber, newRightMotorStepNumber - rightMotorStepNumber, delay);

	    leftMotorStepNumber = newLeftMotorStepNumber;
	    rightMotorStepNumber = newRightMotorStepNumber;
	}

	
	private double getLeftStringTension(double xPos, double yPos){
		double frameWidth=properties.getFrameWidth();
		// for our purposes, don't return tensions above 1
	    Point2D.Double point = getAbsolutePoint(xPos, yPos);
	    double tension = (getLeftMotorLength(xPos, yPos) * (frameWidth-point.x)) / (point.y * frameWidth); 
	    return Math.min(tension, 1);
	}
	
	private double getRightStringTension(double xPos, double yPos){
		double frameWidth=properties.getFrameWidth();
	    Point2D.Double point = getAbsolutePoint(xPos, yPos);
	    double tension = (getRightMotorLength(xPos, yPos) * (point.x)) / (point.y * frameWidth);
	    return Math.min(tension, 1);
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

	private int getLeftMotorStepNumber(double length){
		return (int)(properties.getLeftMotorStepsPerMM() * length);
	}
	
	private int getRightMotorStepNumber(double length){
		return (int)(properties.getRightMotorStepsPerMM() * length);
	}

}
