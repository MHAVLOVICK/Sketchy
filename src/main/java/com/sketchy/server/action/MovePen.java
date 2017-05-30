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

package com.sketchy.server.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.sketchy.SketchyContext;
import com.sketchy.drawing.DrawingSize;
import com.sketchy.hardware.HardwareControllerProperties;
import com.sketchy.plotter.PlotterController;
import com.sketchy.server.JSONServletResult;
import com.sketchy.server.ServletAction;
import com.sketchy.server.JSONServletResult.Status;
import com.sketchy.utils.JSONUtils;

public class MovePen extends ServletAction {
	@Override
	public JSONServletResult execute(HttpServletRequest request) throws Exception {
		JSONServletResult jsonServletResult = new JSONServletResult(Status.SUCCESS);

		HardwareControllerProperties oldHardwareControllerProperties = SketchyContext.hardwareControllerProperties;

		try{
			String responseBody = getResponseBody(request);

			if (StringUtils.equalsIgnoreCase(request.getParameter("home"),"Y")){
				SketchyContext.initializeHardwareController();
				SketchyContext.initializePlotterController();
				PlotterController plotterController = SketchyContext.plotterController;
				plotterController.home();
			} else if (!StringUtils.isBlank(responseBody)){
				try{
					boolean draw = StringUtils.equalsIgnoreCase(request.getParameter("draw"),"Y");
					
					Map<String, String> map = (Map<String, String>) JSONUtils.fromJson(responseBody);
					String drawingSizeString = map.get("drawingSize");
					Double xPos = Double.parseDouble(map.get("xPos"));
					Double yPos = Double.parseDouble(map.get("yPos"));
					SketchyContext.initializeHardwareController();
					SketchyContext.initializePlotterController();
					
					PlotterController plotterController = SketchyContext.plotterController;
					
					DrawingSize drawingSize = DrawingSize.parse(drawingSizeString);
					double canvasWidth = plotterController.getProperties().getCanvasWidth();
					
					double drawingLeft=(canvasWidth-drawingSize.getWidth())/2;
					double drawingRight = drawingLeft+drawingSize.getWidth()-1;
					
					xPos+=drawingLeft;
					
					if (xPos<drawingLeft) xPos=drawingLeft;
					if (xPos>drawingRight) xPos=drawingRight;
					if (yPos<0) yPos=0d;
					if (yPos>drawingSize.getHeight()) yPos=drawingSize.getHeight();
					
					if (!draw) {
						plotterController.moveTo(xPos, yPos);
					}

					plotterController.drawTo(xPos, yPos);
				} catch (NumberFormatException e){
					
				}
			}
				
		} catch (Throwable t){
			jsonServletResult = new JSONServletResult(Status.ERROR, "Error Saving Hardware Settings! " + t.getMessage());
		} finally {
			SketchyContext.hardwareControllerProperties = oldHardwareControllerProperties;
		}
		return jsonServletResult;
	}
	

}
