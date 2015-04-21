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

package com.sketchy.image;

import com.sketchy.utils.JSONUtils;


// Represents a Physical Image File
public abstract class ImageAttributes {
	
	public static final String DATA_FILE_EXTENSION = ".dat";
	public static final String IMAGE_FILE_EXTENSION = ".png";
	
	public static String getDataFilename(String imageName) {
		return imageName + DATA_FILE_EXTENSION;
	}

	public static String getImageFilename(String imageName) {
		return imageName + IMAGE_FILE_EXTENSION;
	}
	
	private String imageName=""; // the image name as displayed in the Grid
	
	private int width;
	private int height;
	
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getImageName() {
		return imageName;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}

	
	public String getDataFilename() {
		return getDataFilename(imageName);
	}

	public String getImageFilename() {
		return getImageFilename(imageName);
	}
	
	public String toJson(){
		return JSONUtils.toJson(this);
	}
	
	public static ImageAttributes fromJson(String json){
		return (ImageAttributes) JSONUtils.fromJson(json);
	}
	
}
