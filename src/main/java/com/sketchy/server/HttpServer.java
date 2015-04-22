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

import javax.servlet.MultipartConfigElement;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.sketchy.SketchyContext;
import com.sketchy.image.ImageProcessingThread;
import com.sketchy.pathing.DrawingProcessorThread;

public class HttpServer {

	public static final File SKETCHY_PROPERTY_FILE=new File("SketchyProperties.json");
	public static final File FILE_UPLOAD_DIRECTORY = new File("upload");
	
	public static ImageProcessingThread imageProcessingThread = null;
	public static DrawingProcessorThread drawingProccessorThread = null;

	public static File getUploadFile(String filename){
    	return new File(FILE_UPLOAD_DIRECTORY.getPath() + File.separator + filename);
	}
	
	public void start() throws Exception {
    	if (!FILE_UPLOAD_DIRECTORY.exists()) {
    		if (!FILE_UPLOAD_DIRECTORY.mkdir()) {
    			throw new Exception("Error Creating Upload Directory '" + FILE_UPLOAD_DIRECTORY.getAbsolutePath() + "!");
    		}
    	}

		Server server = new Server(80);

        // File Upload Handler
        ServletHolder fileUploadHolder = new ServletHolder(new FileUploadServlet());
        MultipartConfigElement multipartConfig = new MultipartConfigElement(FileUtils.getTempDirectory().getPath());
        fileUploadHolder.getRegistration().setMultipartConfig(multipartConfig);
        
        ServletHandler servletHandler = new ServletHandler();
        ServletContextHandler servletContext = new ServletContextHandler();
        servletContext.setHandler(servletHandler);
        servletContext.addServlet(new ServletHolder(new JsonServlet()), "/servlet/*");
        servletContext.addServlet(fileUploadHolder, "/fileUpload/*");
        
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        ContextHandler resourceContext = new ContextHandler();
        resourceContext.setWelcomeFiles(new String[] { "index.html" });
        resourceContext.setContextPath("/");
        resourceContext.setResourceBase("./resources/html");
        resourceContext.setHandler(resourceHandler);
        
        ResourceHandler uploadResourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        ContextHandler uploadResourceContext = new ContextHandler();
        uploadResourceContext.setContextPath("/upload");
        uploadResourceContext.setResourceBase("./upload");
        uploadResourceContext.setHandler(uploadResourceHandler);
        
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { resourceContext, servletContext, uploadResourceHandler});
        server.setHandler(contexts);

        server.start();
        server.join();
	}
	
	
    public static void main(String[] args) throws Exception {
    	try{
    		SketchyContext.load(SKETCHY_PROPERTY_FILE);
    	} catch (Exception e){
    		System.out.println("Error Loading Sketchy Property file! " + e.getMessage());
    	}
    	HttpServer server = new HttpServer();
    	server.start();
    }
}
