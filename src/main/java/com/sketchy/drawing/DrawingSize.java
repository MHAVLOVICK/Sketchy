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

package com.sketchy.drawing;

import java.text.NumberFormat;

import org.apache.commons.lang3.StringUtils;

public class DrawingSize {

	private static final NumberFormat nf = NumberFormat.getInstance();
	static{
		nf.setGroupingUsed(false);
		nf.setMaximumFractionDigits(5);
		nf.setMaximumFractionDigits(0);
		nf.setMinimumIntegerDigits(1);
		nf.setMaximumIntegerDigits(5);
	}
	
	private double width;
	private double height;
	private String value="";
	
	public DrawingSize(double width, double height){
		this.width = width;
		this.height = height;
		this.value = nf.format(width) + " x " + nf.format(height);
	}
	
	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public String getValue() {
		return value;
	}

	// Format is width[mm] x height[mm] <comments>
	public static DrawingSize parse(String string) {
		double width=0;
		double height=0;
		
		String upperCasedString = StringUtils.upperCase(string);
		String widthString = StringUtils.substringBefore(upperCasedString, "X").trim();
		String heightString = StringUtils.substringAfter(upperCasedString, "X").trim();
		
		widthString = StringUtils.substringBeforeLast(widthString, "MM");
		heightString = StringUtils.substringBefore(heightString, " ");
		heightString = StringUtils.substringBeforeLast(heightString, "MM");
		
		try{
			width = Double.parseDouble(widthString);
			height = Double.parseDouble(heightString);
		} catch (Exception e){
			throw new RuntimeException("Invalid Drawing Size! '" + string + "'");
		}
		
		if ((width<=0) || (height<=0)) {
			throw new RuntimeException("Invalid Drawing Size! '" + string + "'");
		}
		return new DrawingSize(width, height);
	}
	
}
