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

package com.sketchy.metadata;

import java.util.Map;

public class MetaDataProperty {

	public enum AttributeType{
		String, Number, Decimal, Boolean, List, Button;
		public String getName() {
			return name();
		}
	}
	
	public MetaDataProperty(String id, String name, AttributeType type){
		this.id=id;
		this.name=name;
		this.type=type;
		this.required=false;
	}
	
	public MetaDataProperty(String id, String name, AttributeType type, boolean isRequired){
		this.id=id;
		this.name=name;
		this.type=type;
		this.required=isRequired;
	}

	// List Values are assumed to be required
	public MetaDataProperty(String id, String name, AttributeType type, Map<String, String> listValues){
		this.id=id;
		this.name=name;
		this.type=type;
		this.required=true;
		this.listValues=listValues;
	}
	
	private String id="";
	private String name="";
	private AttributeType type=null;
	private boolean required=false;
	private Map<String, String> listValues=null;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public AttributeType getType() {
		return type;
	}
	public void setType(AttributeType type) {
		this.type = type;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}

	public Map<String, String> getListValues() {
		return listValues;
	}

	public void setListValues(Map<String, String> listValues) {
		this.listValues = listValues;
	}
}
