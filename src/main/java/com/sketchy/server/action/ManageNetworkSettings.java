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
	
	private static File WPA_SUPPLICANT_FILE = new File("/etc/wpa_supplicant/wpa_supplicant.conf");

	@SuppressWarnings("unchecked")
	@Override
	public JSONServletResult execute(HttpServletRequest request) throws Exception {
		JSONServletResult jsonServletResult = new JSONServletResult(Status.SUCCESS);
		try{
			if ((!WPA_SUPPLICANT_FILE.isFile()) || (!WPA_SUPPLICANT_FILE.exists())){
				throw new Exception("Can not update wpa_supplicant.conf file! File '" + WPA_SUPPLICANT_FILE.getPath() + "' not found.");
			}
			
			if (!WPA_SUPPLICANT_FILE.canWrite()){
				throw new Exception("Can not update Network Settings! Can not write to File '" + WPA_SUPPLICANT_FILE.getPath() + "'.");
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
		
		List<String> lines = FileUtils.readLines(WPA_SUPPLICANT_FILE);
		
		for (String line:lines){
			String ssid = StringUtils.substringAfter(line, "ssid=");
			String psk = StringUtils.substringAfter(line, "psk=");
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
		
		List<String> lines = FileUtils.readLines(WPA_SUPPLICANT_FILE);
		
		List<String> newLines = new ArrayList<String>();

		for (String line:lines){
			
			if (StringUtils.containsIgnoreCase(line, "ssid=\"")){
				line="\tssid=\"" + networkInfo.ssid + "\"";
			} else if (StringUtils.containsIgnoreCase(line, "psk=\"")){
				line="\tpsk=\"" + networkInfo.password + "\"";
			}
			newLines.add(line);
		}

		FileUtils.writeLines(WPA_SUPPLICANT_FILE, newLines);
	}
	
	public class NetworkInfo {
		public String ssid="";
		public String password="";
	}

}
