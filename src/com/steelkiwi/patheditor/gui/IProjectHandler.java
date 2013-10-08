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

import java.util.ArrayList;

import javax.swing.tree.TreePath;

import com.badlogic.gdx.math.Vector3;
import com.steelkiwi.patheditor.path.Path;
import com.steelkiwi.patheditor.proj.ScreenData;

public interface IProjectHandler {
	public void onProjectCreated(String name, String path);
	public void onProjectOpened(String path);
	public boolean onProjectedSaved(boolean showSaveSuccessMsg);
	public void onProjectClosed();
	
	public void onScreenAdded(String name, int w, int h);
	public void onScreenSwitched(ScreenData scrData, TreePath path);
	public void onRootSwitched();
	
	public void onBGImageAdded(String name, String path, float scaleCoef);
	public void onLeafSwitched(String name, TreePath path);
	
	public void onPathCreate(String name, int pointsCnt, String controlColor, String segmentColor, String selectColor);
	public void onPathVertexAdd();
	public void onPathVertexEdit();
	public void onPathVertexInsert();
	public void onPathVertexRemove();
	public void onPathUpdated(int screenIndex, ArrayList<Vector3> controlPath, Path path);
	public void onPathClear();
}
