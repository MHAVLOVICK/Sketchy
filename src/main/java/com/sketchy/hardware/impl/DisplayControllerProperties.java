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

package com.sketchy.hardware.impl;

import com.sketchy.hardware.HardwareController;
import com.sketchy.hardware.HardwareControllerProperties;
import com.sketchy.metadata.MetaData;
import com.sketchy.metadata.MetaDataGroup;
import com.sketchy.metadata.MetaDataProperty;
import com.sketchy.metadata.MetaDataProperty.AttributeType;

public class DisplayControllerProperties extends HardwareControllerProperties {
	
	private static final long serialVersionUID = -8825894320870849739L;

	@Override
	public Class<? extends HardwareController> getImplementationClass() {
		return DisplayController.class; 
	}
	
	@Override	
	public MetaData getMetaData() {
		
		MetaData metaData = new MetaData();

		MetaDataGroup displayGroup = new MetaDataGroup("Display");
		displayGroup.add(new MetaDataProperty("windowWidth","Window Width",AttributeType.Number));
		displayGroup.add(new MetaDataProperty("windowHeight","Window Height",AttributeType.Number));
		metaData.add(displayGroup);
		
		MetaDataGroup canvasGroup = new MetaDataGroup("Display Properties");
		canvasGroup.add(new MetaDataProperty("frameWidth","Frame Width",AttributeType.Decimal));
		canvasGroup.add(new MetaDataProperty("frameHeight","Frame Height",AttributeType.Decimal));
		canvasGroup.add(new MetaDataProperty("canvasWidth","Canvas Width",AttributeType.Decimal));
		canvasGroup.add(new MetaDataProperty("canvasHeight","Canvas Height",AttributeType.Decimal));
		canvasGroup.add(new MetaDataProperty("yPosOffset","Home Y Position Offset",AttributeType.Decimal,true));
		metaData.add(canvasGroup);

			
		return metaData;
	}
	
	private int windowWidth=800;
	private int windowHeight=600;
	
	private double frameWidth=375;
	private double frameHeight=450;
	private double canvasWidth=240;
	private double canvasHeight=280;
	
	private double yPosOffset=-20; // Pen starts at 10mm above the Canvas Area
	
	public double getFrameWidth() {
		return frameWidth;
	}

	public void setFrameWidth(double frameWidth) {
		this.frameWidth = frameWidth;
	}

	public double getFrameHeight() {
		return frameHeight;
	}

	public void setFrameHeight(double frameHeight) {
		this.frameHeight = frameHeight;
	}

	public double getCanvasWidth() {
		return canvasWidth;
	}

	public void setCanvasWidth(double canvasWidth) {
		this.canvasWidth = canvasWidth;
	}

	public double getCanvasHeight() {
		return canvasHeight;
	}

	public void setCanvasHeight(double canvasHeight) {
		this.canvasHeight = canvasHeight;
	}
	
	public int getWindowWidth() {
		return windowWidth;
	}


	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}


	public int getWindowHeight() {
		return windowHeight;
	}


	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}


	public double getyPosOffset() {
		return yPosOffset;
	}


	public void setyPosOffset(double yPosOffset) {
		this.yPosOffset = yPosOffset;
	}

}
