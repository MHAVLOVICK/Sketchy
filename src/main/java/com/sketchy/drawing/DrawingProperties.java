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

import java.util.ArrayList;
import java.util.List;

import com.sketchy.SketchyContext;
import com.sketchy.metadata.MetaData;
import com.sketchy.metadata.MetaDataGroup;
import com.sketchy.metadata.MetaDataObject;
import com.sketchy.metadata.MetaDataProperty;
import com.sketchy.metadata.MetaDataProperty.AttributeType;

public class DrawingProperties extends MetaDataObject {

	private static final long serialVersionUID = 5559751191321106491L;

	@Override	
	public MetaData getMetaData() {
		
		MetaData metaData = new MetaData();
		
		MetaDataGroup defaultGroup = new MetaDataGroup("Defaults");
		defaultGroup.add(new MetaDataProperty("defaultPenWidth","Default Pen Width (mm)",AttributeType.List, SketchyContext.PEN_SIZES));
		metaData.add(defaultGroup);

		MetaDataGroup drawing = new MetaDataGroup("Drawing Sizes");
		drawing.add(new MetaDataProperty("drawingSizes[0]","Drawing Size",AttributeType.String,true));
		drawing.add(new MetaDataProperty("drawingSizes[1]","Drawing Size",AttributeType.String,true));
		drawing.add(new MetaDataProperty("drawingSizes[2]","Drawing Size",AttributeType.String,true));
		drawing.add(new MetaDataProperty("drawingSizes[3]","Drawing Size",AttributeType.String,true));
		drawing.add(new MetaDataProperty("drawingSizes[4]","Drawing Size",AttributeType.String,true));
		metaData.add(drawing);
		
		return metaData;
	}
	
	private Double defaultPenWidth=1.0;

	private List<String> drawingSizes = new ArrayList<String>();
	
	
	public Double getDefaultPenWidth() {
		return defaultPenWidth;
	}

	public void setDefaultPenWidth(Double defaultPenWidth) {
		this.defaultPenWidth = defaultPenWidth;
	}

	public List<String> getDrawingSizes() {
		return drawingSizes;
	}

	public void setDrawingSizes(List<String> drawingSizes) {
		this.drawingSizes = drawingSizes;
	}

	
}
