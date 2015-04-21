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

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import com.sketchy.SketchyContext;
import com.sketchy.image.ImageAttributes;
import com.sketchy.image.ImageProcessingThread;
import com.sketchy.image.RenderedImageAttributes;
import com.sketchy.server.HttpServer;
import com.sketchy.server.JSONServletResult;
import com.sketchy.server.ServletAction;
import com.sketchy.server.JSONServletResult.Status;


public class RenderImage extends ServletAction {
	
	@Override
	public JSONServletResult execute(HttpServletRequest request) throws Exception {
		JSONServletResult jsonServletResult = new JSONServletResult(Status.SUCCESS);
		try{
			SketchyContext.initializePlotterController();
			String responseBody = getResponseBody(request);
			
			RenderedImageAttributes renderImageAttributes = (RenderedImageAttributes) ImageAttributes.fromJson(responseBody);
			
			// imageName contains the Selected Source Image name
			String sourceImageName=renderImageAttributes.getImageName();
			renderImageAttributes.setSourceImageName(sourceImageName);
	
			File sourceImageFile =  HttpServer.getUploadFile(ImageAttributes.getImageFilename(sourceImageName));
			if (!sourceImageFile.exists()) {
				throw new Exception("Error finding Image File '" + sourceImageFile.getName() + "'!");
			}
			
			// Just get the current TimeMillis to make the file unique.. and it with rendered.png
			String currentTimeMillis = Long.toString(System.currentTimeMillis());
			String renderedName = sourceImageName + "." + currentTimeMillis + ".rendered";
			renderImageAttributes.setImageName(renderedName);
			
			// validate renderImageAttributes
			if (renderImageAttributes.getPenWidth()<=0){
				throw new Exception("Pen width must be greater than 0.0!");
			}
			
			HttpServer.imageProcessingThread = null;
			HttpServer.imageProcessingThread = new ImageProcessingThread(renderImageAttributes);
			HttpServer.imageProcessingThread.start();

		} catch (Throwable t){
			jsonServletResult = new JSONServletResult(Status.ERROR, "Error Rending Image! " + t.getMessage());
		}
		return jsonServletResult;
	}

}
