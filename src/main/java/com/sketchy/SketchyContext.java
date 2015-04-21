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

package com.sketchy;

import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.sketchy.drawing.DrawingProperties;
import com.sketchy.hardware.HardwareController;
import com.sketchy.hardware.HardwareControllerProperties;
import com.sketchy.pathing.PathingProcessor;
import com.sketchy.pathing.PathingProcessorProperties;
import com.sketchy.plotter.PlotterController;
import com.sketchy.plotter.PlotterControllerProperties;
import com.sketchy.utils.JSONUtils;

public enum SketchyContext {
	INSTANCE;
	
	private static NumberFormat nf = NumberFormat.getInstance();

	public static final Map<String, String> PEN_SIZES = new LinkedHashMap<String, String>();
	static{
		nf.setMinimumIntegerDigits(1);
		nf.setMinimumFractionDigits(1);
		
		for (double penSize=0.1;penSize<=3.01;penSize+=.1){
			PEN_SIZES.put(nf.format(penSize), nf.format(penSize) + "mm");
		}
	}
	
	public static DrawingProperties drawingProperties;

	public static HardwareController hardwareController;
	public static HardwareControllerProperties hardwareControllerProperties;
	
	public static PlotterController plotterController;
	public static PlotterControllerProperties plotterControllerProperties;
	
	public static PathingProcessor pathingProcessor;
	public static PathingProcessorProperties pathingProcessorProperties;

	@SuppressWarnings("unchecked")
	public static void load(File jsonFile) throws Exception {
		synchronized(SketchyContext.class){
			if (jsonFile.exists()){
				
				String json = FileUtils.readFileToString(jsonFile);
				Map<String, Object> saveMap = (Map<String, Object>) JSONUtils.fromJson(json);
			
				hardwareControllerProperties = (HardwareControllerProperties) saveMap.get("hardwareControllerProperties");
					
				plotterControllerProperties = (PlotterControllerProperties) saveMap.get("plotterControllerProperties");
				
				pathingProcessorProperties = (PathingProcessorProperties) saveMap.get("pathingProcessorProperties");

				drawingProperties = (DrawingProperties) saveMap.get("drawingProperties");
			}
		}
	}
	
	public static void save(File jsonFile) throws Exception {
		synchronized(SketchyContext.class){
			
			Map<String, Object> saveMap = new HashMap<String, Object>();
			saveMap.put("hardwareControllerProperties", hardwareControllerProperties);
			saveMap.put("plotterControllerProperties", plotterControllerProperties);
			saveMap.put("pathingProcessorProperties",pathingProcessorProperties);
			saveMap.put("drawingProperties",drawingProperties);
			
			FileUtils.writeStringToFile(jsonFile, JSONUtils.toJson(saveMap));
		}
	}
	
	public static void initializeHardwareController() throws Exception {
		if (hardwareControllerProperties==null){
			throw new Exception("Hardware Properties must be configured!");
		}
		if ((hardwareController==null) || (!hardwareController.getProperties().equals(hardwareControllerProperties))){
			hardwareController = HardwareController.newInstance(hardwareControllerProperties);
		} 
	}
	
	public static void initializePlotterController() throws Exception {
		if (plotterControllerProperties==null){
			throw new Exception("Plotter Properties must be configured!");
		}		
		if ((plotterController==null) || (!plotterController.getProperties().equals(plotterControllerProperties))){
			plotterController = PlotterController.newInstance(plotterControllerProperties);
		}
	}

	public static void initializePathingProcessor() throws Exception {
		if (pathingProcessorProperties==null){
			throw new Exception("Pathing Properties must be configured!");
		}
		if ((pathingProcessor==null) || (!pathingProcessor.getProperties().equals(pathingProcessorProperties))){
			pathingProcessor = PathingProcessor.newInstance(pathingProcessorProperties);
		}
	}

	public static void initializeDrawingProperties() throws Exception {
		if (drawingProperties==null){
			drawingProperties = new DrawingProperties();
		}
	}




}
