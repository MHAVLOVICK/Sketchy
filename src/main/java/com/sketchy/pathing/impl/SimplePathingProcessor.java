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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.sketchy.SketchyContext;
import com.sketchy.pathing.PathingProcessor;
import com.sketchy.pathing.PathingProcessorProperties;
import com.sketchy.utils.image.SketchyImage;

// this class should is based on the width and height of the drawing area
// the controller class it calls should adjust it for the actual absolute dimension

public class SimplePathingProcessor extends PathingProcessor {

	private static final Random random = new Random(System.currentTimeMillis());
	
	public static enum PathingPattern {
		Random("Random"),
		FollowLeft("Follow Left"), 
		FollowRight("Follow Right"),
		Clockwise("Clockwise", new int[]{0,1,2,3,4,5,6,7}),
		CounterClockwise("Counter Clockwise", new int[]{0,7,6,5,4,3,2,1}),
		UpperLeftToLowerRight("Upper Left to Lower Right", new int[]{7,0,6,1,5,2,4,3}),
		UpperRightToLowerLeft("Upper Right to Lower Left", new int[]{1,0,2,7,3,6,4,5}),
		LowerLeftToUpperRight("Lower Left to Upper Right", new int[]{5,4,6,3,7,2,0,1}),
		LowerRightToUpperLeft("Lower Right to Upper Left", new int[]{3,4,2,5,1,6,0,7}),
		PreferNonDiagonals("Prefer Non-Diagonals", new int[]{0,2,4,6,1,3,5,7}),
		SkipDiagonals("Skip Diagonals", new int[]{0,2,4,6,0,2,4,6});
		
		public String label="";
		public int[] pattern=null;
		private PathingPattern(String label){
			this.label=label;
		}
		private PathingPattern(String label, int[] pattern){
			this.label=label;
			this.pattern=pattern;
		}
	}
	
	public static enum PathingDirectionPreference {
		NoPreference("No Direction Preference"),
		SameDirection("Prefer Same Direction"),
		DifferentDirection("Prefer Different Direction");

		public String label="";
		public int[] pattern=null;
		private PathingDirectionPreference(String label){
			this.label=label;
		}
	}
	
	public static enum PathingBacktrackOption {
		Backtrack("Backtrack"),
		LiftPen("Lift Pen");
		public String label="";
		public int[] pattern=null;
		private PathingBacktrackOption(String label){
			this.label=label;
		}
	}
	
	
	private SimplePathingProcessorProperties properties = null;
	
	// Initialize History to 100,000
	private final static int HISTORY_INITIAL_SIZE=100000;

	// Limit the History to 1,000,000
	// There really isn't the need to go beyond this limit,
	// and won't be a problem if it happens.  This will just help to keep memory in check
	private final static int HISTORY_LIMIT=1000000;
	
	private String statusMessage="";
	private Status status=Status.INIT;
	private int progress=0;
	private boolean cancel=false;
	private boolean paused=false;

