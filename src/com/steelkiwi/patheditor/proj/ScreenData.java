package com.steelkiwi.patheditor.proj;

import com.steelkiwi.patheditor.widgets.GdxImage;
import com.steelkiwi.patheditor.widgets.GdxPath;

public class ScreenData {
	private String name;
	private int width;
	private int height;
	
	private String xmlPath;
	private String jsonPath;
	
	private GdxImage bgImage;
	private GdxPath path;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public String getXmlPath() {
		return xmlPath;
	}
	
	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}
	
	public String getJsonPath() {
		return jsonPath;
	}
	
	public void setJsonPath(String jsonPath) {
		this.jsonPath = jsonPath;
	}
	
	public GdxImage getBgImage() {
		return bgImage;
	}

	public void setBgImage(GdxImage bgImage) {
		this.bgImage = bgImage;
	}

	public GdxPath getPath() {
		return path;
	}

	public void setPath(GdxPath path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return String.format("%s (%dx%d)", name, width, height);
	}
}
