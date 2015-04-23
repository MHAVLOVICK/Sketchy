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

package com.sketchy.server;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.sketchy.server.JSONServletResult.Status;

public final class UpgradeUploadServlet extends HttpServlet {
	
	private static final int MAX_SIZE = 10000000; // 10Mbytes
	private static final long serialVersionUID = -6172468221875699663L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	JSONServletResult jsonServletResult = new JSONServletResult(Status.SUCCESS);
    	try{
    		
    		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
    		if (isMultipart){
    			    			
	    		DiskFileItemFactory factory = new DiskFileItemFactory();
	    		factory.setRepository(FileUtils.getTempDirectory());
	    		factory.setSizeThreshold(MAX_SIZE);

	    		ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
		    	List<FileItem> files = servletFileUpload.parseRequest(request);
		    	String version="";
		    	for (FileItem fileItem:files){
		    		String uploadFileName = fileItem.getName();
		    		if (StringUtils.isNotBlank(uploadFileName)){

		    			JarInputStream jarInputStream = null;
		    			try{
			    			// check to make sure it's a Sketchy File with a Manifest File
			    			jarInputStream=new JarInputStream(fileItem.getInputStream(), true);
			    			Manifest manifest = jarInputStream.getManifest();
			    			if (manifest==null){
			    				throw new Exception("Invalid Upgrade File!");
			    			}
			    			Attributes titleAttributes=manifest.getMainAttributes();
			    			if ((titleAttributes==null) || (!StringUtils.containsIgnoreCase(titleAttributes.getValue("Implementation-Title"), "Sketchy"))){
			    				throw new Exception("Invalid Upgrade File!");
			    			}
			    			version = titleAttributes.getValue("Implementation-Version");
		    			} catch (Exception e){
		    				throw new Exception("Invalid Upgrade File!");
		    			} finally {
		    				IOUtils.closeQuietly(jarInputStream);
		    			}
		    			// save new .jar file as "ready"
		    			fileItem.write(new File("Sketchy.jar.ready"));
		    			jsonServletResult.put("version", version);
		    		}
		    	}
    		}
    	} catch (Exception e){
    		jsonServletResult = new JSONServletResult(Status.ERROR, e.getMessage());
    	}
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(jsonServletResult.toJSONString());
    }	

}
