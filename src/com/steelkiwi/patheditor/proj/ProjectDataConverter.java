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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.XmlWriter;
import com.steelkiwi.patheditor.gui.dlg.CreateImageDialog.widgetType;
import com.steelkiwi.patheditor.path.Path;
import com.steelkiwi.patheditor.path.PathVertex;
import com.steelkiwi.patheditor.widgets.GdxImage;
import com.steelkiwi.patheditor.widgets.GdxPath;
import com.steelkiwi.patheditor.widgets.WidgetManager;

public class ProjectDataConverter {
	private final static String PATH_SEPARATOR = System.getProperty("file.separator");
	private final static String PROJECT_EXT = ".proj";
	
	// ==============================================================
	// create project description file
	// ==============================================================
	
	public static void saveProject(ProjectData projData) throws Exception {
		File oldProjDir = new File(projData.getPath());
		if (!oldProjDir.exists() || !oldProjDir.isDirectory()) { 
			oldProjDir.mkdir(); 
		}
		
		//rename old proj dir
		String tempDirPath = projData.getPath() + "_" + new SimpleDateFormat("yy.MM.dd_hh:mm:ss").format(Calendar.getInstance().getTime()).toString();
		File tempProjDir = new File(tempDirPath);
		if (!oldProjDir.renameTo(tempProjDir)) { throw new Exception(); }
		
		//create new proj dir
		File newProjDir = new File(projData.getPath());
		newProjDir.mkdir();
		
		try {
			//fill new proj dir
			saveProjectData(projData);
			
			//delete old proj dir
			oldProjDir = new File(tempDirPath);
			deleteDir(oldProjDir);
		}
		catch (Exception e) {
			//delete newly created proj dir
			deleteDir(newProjDir);
			
			//return old proj dir
			oldProjDir  = new File(tempDirPath);
			tempProjDir = new File(projData.getPath());
			oldProjDir.renameTo(tempProjDir);
			
			throw e;
		}
	}
	
	private static void saveProjectData(ProjectData projData) throws Exception {
		String path = projData.getPath() + PATH_SEPARATOR + projData.getName() + PROJECT_EXT;
		File projectFile = new File(path);
		
		if (projectFile.exists()) { projectFile.delete(); }
		projectFile.createNewFile();
		
		StringWriter strWriter = new StringWriter();
		XmlWriter xmlWriter = new XmlWriter(strWriter);
		
		xmlWriter.element("project");
		
		xmlWriter.element("name", projData.getName());
		xmlWriter.element("path", projData.getPath());
		
		if (projData.getScreens() != null) {
			for (int i=0; i<projData.getScreens().size(); i++) {
				xmlWriter.element("screen")
			 			 	.element("xml",  projData.getScreens().get(i).getXmlPath())
			 			 	.element("json", projData.getScreens().get(i).getJsonPath())
			 			 .pop();	
			}
		}
		
		xmlWriter.pop();
		xmlWriter.close();
		
		FileWriter writer = new FileWriter(new File(path));
		writer.write(strWriter.toString());
		writer.close();
		
		if (projData.getScreens() != null) {
			saveScreens(projData);
		}
	}

	private static void saveScreens(ProjectData projData) throws Exception {
		ScreenData scrData;
		for (int i=0; i<projData.getScreens().size(); i++) {
			scrData = projData.getScreens().get(i);
			createScreenDir(projData.getPath(), scrData.getName());
			saveScreenToXML(scrData, projData.getPath());
			saveScreenToJSON(scrData, projData.getPath());
			if ((scrData.getPath() != null) && (scrData.getPath().getPath() != null)) {
				savePath(scrData);
			}
		}
	}
	
	private static void createScreenDir(String projPath, String scrDirName) {
		String dir = projPath + PATH_SEPARATOR + scrDirName;
		File scrDir = new File(dir);
		if (!scrDir.exists()) {
			scrDir.mkdir();
		} else if (!scrDir.isDirectory()) {
			scrDir.delete();
			scrDir.mkdir();
		}
	}
	
