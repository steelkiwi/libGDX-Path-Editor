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
