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

import java.util.ArrayList;
import java.util.List;

public class Range {
	
	private Range() {
	}
	
	public static List<double[]> getRanges(List<RangeSet> rangeSets) throws Exception {
		return getRanges(rangeSets, false);
	}
	
	public static List<double[]> getRanges(List<RangeSet> rangeSets, boolean omitFirst) throws Exception {
		List<double[]> ret = new ArrayList<double[]>();
		RangeListHook hook = new Range().new RangeListHook(ret);
		processRanges(rangeSets, hook);
		return ret;
	}
	
	public static void processRanges(List<RangeSet> rangeSets, RangeHook hook) throws Exception {
		processRanges(rangeSets, hook, false);
	}
	
	public static boolean processRanges(List<RangeSet> rangeSets, RangeHook hook, boolean omitFirst) throws Exception {
		int maxCount=0;
		boolean ret = false;
		for (RangeSet rangeSet:rangeSets){
			if (rangeSet.getIncrement()>0){ // ignore the range Set if increment isn't set
				double start=rangeSet.getStart();
				double end=rangeSet.getEnd();
				int count=(int) Math.abs(Math.ceil(((end-start)/rangeSet.getIncrement())));
				if (count>maxCount){
					maxCount=count;
				}
			}
		}
		if (maxCount>0){
			// number of increments
			for (int idx=0;idx<=maxCount;idx++){
				double[] nextElement = new double[rangeSets.size()];
				for (int i=0;i<rangeSets.size();i++){
					double rangeStart = rangeSets.get(i).getStart();
					double rangeSize = rangeSets.get(i).getEnd() - rangeStart;
					double value = ((rangeSize/(double)maxCount)*((double)idx)) + rangeStart;
					nextElement[i]=value;
				}
				if ((!omitFirst) || (idx>0)){
					hook.processRange(nextElement);
					ret=true;
				}
			}
		}
		return ret;
	}
	
	
	public static void main(String... args) throws Exception {

		List<RangeSet> rangeSets = new ArrayList<RangeSet>();
		rangeSets.add(new RangeSet(1, 30000, .55));
		rangeSets.add(new RangeSet(-8990.54, 20, .055));
		List<double[]> ranges = Range.getRanges(rangeSets, true);

		for (int idx=0;idx<(int)ranges.size();idx++){
			double rangeX = ranges.get(idx)[0];
			double rangeY = ranges.get(idx)[1];
			System.out.println("idx: " + idx + " rangeX: " + rangeX + "   rangeY: " + rangeY);			
		}
	}
	
	public class RangeListHook implements RangeHook{
		private List<double[]> list = null;
		public RangeListHook(List<double[]> list){
			this.list = list;
		}
		
		@Override
		public void processRange(double[] values) throws Exception {
			list.add(values);
		}
	}

}