	public SimplePathingProcessor(SimplePathingProcessorProperties properties) {
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
			if (properties.getPathingPattern()==null){
				throw new Exception("Pathing Properties 'Pattern' must be set!");
			}
			if (properties.getPathingDirectionPreference()==null){
				throw new Exception("Pathing Properties 'Direction Preference' must be set!");
			}
			if (properties.getPathingBacktrackOption()==null){
				throw new Exception("Pathing Properties 'Backtrack Option' must be set!");
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
		
		if (properties.getPathingBacktrackOption().equals(PathingBacktrackOption.Backtrack)){
			//  If backtracking then a pixel is drawn twice, so count it twice
			totalCount*=2;
		}
		
	    int xpos = 0;
	    int ypos = 0;
		int count=0;
	    statusMessage = "Drawing";
	    int penLifts=0;
    	List<Point> history = new ArrayList<Point>(HISTORY_INITIAL_SIZE);
    	int stripeLength = height-1;
    	stripeLength=(int) (heightDotsPerMM*properties.getStripingLengthInMM());
    	int maxY=stripeLength;
		while(true){
			if (cancel) break;
			Point foundPoint = findNextPoint(bitmapArray, xpos, ypos, maxY);
			if (foundPoint==null){
				if ((stripeLength==0) || (maxY>=height)){
					break;
				}
				maxY+=stripeLength;
				continue;
			}
	    	
	    	xpos = foundPoint.x;
	    	ypos = foundPoint.y;
	    	
	    	bitmapArray[xpos][ypos]=false; //Not set
	    	
	    	// Physical Location of Pen
	    	double plotterXPos = xpos/widthDotsPerMM+xOffset;
	    	double plotterYPos = ypos/heightDotsPerMM;
	    	
	    	penLifts++;
			SketchyContext.plotterController.moveTo(plotterXPos, plotterYPos);
			SketchyContext.plotterController.drawTo(plotterXPos, plotterYPos);

	    	int lastDirection=-1;
	   	    while(true){
	   	    	if (cancel) break;
	   	    	
	   	    	while((paused) && (!cancel)){
   	    			status = Status.PAUSED;
   	    			Thread.sleep(50); //sleep a bit until unpaused
	   	    	}
	   	    	status=Status.DRAWING;
	   	    	
	   	    	Integer direction = null;
	   	    	if (history.size()<HISTORY_LIMIT){
	   	    		direction = getNextPixelDirection(bitmapArray, xpos, ypos, lastDirection, maxY);
	   	    	}
	   	    	if (direction!=null){
	   	    		count++;
	   	    		if ((count%100)==0){
	   	    			progress = (int) ((count/(double)totalCount) * 100);
	   	    			statusMessage = "Drawing. Total Pen Lifts: " + penLifts;
	   	    		}
	   	    		int dir = direction.intValue();
	   	    		lastDirection=dir;
	   	    		Point nextPoint = getPointFromDirection(dir);
	   	    		xpos+=nextPoint.x;
	   	    		ypos+=nextPoint.y;
	
	   		    	bitmapArray[xpos][ypos]=false; //Not set

	   		    	// if we are backtracking, then add to the history
	   	    		if (properties.getPathingBacktrackOption()==PathingBacktrackOption.Backtrack){
	   	    			history.add(new Point(nextPoint));
	   	    		}
	   	    		
	   		    	plotterXPos = xpos/widthDotsPerMM+xOffset;;
	   		    	plotterYPos = ypos/heightDotsPerMM;
	   		    	SketchyContext.plotterController.drawTo(plotterXPos, plotterYPos);
	   	    	} else {
	   	    		count++;
	   	    		if ((count%100)==0){
	   	    			progress = (int) ((count/(double)totalCount) * 100);
	   	    		}
	   	    		
	   	    		lastDirection=-1;
	   	    		if (history.size()==0){
	   	    			break;
	   	    		}
	   	    		Point point = history.remove(history.size()-1);
	   	    		xpos-=point.x;
	   	    		ypos-=point.y;

	   	    		plotterXPos = xpos/widthDotsPerMM+xOffset;
	   		    	plotterYPos = ypos/heightDotsPerMM;
	   		    	SketchyContext.plotterController.drawTo(plotterXPos, plotterYPos);
	   	    	}
	   	    }
	   	    if (cancel){
	   	    	status=Status.CANCELLED;
	   	    	statusMessage = "Drawing Cancelled!";
	   	    }
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
 	
    public Integer getNextPixelDirection(boolean[][] bitmapArray, int x, int y, int lastDirection, int maxY){
    	Integer ret = null;
    	
    	// get pattern
    	int[] pathingPattern = null;
    	if (properties.getPathingPattern().pattern!=null){
    		pathingPattern = properties.getPathingPattern().pattern;
    	} else if (properties.getPathingPattern()==PathingPattern.Random){
			pathingPattern = new int[]{-1,-1,-1,-1,-1,-1,-1,-1};
    		for (int idx=0;idx<8;idx++){
				while(true){
					int randomIdx= random.nextInt(8); // next random number 0 - 7
					if (pathingPattern[randomIdx]==-1){
						pathingPattern[randomIdx]=idx;
						break;
					}
				}
    		}
    	}

    	for (int idx=0;idx<8;idx++){
    		int dir=-1;
    		if (pathingPattern!=null){
    			dir = pathingPattern[idx];
    		} else {
        		if (properties.getPathingPattern()==PathingPattern.FollowLeft){
            		dir = (lastDirection+idx+6) % 8;
        		} else if (properties.getPathingPattern()==PathingPattern.FollowRight){
        			dir = (lastDirection+(8-idx)+2) % 8;
        		} else {
        			dir = idx; // if for some reason, nothing is found, then just path in idx order
        		}
    		} 
    		
    		Point point = getPointFromDirection(dir);
    		int testX = x+point.x;
    		int testY = y+point.y;


    		if ((maxY<=0) || (testY<=maxY) || (getPointFromDirection(dir).y<1)){
	    		if ((testX>=0) && (testX<bitmapArray.length) && 
	    			(testY>=0) && (testY<bitmapArray[0].length)){
	    			if (bitmapArray[testX][testY]){
	    				if (properties.getPathingDirectionPreference()==PathingDirectionPreference.NoPreference){
	    					ret=new Integer(dir);
	    					break; // or just break when you find a pixel, regardless of last travel direction
	    				} else if ((properties.getPathingDirectionPreference()==PathingDirectionPreference.DifferentDirection) && 
	    					(idx!=lastDirection)){
	    					ret=new Integer(dir);
	        				break; // exit out.. we found what we were looking for
	    				} else if ((properties.getPathingDirectionPreference()==PathingDirectionPreference.SameDirection) && 
	    					(idx==lastDirection)){
	    					ret=new Integer(dir);
	    					break; // exit out.. we found a 
	    				} else {
	    	    	    	if (ret==null){ // if the return direction hasn't been set yet.. then set it here
	    	    	    		ret=new Integer(dir);
	    	    	    	}
	    				}
	    			}
	    		}
    		}
    	}
    	return ret;
    }

    
    public Point getPointFromDirection(int direction){
    	if ((direction<0) || (direction>7)){
    		throw new RuntimeException("Invalid Direction to search for next Point! direction= " + direction);
    	}
    	int stepX = 0;
    	int stepY = 0;
    	if ((direction==0) || (direction==1) || (direction==7)){
    		stepY=-1;
  	   	} else if ((direction==3) || (direction==4) || (direction==5)){
  		   stepY=1;
  	   	}
  	   
  	   if ((direction==1) || (direction==2)  || (direction==3)){
  		   stepX=1;
  	   } else if ((direction==5) || (direction==6) || (direction==7)){
  		   stepX=-1;
  	   }
  	   
  	   return new Point(stepX, stepY);
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
