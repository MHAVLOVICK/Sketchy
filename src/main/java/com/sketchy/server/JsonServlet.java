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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.sketchy.server.JSONServletResult.Status;

public final class JsonServlet extends HttpServlet {

	private static final String ACTION_PACKAGE = "com.sketchy.server.action.";
	
	private static final long serialVersionUID = 6826724318580539026L;
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        String pathInfo = request.getPathInfo();
        if (pathInfo.startsWith("/")) pathInfo = StringUtils.substringAfter(pathInfo, "/");
        String action = ACTION_PACKAGE + pathInfo;
        JSONServletResult jsonServletResult = null;
        try{
	
        	ServletAction servletAction = ServletAction.getInstance(action);

        	jsonServletResult = servletAction.execute(request);
        } catch (ClassNotFoundException e){
        	jsonServletResult = new JSONServletResult(Status.ERROR, "ServletAction '" + pathInfo + "' not found! " + e.getMessage());
        } catch (Exception e){
        	jsonServletResult = new JSONServletResult(Status.ERROR, "Unexpected Exception from ServletAction '" + pathInfo + "'! " + e.getMessage());
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(jsonServletResult.toJSONString());

    }	

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doGet(request, response);
    }	
}
