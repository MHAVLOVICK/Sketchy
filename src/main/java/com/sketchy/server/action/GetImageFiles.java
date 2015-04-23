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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.sketchy.image.ImageAttributes;
import com.sketchy.image.SourceImageAttributes;
import com.sketchy.server.HttpServer;
import com.sketchy.server.JSONServletResult;
import com.sketchy.server.ServletAction;
import com.sketchy.server.JSONServletResult.Status;

public class GetImageFiles extends ServletAction {
	@Override
	public JSONServletResult execute(HttpServletRequest request) throws Exception {
		JSONServletResult jsonServletResult = new JSONServletResult(Status.SUCCESS);
		try{
	    	File[] files = HttpServer.IMAGE_UPLOAD_DIRECTORY.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File arg0, String filename) {
					return (StringUtils.endsWithIgnoreCase(filename, ".dat") && 
							(!StringUtils.endsWithIgnoreCase(filename, "rendered.dat")));
				}
			});
	
	    	List<Object> rows = new ArrayList<Object>();
	    	for (File file:files){
	
		    	Map<String, Object> row = new HashMap<String, Object>();
		    	File dimFile = new File(file.getPath());
		    	if (dimFile.exists()){
		    		String json = FileUtils.readFileToString(dimFile);
		    		SourceImageAttributes sourceImageAttributes = (SourceImageAttributes) ImageAttributes.fromJson(json);
			    	row.put("imageSize", Integer.toString(sourceImageAttributes.getWidth()) + " x " + Integer.toString(sourceImageAttributes.getHeight()));
			    	row.put("imageName", sourceImageAttributes.getImageName());
			    	row.put("filename", sourceImageAttributes.getImageFilename());
		    	}
		    	
		    	rows.add(row);
	    	}
	    	
			jsonServletResult.put("rows", rows);
		} catch (Throwable t){
			jsonServletResult = new JSONServletResult(Status.ERROR, "Error! " + t.getMessage());
		}
		
		return jsonServletResult;
	}

}