	private static void saveScreenToXML(ScreenData scrData, String projPath) throws Exception {
		String path = scrData.getXmlPath();
		File scrFile = new File(path);
		
		if (scrFile.exists()) { scrFile.delete(); }
		scrFile.createNewFile();
			
		StringWriter strWriter = new StringWriter();
		XmlWriter xmlWriter = new XmlWriter(strWriter);
		
		xmlWriter.element("screen");
		
		xmlWriter.element("name",     scrData.getName());
		xmlWriter.element("width",    scrData.getWidth());
		xmlWriter.element("height",   scrData.getHeight());
		xmlWriter.element("xmlPath",  scrData.getXmlPath());
		xmlWriter.element("jsonPath", scrData.getJsonPath());
		
		if (scrData.getBgImage() != null) {
			xmlWriter.element("bg")
					 	.element("name",        scrData.getBgImage().name)
					 	.element("type",        (getWidgetType(scrData.getBgImage()) != null) ? getWidgetType(scrData.getBgImage()).ordinal() : -1)
					 	.element("texturePath", ((GdxImage)scrData.getBgImage()).getTexPath())
					 	.element("scaleX",      scrData.getBgImage().scaleX)
					 	.element("scaleY",      scrData.getBgImage().scaleY)
					 	.element("x",           scrData.getBgImage().x)
					 	.element("y",           scrData.getBgImage().y)
					 	.element("angle",       scrData.getBgImage().rotation)
					 .pop();
		}
		
		if ((scrData.getPath() != null) && (scrData.getPath().getPath() != null)) {
			xmlWriter.element("path")
			 	.element("xmlPath",  scrData.getPath().getXmlPath())
			 	.element("jsonPath", scrData.getPath().getJsonPath())
			 .pop();
		}
		
		xmlWriter.pop();
		xmlWriter.close();
		
		FileWriter writer = new FileWriter(new File(path));
		writer.write(strWriter.toString());
		writer.close();
	}
	
	private static void saveScreenToJSON(ScreenData scrData, String projPath) throws Exception {
		String path = scrData.getJsonPath();
		File scrFile = new File(path);
		
		if (scrFile.exists()) { scrFile.delete(); }
		scrFile.createNewFile();
		
		StringWriter strWriter = new StringWriter();
		JsonWriter jsonWriter = new JsonWriter(strWriter);
		jsonWriter.object()
					.object("screen")
						.set("name",     scrData.getName())
						.set("width",    scrData.getWidth())
						.set("height",   scrData.getHeight())
						.set("xmlPath",  scrData.getXmlPath())
						.set("jsonPath", scrData.getJsonPath());
		
		if ((scrData.getBgImage() != null)) {
			jsonWriter.object("bg")
						.set("name",        scrData.getBgImage().name)
						.set("type",        (getWidgetType(scrData.getBgImage()) != null) ? getWidgetType(scrData.getBgImage()).ordinal() : -1)
						.set("texturePath", ((GdxImage)scrData.getBgImage()).getTexPath())
						.set("scaleX",      scrData.getBgImage().scaleX)
						.set("scaleY",      scrData.getBgImage().scaleY)
						.set("x",           scrData.getBgImage().x)
						.set("y",           scrData.getBgImage().y)
						.set("angle",       scrData.getBgImage().rotation)
					  .pop();
		}
		
		if ((scrData.getPath() != null) && (scrData.getPath().getPath() != null)) {
			jsonWriter.object("path")
			 	.set("xmlPath",  scrData.getPath().getXmlPath())
			 	.set("jsonPath", scrData.getPath().getJsonPath())
			 .pop();
		}
		
		jsonWriter.pop();
		jsonWriter.pop();
		jsonWriter.close();
		
		FileWriter writer = new FileWriter(new File(path));
		writer.write(strWriter.toString());
		writer.close();
	}
	
	private static void savePath(ScreenData scrData) throws Exception {
		if ((scrData.getPath() != null) && (scrData.getPath().getPath() != null)) {
			createPathDir(getScreenDir(scrData.getXmlPath()), "path");
			savePathToXml(scrData.getPath());
			savePathToJSON(scrData.getPath());
		}
	}
	
	private static void createPathDir(String scrPath, String pathDirName) {
		String dir = scrPath + PATH_SEPARATOR + pathDirName;
		File scrDir = new File(dir);
		if (!scrDir.exists()) {
			scrDir.mkdir();
		} else if (!scrDir.isDirectory()) {
			scrDir.delete();
			scrDir.mkdir();
		}
	}
	
