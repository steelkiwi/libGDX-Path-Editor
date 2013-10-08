package com.steelkiwi.patheditor.utils;

import java.awt.Color;

public class ColorUtils {
    public static Color hexToColor(String hexString) {
    	return Color.decode(hexString);
    }
    
    public static String colorToHex(Color color) { 
    	String rgb = Integer.toHexString(color.getRGB()); 
    	return "#" + rgb.substring(2, rgb.length()); 
    }
}
