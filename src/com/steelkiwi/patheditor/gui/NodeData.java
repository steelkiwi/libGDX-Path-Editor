/*
 * Copyright (C) 2013 Steelkiwi Development, Julia Zudikova
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.steelkiwi.patheditor.gui;

import com.steelkiwi.patheditor.consts.MenuConsts.TREE_NODE_TYPE;
import com.steelkiwi.patheditor.proj.ScreenData;

public class NodeData {
	private TREE_NODE_TYPE type;
	private Object data;
	
	public TREE_NODE_TYPE getType() {
		return type;
	}
	public void setType(TREE_NODE_TYPE type) {
		this.type = type;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		if (data instanceof String)     {
			switch (type) {
				case BG:   	return (String) data + " [image]";
				case PATH:	return (String) data + " [path]";
				default:  	return (String) data;
			}
		}
		if (data instanceof ScreenData) { return ((ScreenData) data).getName(); }
		return super.toString();
	}
}