	private static void savePathToXml(GdxPath gdxPath) throws Exception {
		String path = gdxPath.getXmlPath();
		File pathFile = new File(path);
		
		if (pathFile.exists()) { pathFile.delete(); }
		pathFile.createNewFile();
			
		StringWriter strWriter = new StringWriter();
		XmlWriter xmlWriter = new XmlWriter(strWriter);
		
		xmlWriter.element("path");
		
		xmlWriter.element("name",         gdxPath.getName());
		xmlWriter.element("pointsCnt",    gdxPath.getPointsCnt());
		xmlWriter.element("controlColor", gdxPath.getControlColor());
		xmlWriter.element("segmentColor", gdxPath.getSegmentColor());
		xmlWriter.element("selectColor",  gdxPath.getSelectColor());
		
		xmlWriter.element("xmlPath",  gdxPath.getXmlPath());
		xmlWriter.element("jsonPath", gdxPath.getJsonPath());
		
		if ((gdxPath.getPath() != null) && (gdxPath.getPath().getPathVerticesCount() > 0)) {
			xmlWriter.element("controlVertices");
			Vector3 controlVertex;
			for (int i=0; i<gdxPath.getControlPath().size(); i++) {
				controlVertex = gdxPath.getControlPath().get(i);
				xmlWriter.element("controlVertex")
			 			 	.element("id", i)
			 			 	.element("x",  controlVertex.x)	
			 			 	.element("y",  controlVertex.y)
			 			 .pop();	
			}
			xmlWriter.pop();
			
			xmlWriter.element("vertices");
			PathVertex vertex;
			for (int i=0; i<gdxPath.getPath().getPathVerticesCount(); i++) {
				vertex = gdxPath.getPath().getPathVertexByIndex(i);
				xmlWriter.element("vertex")
			 			 	.element("id",    i)
			 			 	.element("x",     vertex.getPosition().x)	
			 			 	.element("y",     vertex.getPosition().y)
			 			 	.element("tanX",  vertex.getTangentNornal().x)	
			 			 	.element("tanY",  vertex.getTangentNornal().y)
			 			 	.element("angle", vertex.getAngle())
			 			 .pop();	
			}
			xmlWriter.pop();
		}
		
		xmlWriter.pop();
		xmlWriter.close();
		
		FileWriter writer = new FileWriter(new File(path));
		writer.write(strWriter.toString());
		writer.close();
	}
	
	private static void savePathToJSON(GdxPath gdxPath) throws Exception {
		String path = gdxPath.getJsonPath();
		File pathFile = new File(path);
		
		if (pathFile.exists()) { pathFile.delete(); }
		pathFile.createNewFile();
		
		StringWriter strWriter = new StringWriter();
		JsonWriter jsonWriter = new JsonWriter(strWriter);
		jsonWriter.object()
					.object("path")
						.set("name",     	 gdxPath.getName())
						.set("pointsCnt",    gdxPath.getPointsCnt())
						.set("controlColor", gdxPath.getControlColor())
						.set("segmentColor", gdxPath.getSegmentColor())
						.set("selectColor",  gdxPath.getSelectColor())
						.set("xmlPath", 	 gdxPath.getXmlPath())
						.set("jsonPath", 	 gdxPath.getJsonPath());
		
		if ((gdxPath.getPath() != null) && (gdxPath.getPath().getPathVerticesCount() > 0)) {
			jsonWriter.array("controlVertices");
			Vector3 controlVertex;
			for (int i=0; i<gdxPath.getControlPath().size(); i++) {
				controlVertex = gdxPath.getControlPath().get(i);
				jsonWriter.object()
							.set("id", i)
							.set("x",  controlVertex.x)
							.set("y",  controlVertex.y)
						  .pop();
			}
			jsonWriter.pop();

			jsonWriter.array("vertices");
			PathVertex vertex;
			for (int i=0; i<gdxPath.getPath().getPathVerticesCount(); i++) {
				vertex = gdxPath.getPath().getPathVertexByIndex(i);
				jsonWriter.object()
							.set("id",    i)
							.set("x", 	  vertex.getPosition().x)
							.set("y", 	  vertex.getPosition().y)
							.set("tanX",  vertex.getTangentNornal().x)
							.set("tanY",  vertex.getTangentNornal().y)
							.set("angle", vertex.getAngle())
						  .pop();
			}
			jsonWriter.pop();
		}
		
		jsonWriter.pop();
		jsonWriter.pop();
		jsonWriter.close();
		
		FileWriter writer = new FileWriter(new File(path));
		writer.write(strWriter.toString());
		writer.close();
	}
	
	// ==============================================================
	// create project from its description file
	// ==============================================================
	
