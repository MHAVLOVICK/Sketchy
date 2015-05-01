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

public class RenderedImageAttributes extends ImageAttributes{
	
	public enum RotateOption{
		ROTATE_NONE, ROTATE_90, ROTATE_180, ROTATE_270
	};
	
	public enum FlipOption{
		FLIP_NONE, FLIP_VERTICAL, FLIP_HORIZONTAL, FLIP_BOTH
	};
	
	public enum RenderOption{
		RENDER_NORMAL, RENDER_HALFTONE, RENDER_EDGE_DETECTION
	};
	
	public enum CenterOption{
		CENTER_NONE, CENTER_HORIZONTAL, CENTER_VERTICAL, CENTER_BOTH
	};

	
	private String sourceImageName;
	
	private RotateOption rotateOption=RotateOption.ROTATE_NONE;
	
	private RenderOption renderOption=RenderOption.RENDER_NORMAL;
	
	private FlipOption flipOption = FlipOption.FLIP_NONE;
	
	private int brightness=50;
	
	private int contrast=50;
	
	private int threshold=50;
	
	private double penWidth;
	
	private CenterOption centerOption=CenterOption.CENTER_BOTH;
	
	private boolean invertImage=false;
	

	private String drawingSize="";

	public double getPenWidth() {
		return penWidth;
	}

	public void setPenWidth(double penWidth) {
		this.penWidth = penWidth;
	}

	public String getSourceImageName() {
		return sourceImageName;
	}

	public void setSourceImageName(String sourceImageName) {
		this.sourceImageName = sourceImageName;
	}

	public String getDrawingSize() {
		return drawingSize;
	}

	public void setDrawingSize(String drawingSize) {
		this.drawingSize = drawingSize;
	}

	public CenterOption getCenterOption() {
		return centerOption;
	}

	public void setCenterOption(CenterOption centerOption) {
		this.centerOption = centerOption;
	}

	public RotateOption getRotateOption() {
		return rotateOption;
	}

	public void setRotateOption(RotateOption rotateOption) {
		this.rotateOption = rotateOption;
	}

	public FlipOption getFlipOption() {
		return flipOption;
	}

	public void setFlipOption(FlipOption flipOption) {
		this.flipOption = flipOption;
	}

	public int getBrightness() {
		return brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public int getContrast() {
		return contrast;
	}

	public void setContrast(int contrast) {
		this.contrast = contrast;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public RenderOption getRenderOption() {
		return renderOption;
	}

	public void setRenderOption(RenderOption renderOption) {
		this.renderOption = renderOption;
	}

	public boolean isInvertImage() {
		return invertImage;
	}

	public void setInvertImage(boolean invertImage) {
		this.invertImage = invertImage;
	}
	
}
