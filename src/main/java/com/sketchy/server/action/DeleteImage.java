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

import org.apache.commons.io.FileUtils;

import com.sketchy.image.ImageAttributes;
import com.sketchy.server.HttpServer;
import com.sketchy.server.JSONServletResult;
import com.sketchy.server.ServletAction;
import com.sketchy.server.JSONServletResult.Status;

public class DeleteImage extends ServletAction {

	@Override
	public JSONServletResult execute(HttpServletRequest request) throws Exception {
		JSONServletResult jsonServletResult = new JSONServletResult(Status.SUCCESS);
		String imageName = request.getParameter("imageName");

		// if it doesn't end with .rendered, this is a source image.. delete all the rendered files 
		if (!imageName.endsWith(".rendered")){
			File uploadDirectory = HttpServer.FILE_UPLOAD_DIRECTORY;
			File[] files = uploadDirectory.listFiles();

			// delete all the rendered files
			for (int idx=0;idx<files.length;idx++){
				File file = files[idx];
				String fileName = file.getName();
				if (fileName.startsWith(imageName)){
					if (fileName.endsWith(".rendered.png") ||
						fileName.endsWith(".rendered.dat")) {
						FileUtils.deleteQuietly(file);
					}
				}
			}
		}
		
		File dataFile = HttpServer.getUploadFile(ImageAttributes.getDataFilename(imageName));
		File imageFile = HttpServer.getUploadFile(ImageAttributes.getImageFilename(imageName));
		FileUtils.deleteQuietly(dataFile);
		FileUtils.deleteQuietly(imageFile);
		
		return jsonServletResult;
	}

}
