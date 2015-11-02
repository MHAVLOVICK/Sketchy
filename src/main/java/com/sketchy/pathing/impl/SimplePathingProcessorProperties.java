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

package com.sketchy.pathing.impl;


import java.util.LinkedHashMap;
import java.util.Map;

import com.sketchy.metadata.MetaData;
import com.sketchy.metadata.MetaDataGroup;
import com.sketchy.metadata.MetaDataProperty;
import com.sketchy.metadata.MetaDataProperty.AttributeType;
import com.sketchy.pathing.PathingProcessor;
import com.sketchy.pathing.PathingProcessorProperties;
import com.sketchy.pathing.impl.SimplePathingProcessor.PathingBacktrackOption;
import com.sketchy.pathing.impl.SimplePathingProcessor.PathingDirectionPreference;
import com.sketchy.pathing.impl.SimplePathingProcessor.PathingPattern;

public class SimplePathingProcessorProperties extends PathingProcessorProperties {
	
	private static final long serialVersionUID = -2722385158493064636L;

	private static final Map<String, String> pathingPatternList = new LinkedHashMap<String, String>();
	private static final Map<String, String> pathingDirectionPreferenceList = new LinkedHashMap<String, String>();
	private static final Map<String, String> pathingBacktrackOptionList = new LinkedHashMap<String, String>();

	
	static{
		pathingPatternList.put(PathingPattern.FollowLeft.name(), PathingPattern.FollowLeft.label);
		pathingPatternList.put(PathingPattern.FollowRight.name(), PathingPattern.FollowRight.label);
		pathingPatternList.put(PathingPattern.Clockwise.name(), PathingPattern.Clockwise.label);
		pathingPatternList.put(PathingPattern.CounterClockwise.name(), PathingPattern.CounterClockwise.label);
		pathingPatternList.put(PathingPattern.UpperLeftToLowerRight.name(), PathingPattern.UpperLeftToLowerRight.label);
		pathingPatternList.put(PathingPattern.UpperRightToLowerLeft.name(), PathingPattern.UpperRightToLowerLeft.label);
		pathingPatternList.put(PathingPattern.LowerLeftToUpperRight.name(), PathingPattern.LowerLeftToUpperRight.label);
		pathingPatternList.put(PathingPattern.LowerRightToUpperLeft.name(), PathingPattern.LowerRightToUpperLeft.label);
		pathingPatternList.put(PathingPattern.PreferNonDiagonals.name(), PathingPattern.PreferNonDiagonals.label);
		pathingPatternList.put(PathingPattern.SkipDiagonals.name(), PathingPattern.SkipDiagonals.label);		
		pathingPatternList.put(PathingPattern.Random.name(), PathingPattern.Random.label);
	}
		
	static {
		pathingDirectionPreferenceList.put(PathingDirectionPreference.NoPreference.name(), PathingDirectionPreference.NoPreference.label);
		pathingDirectionPreferenceList.put(PathingDirectionPreference.SameDirection.name(), PathingDirectionPreference.SameDirection.label);
		pathingDirectionPreferenceList.put(PathingDirectionPreference.DifferentDirection.name(), PathingDirectionPreference.DifferentDirection.label);
	}
	
	static {
		pathingBacktrackOptionList.put(PathingBacktrackOption.Backtrack.name(), PathingBacktrackOption.Backtrack.label);
		pathingBacktrackOptionList.put(PathingBacktrackOption.LiftPen.name(), PathingBacktrackOption.LiftPen.label);
	}
	
	@Override
	public Class<? extends PathingProcessor> getImplementationClass() {
		return SimplePathingProcessor.class; 
	}
	
	
	@Override	
	public MetaData getMetaData() {
		
		MetaData metaData = new MetaData();
		
		metaData.setHelpUrl("/docs/SimplePathingProcessorOptions.html");
		
		MetaDataGroup patternGroup = new MetaDataGroup("Pathing Logic");
		patternGroup.add(new MetaDataProperty("pathingPattern","Pattern",AttributeType.List, pathingPatternList));
		patternGroup.add(new MetaDataProperty("pathingDirectionPreference","Direction Preference",AttributeType.List, pathingDirectionPreferenceList));
		patternGroup.add(new MetaDataProperty("pathingBacktrackOption","End of Line Behavior",AttributeType.List, pathingBacktrackOptionList));
		patternGroup.add(new MetaDataProperty("stripingLengthInMM", "Vertical Striping Length (MM)", AttributeType.Decimal, false));
		metaData.add(patternGroup);
		
		return metaData;
	}

	private PathingPattern pathingPattern=PathingPattern.Random;
	private PathingDirectionPreference pathingDirectionPreference=PathingDirectionPreference.NoPreference;
	private PathingBacktrackOption pathingBacktrackOption = PathingBacktrackOption.Backtrack;
	private double stripingLengthInMM=0;

	public double getStripingLengthInMM() {
		return stripingLengthInMM;
	}

	public void setStripingLengthInMM(double stripingLengthInMM) {
		this.stripingLengthInMM = stripingLengthInMM;
	}

	public PathingPattern getPathingPattern() {
		return pathingPattern;
	}

	public void setPathingPattern(PathingPattern pathingPattern) {
		this.pathingPattern = pathingPattern;
	}

	public PathingDirectionPreference getPathingDirectionPreference() {
		return pathingDirectionPreference;
	}

	public void setPathingDirectionPreference(PathingDirectionPreference pathingDirectionPreference) {
		this.pathingDirectionPreference = pathingDirectionPreference;
	}

	public PathingBacktrackOption getPathingBacktrackOption() {
		return pathingBacktrackOption;
	}

	public void setPathingBacktrackOption(PathingBacktrackOption pathingBacktrackOption) {
		this.pathingBacktrackOption = pathingBacktrackOption;
	}
	
}
