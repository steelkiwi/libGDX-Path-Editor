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

import java.util.ArrayList;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.steelkiwi.patheditor.gui.IProjectHandler;
import com.steelkiwi.patheditor.path.Path;
import com.steelkiwi.patheditor.path.PathSpline;
import com.steelkiwi.patheditor.widgets.GdxPath;

public class SplineBuilder {
	private int screenIndex;
	private String pathName;
	
	private int curVertexIndex = -1;
	//for insertion
	private int leftVertexIndex  = -1;
	private int rightVertexIndex = -1;
	
	private boolean modeAddVertex    = false;
	private boolean modeEditVertex   = false;
	private boolean modeInsertVertex = false;
	private boolean modeRemoveVertex = false;
	
	public static enum renderMode { ADD, EDIT, INSERT, REMOVE };
	private renderMode mode;
	
	private PathSpline spline;
	private SplineRenderer renderer;
	
	private Path resultPath;
	
	private IProjectHandler pathHandler;
	
	public SplineBuilder(String name, int pointsCnt, String controlColor, String segmentColor, String selectColor, IProjectHandler handler, int screenIndex) {
		this.screenIndex = screenIndex;
		pathName = name;
		spline = new PathSpline();
		spline.setSplineSegmentPointsCount(pointsCnt);
		renderer = new SplineRenderer(spline, controlColor, segmentColor, selectColor);
		pathHandler = handler;
	}
	
	private void addSplineVertex(float x, float y) {
		spline.addSplineControlVertex(x, y);
		createResultPath();
	}
	
	private void editSplineVertex(float x, float y) {
		curVertexIndex = spline.getNearestSplineControlVertexIndex(x, y);
		if (curVertexIndex == -1) { return; }
		mode = renderMode.EDIT;
	}
	
	private void updateSplineVertex(float x, float y) {
		if (curVertexIndex == -1) { return; }
		spline.updateSplineControlVertex(curVertexIndex, x, y);
		createResultPath();
	}
	
	private void insertSplineVertex(float x, float y) {
		if (leftVertexIndex == -1) {
			leftVertexIndex = spline.getNearestSplineControlVertexIndex(x, y);
			mode = renderMode.INSERT;
			return;
		}
		if (rightVertexIndex == -1) {
			rightVertexIndex = spline.getNearestSplineControlVertexIndex(x, y);
			mode = renderMode.INSERT;
			return;
		}
		if ((leftVertexIndex != -1) && (rightVertexIndex != -1)) {
			Vector3 p1 = spline.getSplineControlVertexByIndex(leftVertexIndex);
			Vector3 p2 = spline.getSplineControlVertexByIndex(rightVertexIndex);
			
			spline.insertSplineControlVertex(leftVertexIndex, rightVertexIndex, (p1.x + p2.x) / 2f, (p1.y + p2.y) / 2f);
			createResultPath();
			
			leftVertexIndex = -1;
			rightVertexIndex = -1;
		}
	}
	
	private void removeSplineVertex(float x, float y) {
		int num = spline.getNearestSplineControlVertexIndex(x, y);
		spline.removeSplineControlVertex(num);
		createResultPath();
	}
	
	private void createResultPath() {
        if (spline == null) { return; }
        if (spline.getSplineControlVerticesCount() < PathSpline.SPLINE_MIN_VERTICES_COUNT) { return; }
        
        clearResultPath();
        resultPath = new Path();
        
    	for (int i=0; i<spline.getSplineVerticesCount(); i++) {
    		resultPath.addPathVertex(spline.getSplineVertexByIndex(i).x,
				    				 spline.getSplineVertexByIndex(i).y,
				    				 spline.getSplineVertexTangentNormalByIndex(i).x,
				    				 spline.getSplineVertexTangentNormalByIndex(i).y);
        }
    	
    	if (pathHandler != null) { pathHandler.onPathUpdated(screenIndex, spline.getSplineControlVertices(), resultPath); }
    }
	
