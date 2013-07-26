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
