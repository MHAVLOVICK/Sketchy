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

package com.sketchy.pathing;

import com.sketchy.SketchyContext;
import com.sketchy.pathing.PathingProcessor.Status;
import com.sketchy.utils.image.SketchyImage;

public class DrawingProcessorThread extends Thread {
	
	private PathingProcessor pathingProcessor = null;
	private SketchyImage sketchyImage = null;
	
	public DrawingProcessorThread(PathingProcessor pathingProcessor, SketchyImage sketchyImage){
		this.pathingProcessor = pathingProcessor;
		this.sketchyImage = sketchyImage;
	}

	public Status getStatus() {
		return pathingProcessor.getStatus();
	}

	public String getStatusMessage() {
		return pathingProcessor.getStatusMessage();
	}

	public int getProgress() {
		return pathingProcessor.getProgress();
	}
	
	public void run(){
		// Save the drawing and movespeeds
		// they can be updated while drawing and we want to put them back where they were at before the drawing
		int drawSpeed=SketchyContext.plotterControllerProperties.getDrawSpeed();
		int moveSpeed=SketchyContext.plotterControllerProperties.getMoveSpeed();
		try{
			pathingProcessor.run(sketchyImage);
		} finally {
			SketchyContext.plotterControllerProperties.setDrawSpeed(drawSpeed);
			SketchyContext.plotterControllerProperties.setMoveSpeed(moveSpeed);
		}
	}
	
	public void cancelDrawing(){
		pathingProcessor.cancelDrawing();
	}

	public void pauseDrawing(){
		pathingProcessor.pauseDrawing();
	}
	
	public void resumeDrawing(){
		pathingProcessor.resumeDrawing();
	}
	
}
