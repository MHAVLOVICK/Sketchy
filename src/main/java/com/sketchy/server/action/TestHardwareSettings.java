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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.sketchy.SketchyContext;
import com.sketchy.hardware.HardwareControllerProperties;
import com.sketchy.metadata.MetaDataObject;
import com.sketchy.server.JSONServletResult;
import com.sketchy.server.ServletAction;
import com.sketchy.server.JSONServletResult.Status;

public class TestHardwareSettings extends ServletAction {
	@Override
	public JSONServletResult execute(HttpServletRequest request) throws Exception {
		JSONServletResult jsonServletResult = new JSONServletResult(Status.SUCCESS);

		HardwareControllerProperties oldHardwareControllerProperties = SketchyContext.hardwareControllerProperties;
		try{
			String responseBody = getResponseBody(request);
			
			HardwareControllerProperties newHardwareControllerProperties =  (HardwareControllerProperties) MetaDataObject.fromJson(responseBody);
			SketchyContext.hardwareControllerProperties = newHardwareControllerProperties;

			SketchyContext.initializeHardwareController();
			
			String action = request.getParameter("action");
			if (StringUtils.equalsIgnoreCase(action,"penUp")){
				SketchyContext.hardwareController.penUp();
			} else if (StringUtils.equalsIgnoreCase(action,"penDown")){
				SketchyContext.hardwareController.penDown();
			} else if (StringUtils.equalsIgnoreCase(action, "penCycle")){
				for (int idx=0;idx<5;idx++){
					SketchyContext.hardwareController.penUp();
					SketchyContext.hardwareController.penDown();
				}
			} else if (StringUtils.equalsIgnoreCase(action,"leftMotorForward")){
  			    // Using delay of 0, it will be restricted by the Minimum Step Period for the motor
				SketchyContext.hardwareController.moveMotors(10000, 0, 0);
			} else if (StringUtils.equalsIgnoreCase(action,"leftMotorBackward")){
  			    // Using delay of 0, it will be restricted by the Minimum Step Period for the motor
				SketchyContext.hardwareController.moveMotors(-10000, 0, 0);
			} else if (StringUtils.equalsIgnoreCase(action,"rightMotorForward")){
  			    // Using delay of 0, it will be restricted by the Minimum Step Period for the motor
				SketchyContext.hardwareController.moveMotors(0, 10000, 0);
			} else if (StringUtils.equalsIgnoreCase(action,"rightMotorBackward")){
  			    // Using delay of 0, it will be restricted by the Minimum Step Period for the motor
				SketchyContext.hardwareController.moveMotors(0, -10000, 0);
			}
			
		} catch (Throwable t){
			jsonServletResult = new JSONServletResult(Status.ERROR, "Error Saving Hardware Settings! " + t.getMessage());
		} finally {
			SketchyContext.hardwareControllerProperties = oldHardwareControllerProperties;
		}
		return jsonServletResult;
	}
	

}
