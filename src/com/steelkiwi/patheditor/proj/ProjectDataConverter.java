package com.steelkiwi.patheditor.proj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.XmlWriter;
import com.steelkiwi.patheditor.gui.dlg.CreateImageDialog.widgetType;
import com.steelkiwi.patheditor.widgets.GdxImage;
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
		for (int i=0; i<projData.getScreens().size(); i++) {
			createScreenDir(projData.getPath(), projData.getScreens().get(i).getName());
			saveScreenToXML(projData.getScreens().get(i), projData.getPath());
			saveScreenToJSON(projData.getScreens().get(i), projData.getPath());
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
		
		if ((scrData.getBgImage() != null)) {
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
		if (bgRoot == null) { return scrData; }
		String bgName    = bgRoot.get("name", "");
		String bgTexPath = bgRoot.get("texturePath", "");
		float bgScaleX 	 = bgRoot.getFloat("scaleX", -1f);
		float bgScaleY 	 = bgRoot.getFloat("scaleY", -1f);
		float bgX 	     = bgRoot.getFloat("x", -1f);
		float bgY 	     = bgRoot.getFloat("y", -1f);
		float bgAngle 	 = bgRoot.getFloat("angle", -1f);
		
		scrData.setBgImage(WidgetManager.createBGImage(bgName, bgTexPath, bgScaleX, bgScaleY, bgX, bgY, bgAngle));
		
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
		
		if (screenData.get("bg") == null) { return scrData; }
		OrderedMap<String, Object> bgData = (OrderedMap<String, Object>) screenData.get("bg");
		String bgName    = (String) bgData.get("name");
		String bgTexPath = (String) bgData.get("texturePath");
		float bgScaleX 	 = (Float)  bgData.get("scaleX");
		float bgScaleY 	 = (Float)  bgData.get("scaleY");
		float bgX 	     = (Float)  bgData.get("x");
		float bgY 	     = (Float)  bgData.get("y");
		float bgAngle 	 = (Float)  bgData.get("angle");
		
		scrData.setBgImage(WidgetManager.createBGImage(bgName, bgTexPath, bgScaleX, bgScaleY, bgX, bgY, bgAngle));
		
		return scrData;
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
	
	private static widgetType getWidgetType(Actor a) {
		if (a instanceof GdxImage) { return widgetType.IMAGE; }
		return null;
	}
}
