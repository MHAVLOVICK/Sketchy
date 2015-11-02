package com.sketchy.utils;

public class MemoryUtils {

	
	public static long getAvailableMemory(){
		long freeMemory = Runtime.getRuntime().freeMemory();
		return getMaximumMemory()-freeMemory;
	}
	
	public static long getMaximumMemory(){
		
		long maximumMemory = Runtime.getRuntime().maxMemory();
		long totalMemory = Runtime.getRuntime().totalMemory();
		
		if ((maximumMemory<=0) || (maximumMemory==Long.MAX_VALUE)) {
			// if maximum memory can not be determined or isn't defined, then default to the totalMemory
			maximumMemory=totalMemory;
		}
		return maximumMemory;
	}

	
}
