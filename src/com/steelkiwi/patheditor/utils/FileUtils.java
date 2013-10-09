package com.steelkiwi.patheditor.utils;

import java.io.File;

public class FileUtils {
	public static boolean checkDirIsEmpty(String path) {
		File file = new File(path);
		if (!file.isDirectory()) { return false; }
		if (file.list().length > 0) { return false; }
		else { return true; }
	}
	
	public static String getElementRelativePath(String projPath, String path) {
		if (!path.startsWith(projPath)) { return ""; }
		return path.substring(projPath.length());
	}
}
