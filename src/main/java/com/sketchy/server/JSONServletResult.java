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


import java.util.LinkedHashMap;
import java.util.Map;

import com.sketchy.utils.JSONUtils;

public final class JSONServletResult {
	
	private static final String KEY_STATUS="status";
	private static final String KEY_MESSAGE="message";
	
	public enum Status{
		SUCCESS, ERROR;
	}
	
	private Map<String, Object> map = new LinkedHashMap<>();

	public JSONServletResult(Status status){
		map.put(KEY_STATUS, status);
		setMessage("");
	}
	
	public JSONServletResult(Status status, String message){
		map.put(KEY_STATUS, status);
		setMessage(message);
	}

	public void setMessage(String message){
		map.put(KEY_MESSAGE, message);
	}
	
	public void put(String key, Object value){
		if (key.equals(KEY_STATUS)){
			throw new RuntimeException("Can not set value with key '" + KEY_STATUS + "'! Internal Use only!");
		}
		if (key.equals(KEY_MESSAGE)){
			throw new RuntimeException("Can not set value with key '" + KEY_MESSAGE + "'! Internal Use only!");
		}
		map.put(key, value);
	}
	
	public String toJSONString() {
		return JSONUtils.toJson(map);
	}
	
}
