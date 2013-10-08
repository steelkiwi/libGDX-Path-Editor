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

package com.steelkiwi.patheditor.proj;

import java.util.ArrayList;

public class ProjectData {
	private String name;
	private String path;
	private ArrayList<ScreenData> screens;
	
	public ProjectData() {
		screens = new ArrayList<ScreenData>();
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public ArrayList<ScreenData> getScreens() {
		return screens;
	}
	
	public void setScreens(ArrayList<ScreenData> screens) {
		this.screens = screens;
	}
	
	public boolean hasScreens() {
		return ((screens != null) && (screens.size() > 0));
	}
}
