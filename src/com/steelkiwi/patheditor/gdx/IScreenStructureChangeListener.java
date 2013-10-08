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

package com.steelkiwi.patheditor.gdx;

import com.steelkiwi.patheditor.gdx.SplineBuilder.renderMode;
import com.steelkiwi.patheditor.gui.IProjectHandler;
import com.steelkiwi.patheditor.widgets.GdxImage;
import com.steelkiwi.patheditor.widgets.GdxPath;

public interface IScreenStructureChangeListener {
	public void onAddBGTexture(String name, String path, float scaleCoef);
	public GdxImage getBGImage();
	
	public boolean isPathInit();
	public void onAddPath(String name, int pointsCnt, String controlColor, String segmentColor, String selectColor, IProjectHandler handler, int screenIndex);
	public GdxPath getPath();
	public void onClearPath();
	public renderMode getPathMode();
	public void setPathMode(renderMode mode);
}