	public void clearSpline() {
		if (spline != null) { spline.clearSpline(); }
		clearResultPath();
		
		if (pathHandler != null) { pathHandler.onPathUpdated(screenIndex, spline.getSplineControlVertices(), resultPath); }
	}
	
	private void clearResultPath() {
		if (resultPath != null) { resultPath.clear(); resultPath = null; }
	}
	
	public void present(Matrix4 cameraCombined) {
		renderer.present(cameraCombined, curVertexIndex, leftVertexIndex, rightVertexIndex, mode);
	}
	
	public void dispose() {
		if (renderer != null) { renderer.dispose();   renderer = null; }
		if (spline   != null) { spline.clearSpline(); spline = null;   }
		pathHandler = null;
	}

	public boolean touchDown(float x, float y) {
		if (modeAddVertex) {
			addSplineVertex(x, y);
			return true;
		}
		
		if (modeEditVertex) {
			editSplineVertex(x, y);
			return true;
		}
		
		if (modeInsertVertex) {
			insertSplineVertex(x, y);
			return true;
		}
		
		if (modeRemoveVertex) {
			removeSplineVertex(x, y);
			return true;
		}
		
		return false;
	}
	
	public boolean touchDragged(float x, float y) {
		if (modeEditVertex && (curVertexIndex != -1)) {
			updateSplineVertex(x, y);
			return true;
		}
		return false;
	}
	
	public renderMode getPathMode() {
		if (modeAddVertex)    { return renderMode.ADD;    }
		if (modeEditVertex)   { return renderMode.EDIT;   }
		if (modeInsertVertex) { return renderMode.INSERT; }
		if (modeRemoveVertex) { return renderMode.REMOVE; }
		return null;
	}
	
	public void setPathMode(renderMode mode) {
		if (mode == null) {
			modeAddVertex    = false;
			modeEditVertex   = false;
			modeInsertVertex = false;
			modeRemoveVertex = false;
			curVertexIndex   = -1;
			leftVertexIndex  = -1;
			rightVertexIndex = -1;
			return;
		}
		switch (mode) {
			case ADD: {
				modeAddVertex = !modeAddVertex;
				modeEditVertex   = false;
				modeInsertVertex = false;
				modeRemoveVertex = false;
				curVertexIndex   = -1;
				leftVertexIndex  = -1;
				rightVertexIndex = -1;
				break;
			}
			case EDIT: {
				modeEditVertex = !modeEditVertex;
				if (!modeEditVertex) {
					curVertexIndex = -1;
				}
				modeAddVertex    = false;
				modeInsertVertex = false;
				modeRemoveVertex = false;
				leftVertexIndex  = -1;
				rightVertexIndex = -1;
				break;
			}
			case INSERT: {
				modeInsertVertex = !modeInsertVertex;
				modeAddVertex    = false;
				modeEditVertex   = false;
				modeRemoveVertex = false;
				curVertexIndex   = -1;
				leftVertexIndex  = -1;
				rightVertexIndex = -1;
				break;
			}
			case REMOVE: {
				modeRemoveVertex = !modeRemoveVertex;
				modeAddVertex    = false;
				modeEditVertex   = false;
				modeInsertVertex = false;
				curVertexIndex   = -1;
				leftVertexIndex  = -1;
				rightVertexIndex = -1;
				break;
			}
		}
	}
	
	public GdxPath getPath() {
		GdxPath path = new GdxPath();
		path.setName((pathName != null && pathName.length() > 0) ? pathName : "path" + System.currentTimeMillis());
		path.setPointsCnt((spline != null) ? spline.getSplineSegmentVerticesCount() : 0);
		path.setControlColor((renderer != null) ? renderer.getControlColor() : "");
		path.setSegmentColor((renderer != null) ? renderer.getSegmentColor() : "");
		path.setSelectColor((renderer  != null) ? renderer.getSelectColor()  : "");
		path.setControlPath((spline != null) ? spline.getSplineControlVertices() : null);
		path.setPath(resultPath);
		return path;
	}
	
	public void restoreSpline(ArrayList<Vector3> controlVertices) {
		spline.setSplineControlVertices(controlVertices);
		createResultPath();
	}
}