	public static ProjectData openProject(String path) throws Exception {
		File projectFile = new File(path);
		if (!projectFile.exists()) { throw new Exception(); }
		
		File xmlFile = new File(path);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFile), "utf8"));
		
		XmlReader xmlReader = new XmlReader();
		Element xmlRoot = xmlReader.parse(br);
		if (xmlRoot == null) { throw new Exception(); }
		
		String projName = xmlRoot.get("name", "");
		String projPath = xmlRoot.get("path", "");
		if ((projName.length() <= 0) || (projPath.length() <= 0)) { throw new Exception(); }
		
		ProjectData projData = new ProjectData();
		projData.setName(projName);
		projData.setPath(projPath);
		
		Array<Element> screensRoot = xmlRoot.getChildrenByName("screen");
		if ((screensRoot == null) || (screensRoot.size <= 0)) { return projData; }
		
		String xmlPath;
		String jsonPath;
		for (int i=0; i<screensRoot.size; i++) {
			xmlPath  = screensRoot.get(i).get("xml",  "");
			jsonPath = screensRoot.get(i).get("json", "");
			if ((xmlPath.length() <= 0) || (jsonPath.length() <= 0)) { throw new Exception(); }
			ScreenData scrData = getScreenFromJSON(jsonPath);
			//ScreenData scrData = getScreenFromXML(xmlPath);
			if (scrData == null) { throw new Exception(); }
			projData.getScreens().add(scrData);
		}
		
		return projData;
	}
	
	private static ScreenData getScreenFromXML(String path) throws Exception {
		File scrFile = new File(path);
		if (!scrFile.exists()) { throw new Exception(); }
		
		File xmlFile = new File(path);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFile), "utf8"));
		
		XmlReader xmlReader = new XmlReader();
		Element xmlRoot = xmlReader.parse(br);
		
		String name = xmlRoot.get("name", "");
		int w 		= xmlRoot.getInt("width",   -1);
		int h 		= xmlRoot.getInt("height",  -1);
		String xml  = xmlRoot.get("xmlPath",  "");
		String json = xmlRoot.get("jsonPath", "");
		if ((name.length() <= 0) || (xml.length() <= 0) || (json.length() <= 0) ||
			(w <= 0) || (h <= 0)) { throw new Exception(); }
		
		ScreenData scrData = new ScreenData();
		scrData.setName(name);
		scrData.setWidth(w);
		scrData.setHeight(h);
		scrData.setXmlPath(xml);
		scrData.setJsonPath(json);
		
		Element bgRoot = xmlRoot.getChildByName("bg");
		if (bgRoot != null) {
			String bgName    = bgRoot.get("name", "");
			String bgTexPath = bgRoot.get("texturePath", "");
			float bgScaleX 	 = bgRoot.getFloat("scaleX", -1f);
			float bgScaleY 	 = bgRoot.getFloat("scaleY", -1f);
			float bgX 	     = bgRoot.getFloat("x", -1f);
			float bgY 	     = bgRoot.getFloat("y", -1f);
			float bgAngle 	 = bgRoot.getFloat("angle", -1f);
			
			scrData.setBgImage(WidgetManager.createBGImage(bgName, bgTexPath, bgScaleX, bgScaleY, bgX, bgY, bgAngle));
		}
		
		Element pathRoot = xmlRoot.getChildByName("path");
		if (pathRoot != null) {
			xml = pathRoot.get("xmlPath", "");
			if (xml.length() > 0) {
				scrData.setPath(getPathFromXML(xml));
			}
		}
		
		return scrData;
	}
	
	@SuppressWarnings("unchecked")
	private static ScreenData getScreenFromJSON(String path) throws Exception {
		File scrFile = new File(path);
		if (!scrFile.exists()) { throw new Exception(); }
		
		File xmlFile = new File(path);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFile), "utf8"));
		
		JsonReader jsonReader = new JsonReader();
		OrderedMap<String, Object> jsonData = (OrderedMap<String, Object>) jsonReader.parse(br);
		
		if (jsonData.get("screen") == null) { throw new Exception(); }
		OrderedMap<String, Object> screenData = (OrderedMap<String, Object>) jsonData.get("screen");
		
		String name = (String) screenData.get("name");
		float w 	= (Float)  screenData.get("width");
		float h 	= (Float)  screenData.get("height");
		String xml  = (String) screenData.get("xmlPath");
		String json = (String) screenData.get("jsonPath");
		if ((name.length() <= 0) || (xml.length() <= 0) || (json.length() <= 0) ||
			(w <= 0) || (h <= 0)) { throw new Exception(); }
		
		ScreenData scrData = new ScreenData();
		scrData.setName(name);
		scrData.setWidth((int)w);
		scrData.setHeight((int)h);
		scrData.setXmlPath(xml);
		scrData.setJsonPath(json);
		
		if (screenData.get("bg") != null) {
			OrderedMap<String, Object> bgData = (OrderedMap<String, Object>) screenData.get("bg");
			String bgName    = (String) bgData.get("name");
			String bgTexPath = (String) bgData.get("texturePath");
			float bgScaleX 	 = (Float)  bgData.get("scaleX");
			float bgScaleY 	 = (Float)  bgData.get("scaleY");
			float bgX 	     = (Float)  bgData.get("x");
			float bgY 	     = (Float)  bgData.get("y");
			float bgAngle 	 = (Float)  bgData.get("angle");
			
			scrData.setBgImage(WidgetManager.createBGImage(bgName, bgTexPath, bgScaleX, bgScaleY, bgX, bgY, bgAngle));
		}
		
		if (screenData.get("path") != null) {
			OrderedMap<String, Object> pathData = (OrderedMap<String, Object>) screenData.get("path");
			json = (String) pathData.get("jsonPath");
			if (xml.length() > 0) {
				scrData.setPath(getPathFromJSON(json));
			}
		}
		
		return scrData;
	}
	
	private static GdxPath getPathFromXML(String path) throws Exception {
		File scrFile = new File(path);
		if (!scrFile.exists()) { throw new Exception(); }
		
		File xmlFile = new File(path);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFile), "utf8"));
		
		XmlReader xmlReader = new XmlReader();
		Element xmlRoot = xmlReader.parse(br);
		
		String name		= xmlRoot.get("name", "");
		int pointsCnt 	= xmlRoot.getInt("pointsCnt", -1);
		String controlColor = xmlRoot.get("controlColor", "");
		String segmentColor = xmlRoot.get("segmentColor", "");
		String selectColor  = xmlRoot.get("selectColor", "");
		String xml  = xmlRoot.get("xmlPath",  "");
		String json = xmlRoot.get("jsonPath", "");
		if ((name.length() <= 0) || (pointsCnt < 0) ||
			(controlColor.length() <= 0) || (segmentColor.length() <= 0) || (selectColor.length() <= 0) ||
			(xml.length() <= 0) || (json.length() <= 0)) { throw new Exception(); }
		
		GdxPath gdxPath = new GdxPath();
		gdxPath.setName(name);
		gdxPath.setPointsCnt(pointsCnt);
		gdxPath.setControlColor(controlColor);
		gdxPath.setSegmentColor(segmentColor);
		gdxPath.setSelectColor(selectColor);
		gdxPath.setXmlPath(xml);
		gdxPath.setJsonPath(json);
		
		Element cvRoot = xmlRoot.getChildByName("controlVertices");
		if (cvRoot == null) { throw new Exception(); }
		
		ArrayList<Vector3> controlVertices = new ArrayList<Vector3>();
		Element cvertexRoot;
		for (int i=0; i<cvRoot.getChildCount(); i++) {
			cvertexRoot = cvRoot.getChild(i);
			controlVertices.add(new Vector3(cvertexRoot.getFloat("x", 0f), cvertexRoot.getFloat("y", 0f), 0f));
		}
		gdxPath.setControlPath(controlVertices);
		
		Element vRoot = xmlRoot.getChildByName("vertices");
		if (vRoot == null) { throw new Exception(); }
		
		Path vertices = new Path();
		Element vertexRoot;
		for (int i=0; i<vRoot.getChildCount(); i++) {
			vertexRoot = vRoot.getChild(i);
			vertices.addPathVertex(vertexRoot.getFloat("x", 0f),
								   vertexRoot.getFloat("y", 0f),
								   vertexRoot.getFloat("tanX", 0f),
								   vertexRoot.getFloat("tanY", 0f));
		}
		gdxPath.setPath(vertices);
		
		return gdxPath;
	}
	
	private static GdxPath getPathFromJSON(String path) throws Exception {
		File scrFile = new File(path);
		if (!scrFile.exists()) { throw new Exception(); }
		
		File xmlFile = new File(path);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFile), "utf8"));
		
		JsonReader jsonReader = new JsonReader();
		OrderedMap<String, Object> jsonData = (OrderedMap<String, Object>) jsonReader.parse(br);
		
		if (jsonData.get("path") == null) { throw new Exception(); }
		OrderedMap<String, Object> pathData = (OrderedMap<String, Object>) jsonData.get("path");
		
		String name   = (String) pathData.get("name");
		int pointsCnt = Math.round((Float) pathData.get("pointsCnt"));
		String controlColor = (String) pathData.get("controlColor");
		String segmentColor = (String) pathData.get("segmentColor");
		String selectColor  = (String) pathData.get("selectColor");
		String xml  = (String) pathData.get("xmlPath");
		String json = (String) pathData.get("jsonPath");
		if ((name.length() <= 0) || (pointsCnt < 0) ||
			(controlColor.length() <= 0) || (segmentColor.length() <= 0) || (selectColor.length() <= 0) ||
			(xml.length() <= 0) || (json.length() <= 0)) { throw new Exception(); }
		
		GdxPath gdxPath = new GdxPath();
		gdxPath.setName(name);
		gdxPath.setPointsCnt(pointsCnt);
		gdxPath.setControlColor(controlColor);
		gdxPath.setSegmentColor(segmentColor);
		gdxPath.setSelectColor(selectColor);
		gdxPath.setXmlPath(xml);
		gdxPath.setJsonPath(json);
		
		if (pathData.get("controlVertices") != null) {
			Array<OrderedMap<String, Object>> controlVerticesData = (Array<OrderedMap<String, Object>>) pathData.get("controlVertices");
			ArrayList<Vector3> controlVertices = new ArrayList<Vector3>();
			OrderedMap<String, Object> cvertexData;
			for (int i=0; i<controlVerticesData.size; i++) {
				cvertexData = controlVerticesData.get(i);
				controlVertices.add(new Vector3((Float) cvertexData.get("x"), (Float) cvertexData.get("y"), 0f));
			}
			gdxPath.setControlPath(controlVertices);
		}
		else { throw new Exception(); }
		
		if (pathData.get("vertices") != null) {
			Array<OrderedMap<String, Object>> verticesData = (Array<OrderedMap<String, Object>>) pathData.get("vertices");
			Path vertices = new Path();
			OrderedMap<String, Object> vertexData;
			for (int i=0; i<verticesData.size; i++) {
				vertexData = verticesData.get(i);
				vertices.addPathVertex((Float) vertexData.get("x"),
									   (Float) vertexData.get("y"),
									   (Float) vertexData.get("tanX"),
									   (Float) vertexData.get("tanY"));
			}
			gdxPath.setPath(vertices);
		}
		else { throw new Exception(); }
		
		return gdxPath;
	}
	
	// ==============================================================
	// utils
	// ==============================================================
	
	private static boolean deleteDir(File path) {
		if (path.exists()) {
	        File[] files = path.listFiles();
	        for (int i=0; i<files.length; i++) {
	            if (files[i].isDirectory()) {
	            	deleteDir(files[i]);
	            }
	            else {
	            	files[i].delete();
	            }
	        }
	    }
	    return (path.delete());
	}
	
	public static String genScreenXMLPath(String projPath, String screenName) {
		return String.format("%s%s%s%s%s.xml", projPath, PATH_SEPARATOR, screenName, PATH_SEPARATOR, screenName);
	}
	
	public static String genScreenJSONPath(String projPath, String screenName) {
		return String.format("%s%s%s%s%s.json", projPath, PATH_SEPARATOR, screenName, PATH_SEPARATOR, screenName);
	}
	
	public static String genPathXMLPath(String screenPath, String pathName) {
		return String.format("%s%s%s%s%s.xml", screenPath, PATH_SEPARATOR, "path", PATH_SEPARATOR, pathName);
	}
	
	public static String genPathJSONPath(String screenPath, String pathName) {
		return String.format("%s%s%s%s%s.json", screenPath, PATH_SEPARATOR, "path", PATH_SEPARATOR, pathName);
	}
	
	public static String getScreenDir(String screenPath) {
		int sepPos = screenPath.lastIndexOf(PATH_SEPARATOR);
		return screenPath.substring(0, sepPos);
	}
	
	private static widgetType getWidgetType(Actor a) {
		if (a instanceof GdxImage) { return widgetType.IMAGE; }
		return null;
	}
}
