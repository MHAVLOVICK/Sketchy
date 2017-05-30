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

public class RunCommands extends ServletAction {
	@Override
	public JSONServletResult execute(HttpServletRequest request) throws Exception {
		JSONServletResult jsonServletResult = new JSONServletResult(Status.SUCCESS);

		HardwareControllerProperties oldHardwareControllerProperties = SketchyContext.hardwareControllerProperties;

		try{
			String responseBody = getResponseBody(request);

			if (!StringUtils.isBlank(responseBody)){
		
				Map<String, String> map = (Map<String, String>) JSONUtils.fromJson(responseBody);
				String drawingSizeString = map.get("drawingSize");
				String commands = map.get("commands");
				SketchyContext.initializeHardwareController();
				SketchyContext.initializePlotterController();
				
				PlotterController plotterController = SketchyContext.plotterController;
				
				DrawingSize drawingSize = DrawingSize.parse(drawingSizeString);
				double canvasWidth = plotterController.getProperties().getCanvasWidth();
				
				double drawingLeft=(canvasWidth-drawingSize.getWidth())/2;
				double drawingRight = drawingLeft+drawingSize.getWidth()-1;

				String[] commandsArray =StringUtils.split(commands, "\r\n");
				for (int commandIdx=0;commandIdx<commandsArray.length;commandIdx++){
					try {
						String line = StringUtils.trim(commandsArray[commandIdx]);
						if (StringUtils.isNotBlank(line) && !line.startsWith("#")){
							String[] commandArray = StringUtils.split(line, " ");
							String command = commandArray[0];
							
							if (StringUtils.equalsIgnoreCase(command, "HOME")) {
								plotterController.home();
							} else if (commandArray.length>=5){
								double frontXPos = Double.parseDouble(commandArray[1]);
								double frontYPos = Double.parseDouble(commandArray[2]);
								double backXPos = Double.parseDouble(commandArray[3]);
								double backYPos = Double.parseDouble(commandArray[4]);
								
								frontXPos+=drawingLeft;
								backXPos+=drawingLeft;
								
								if (frontXPos<drawingLeft) frontXPos=drawingLeft;
								if (frontXPos>drawingRight) frontXPos=drawingRight;
								if (frontYPos<0) frontYPos=0d;
								if (frontYPos>drawingSize.getHeight()) frontYPos=drawingSize.getHeight();
								
								if (backXPos<drawingLeft) backXPos=drawingLeft;
								if (backXPos>drawingRight) backXPos=drawingRight;
								if (backYPos<0) backYPos=0d;
								if (backYPos>drawingSize.getHeight()) backYPos=drawingSize.getHeight();
								
								if (StringUtils.equalsIgnoreCase(command, "MOVE")){
									plotterController.moveTo(frontXPos, frontYPos, backXPos, backYPos);
									plotterController.drawTo(frontXPos, frontYPos, backXPos, backYPos);
								} else if (StringUtils.equalsIgnoreCase(command, "DRAW")){
									plotterController.drawTo(frontXPos, frontYPos, backXPos, backYPos);
								}
							}
						}
					} catch (NumberFormatException e){
						// just ignore command if badly formatted
					}
				}
				plotterController.home();
			}
		} catch (Throwable t){
			jsonServletResult = new JSONServletResult(Status.ERROR, "Error Saving Hardware Settings! " + t.getMessage());
		} finally {
			SketchyContext.hardwareControllerProperties = oldHardwareControllerProperties;
		}
		return jsonServletResult;
	}
	

}
