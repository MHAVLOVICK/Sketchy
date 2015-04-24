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

package com.sketchy.server.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.sketchy.server.JSONServletResult;
import com.sketchy.server.ServletAction;
import com.sketchy.server.JSONServletResult.Status;
import com.sketchy.utils.JSONUtils;

public class ManageNetworkSettings extends ServletAction {
	
	private static File INTERFACES_FILE = new File("/etc/network/interfaces");
	
	private static final List<String> REMOVE_LINES_LIST = new ArrayList<String>();
	static{
		REMOVE_LINES_LIST.add("iface wlan0 inet");
		REMOVE_LINES_LIST.add("auto wlan0");
		REMOVE_LINES_LIST.add("allow-hotplug wlan0");
		REMOVE_LINES_LIST.add("wpa-ssid");
		REMOVE_LINES_LIST.add("wpa-psk");
		REMOVE_LINES_LIST.add("wpa-roam");
		REMOVE_LINES_LIST.add("iface default inet dhcp");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONServletResult execute(HttpServletRequest request) throws Exception {
		JSONServletResult jsonServletResult = new JSONServletResult(Status.SUCCESS);
		try{
			if ((!INTERFACES_FILE.isFile()) || (!INTERFACES_FILE.exists())){
				throw new Exception("Can not update Network Settings! File '" + INTERFACES_FILE.getPath() + "' not found.");
			}
			
			if (!INTERFACES_FILE.canWrite()){
				throw new Exception("Can not update Network Settings! Can not write to File '" + INTERFACES_FILE.getPath() + "'.");
			}
			
			String responseBody = getResponseBody(request);
			if (!StringUtils.isBlank(responseBody)){
				Map<String, String> map = (Map<String, String>) JSONUtils.fromJson(responseBody);
				NetworkInfo networkInfo = new NetworkInfo();
				networkInfo.password=map.get("networkPassword");
				networkInfo.ssid=map.get("networkSSID");
				updateNetworkInfo(networkInfo);
			}
			
			// always return current network properties (except password)
			NetworkInfo networkInfo = readNetworkInfo();
			jsonServletResult.put("ssid", networkInfo.ssid);
		} catch (Throwable t){
			jsonServletResult = new JSONServletResult(Status.ERROR, "Error! " + t.getMessage());
		}
		return jsonServletResult;
	}
	
	private NetworkInfo readNetworkInfo() throws Exception {
		NetworkInfo networkInfo = new NetworkInfo();
		
		List<String> lines = FileUtils.readLines(INTERFACES_FILE);
		
		for (String line:lines){
			String ssid = StringUtils.substringAfter(line, "wpa-ssid");
			String psk = StringUtils.substringAfter(line, "wpa-psk");
			if (StringUtils.isNotBlank(ssid)){
				networkInfo.ssid=StringUtils.substringBetween(ssid, "\"", "\"");
			}
			if (StringUtils.isNotBlank(psk)){
				networkInfo.password=StringUtils.substringBetween(psk, "\"", "\"");
			}
		}
		return networkInfo;
	}
	
	public void updateNetworkInfo(NetworkInfo networkInfo) throws Exception {
		
		List<String> lines = FileUtils.readLines(INTERFACES_FILE);
		
		List<String> newLines = new ArrayList<String>();
		
		// remove everything related to wlan0 and rebuild it
		// keep everything else
		
		for (String line:lines){
			
			String normalizedLine=StringUtils.lowerCase(StringUtils.normalizeSpace(line));
			
			if (StringUtils.isNotBlank(normalizedLine)){
			
				boolean keepLine=true;
				for (String removeString:REMOVE_LINES_LIST){
					if (StringUtils.trim(normalizedLine).contains(removeString)){
						keepLine=false;
						break;
					}
				}
				if (keepLine){
					newLines.add(line);
				}
			}
		}
		
		// now build new WPA Lines
		newLines.add("");
		newLines.add("allow-hotplug wlan0");
		newLines.add("auto wlan0");
		newLines.add("");
		newLines.add("iface wlan0 inet dhcp");
		newLines.add("wpa-ssid \"" + networkInfo.ssid + "\"");
		newLines.add("wpa-psk \"" + networkInfo.password + "\"");
		
		FileUtils.writeLines(INTERFACES_FILE, newLines);
		
	}
	
	public class NetworkInfo {
		public String ssid="";
		public String password="";
	}

}
