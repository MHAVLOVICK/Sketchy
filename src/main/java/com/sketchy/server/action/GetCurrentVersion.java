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
import java.io.FileInputStream;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;

import com.sketchy.server.JSONServletResult;
import com.sketchy.server.ServletAction;
import com.sketchy.server.JSONServletResult.Status;

public class GetCurrentVersion extends ServletAction {
	@Override
	public JSONServletResult execute(HttpServletRequest request) throws Exception {
		JSONServletResult jsonServletResult = new JSONServletResult(Status.SUCCESS);
		String version="";
		try{
			File sketchyFile = new File("Sketchy.jar");
			if (!sketchyFile.exists()){
				throw new Exception("Can't find Sketchy.jar file!");
			}
			JarInputStream jarInputStream = null;
			try{
    			// check to make sure it's a Sketchy File with a Manifest File
    			jarInputStream=new JarInputStream(new FileInputStream(sketchyFile), true);
    			Manifest manifest = jarInputStream.getManifest();
    			if (manifest==null){
    				throw new Exception("Manifest file not found.");
    			}
    			Attributes titleAttributes=manifest.getMainAttributes();
    			version = titleAttributes.getValue("Implementation-Version");
			} catch (Exception e){
				throw new Exception("Invalid Upgrade File!");
			} finally {
				IOUtils.closeQuietly(jarInputStream);
			}
			
			jsonServletResult.put("currentVersion", version);
		} catch (Throwable t){
			jsonServletResult = new JSONServletResult(Status.ERROR, "Error getting Current Version! " + t.getMessage());
		}
    	return jsonServletResult;
	}

}
