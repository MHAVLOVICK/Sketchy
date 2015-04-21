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

package com.sketchy.utils;

import com.sketchy.metadata.BooleanStringFactory;
import com.sketchy.metadata.BooleanStringTransformer;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class JSONUtils {

	public static Object fromJson(String json){
		 JSONDeserializer<Object> deserializer = new JSONDeserializer<Object>();
		 deserializer = deserializer.use(Boolean.class, new BooleanStringFactory());
		 deserializer = deserializer.use(boolean.class, new BooleanStringFactory());		 
		 return deserializer.deserialize(json);
	}
	
	public static String toJson(Object object){
		JSONSerializer serializer = new JSONSerializer();
		serializer = serializer.transform(new BooleanStringTransformer(), Boolean.class);
		serializer = serializer.transform(new BooleanStringTransformer(), boolean.class);
		return serializer.deepSerialize(object);
	}
	
	
}

