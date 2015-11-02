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

package com.sketchy.pathing.impl;

import java.awt.Point;

import com.sketchy.SketchyContext;
import com.sketchy.pathing.PathingProcessor;
import com.sketchy.pathing.PathingProcessorProperties;
import com.sketchy.utils.image.SketchyImage;

// this class should is based on the width and height of the drawing area
// the controller class it calls should adjust it for the actual absolute dimension

public class DotMatrixPathingProcessor extends PathingProcessor {

	private DotMatrixPathingProcessorProperties properties = null;
	
	private String statusMessage="";
	private Status status=Status.INIT;
	private int progress=0;
	private boolean cancel=false;
	private boolean paused=false;

	public DotMatrixPathingProcessor(DotMatrixPathingProcessorProperties properties) {
		super(properties);
		this.properties=properties;
	}

	@Override
	public PathingProcessorProperties getProperties() {
		return properties;
	}
	
	public void run(SketchyImage sketchyImage){
		try{
			
			if (SketchyContext.plotterControllerProperties==null){
				throw new Exception("Plotter Controller Properties must be configured!");
			}
			
			if (properties==null){
				throw new Exception("Pathing Properties must be configured!");
			}
			
			cancel=false;
			paused=false;
			progress=0;
	    	SketchyContext.plotterController.enableMotors();
			processImage(sketchyImage);
			SketchyContext.plotterController.home();
			if (SketchyContext.plotterControllerProperties.isDisableMotors()){
				SketchyContext.plotterController.disableMotors();
			}
		    status = Status.COMPLETED;
		} catch (Exception e){
			status = Status.ERROR;
			statusMessage = "Error Drawing! " + e.getMessage();
		}
	}
	
    private void processImage(SketchyImage sketchyImage) throws Exception {
    	statusMessage = "Initializing Drawing";
    	status = Status.DRAWING;
		
		double canvasWidth = SketchyContext.plotterControllerProperties.getCanvasWidth();
		double canvasHeight = SketchyContext.plotterControllerProperties.getCanvasHeight();
		
		if ((canvasWidth<=0) || (canvasHeight<=0)){
			throw new Exception("Plotter Controller Properties Canvas Size must be set!");
		}
		
		double widthDotsPerMM=sketchyImage.getDotsPerMillimeterWidth();
		double heightDotsPerMM=sketchyImage.getDotsPerMillimeterWidth();

		int imageWidth = sketchyImage.getImage().getWidth();
		int imageHeight = sketchyImage.getImage().getHeight();
		
		double physicalWidth = imageWidth/widthDotsPerMM;
		double physicalHeight = imageHeight/heightDotsPerMM;
				
		double widthScale=canvasWidth/physicalWidth;
		double heightScale=canvasHeight/physicalHeight;
		
		double scaledImageWidth=imageWidth;
		double scaledImageHeight=imageHeight;
		
		// xOffset moves the image so it's centered on the page if the image is smaller than the drawing area
		// We always want to print at the top, so we don't have a yOffset... possibly in the future though
		double xOffset = 0;
		
		if (widthScale<1){
			scaledImageWidth=imageWidth*widthScale;
		} else {
			xOffset = (canvasWidth-physicalWidth)/2;
		}
		if (heightScale<1){
			scaledImageHeight=imageHeight*heightScale;
		}
		
		int x=(int)(imageWidth-scaledImageWidth)/2;
		int y=(int)(imageHeight-scaledImageHeight)/2;
		int width=(int) scaledImageWidth;
		int height=(int) scaledImageHeight;
		
		// return a bitmapArray for the provided portion of the image
		boolean[][] bitmapArray = sketchyImage.toBooleanBitmapArray(x, y, width, height);
		
		long totalCount = 0;
		for (int yIdx=0;yIdx<bitmapArray[0].length;yIdx++){
			for (int xIdx=0;xIdx<bitmapArray.length;xIdx++){
				if (bitmapArray[xIdx][yIdx]==true){
					totalCount++;
				}
			}			
		}
		
	    int xpos = 0;
	    int ypos = 0;
		int count=0;
	    statusMessage = "Drawing";
	    int dotCount=0;
    	int maxY=0;
		while(true){
			if (cancel) break;
			Point foundPoint = findNextPoint(bitmapArray, xpos, ypos, maxY);
			if (foundPoint==null){
				break;
			}
	    	
	    	xpos = foundPoint.x;
	    	ypos = foundPoint.y;
	    	
	    	bitmapArray[xpos][ypos]=false; //Not set
	    	
	    	// Physical Location of Pen
	    	double plotterXPos = xpos/widthDotsPerMM+xOffset;
	    	double plotterYPos = ypos/heightDotsPerMM;
	    	
			SketchyContext.plotterController.moveTo(plotterXPos, plotterYPos);
			SketchyContext.plotterController.drawTo(plotterXPos, plotterYPos);
			dotCount++;
			
   	    	while((paused) && (!cancel)){
	    			status = Status.PAUSED;
	    			Thread.sleep(50); //sleep a bit until unpaused
   	    	}
   	    	status=Status.DRAWING;

    		count++;
    		if ((count%100)==0){
    			progress = (int) ((count/(double)totalCount) * 100);
    			statusMessage = "Drawing. " + dotCount + " of " + totalCount + " Complete.";
	   	   	}
    		
		}
		
		if (cancel){
			status=Status.CANCELLED;
	   	   	statusMessage = "Drawing Cancelled!";
		}
    }
    
	private Point findNextPoint(boolean[][] bitmapArray, int xPos, int yPos, int maxY){
    	// does the current pixel happen to be set? typically only 0,0
    	// if so, just return
		if (bitmapArray[xPos][yPos]) return new Point(xPos, yPos);
		
		int width=bitmapArray.length;
		int height=bitmapArray[0].length;
		
		int minX=0;
    	int maxX=width-1;
    	
    	int minY=0;
    	
    	if (maxY==0){
    		maxY=height-1;	
    	} else { // make sure maxY is still less than the height of the bitmapArray
    		maxY=Math.min(maxY, height-1);
    	}
    	
        
		for (int distanceFromCenter=1;distanceFromCenter<=Math.max(width, height);distanceFromCenter++){
    		for (int z=-distanceFromCenter;z<=distanceFromCenter;z++){
    			int x=xPos+z;
    			if ((x>=minX) && (x<=maxX)){
        			int y=yPos-distanceFromCenter;
        			if ((y>=minY) && (y<=maxY)){
        				if (bitmapArray[x][y]) return new Point(x, y);
        			}
        			y=yPos+distanceFromCenter;
        			if ((y>=minY) && (y<=maxY)){
        				if (bitmapArray[x][y]) return new Point(x, y);
        			}
    			}
    			
    			int y=yPos+z;
    			if ((y>=minY) && (y<=maxY)){
        			x=xPos-distanceFromCenter;
        			if ((x>=minX) && (x<=maxX)){
        				if (bitmapArray[x][y]) return new Point(x, y);
        			}
        			x=xPos+distanceFromCenter;
        			if ((x>=minX) && (x<=maxX)){
        				if (bitmapArray[x][y]) return new Point(x, y);
        			}
    			}
    		}
    	}
		return null;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public Status getStatus() {
		return status;
	}

	public int getProgress() {
		return progress;
	}

	@Override
	public void cancelDrawing() {
		cancel=true;
	}

	@Override
	public void pauseDrawing() {
		paused=true;
	}

	@Override
	public void resumeDrawing() {
		paused=false;
	}
 
}
