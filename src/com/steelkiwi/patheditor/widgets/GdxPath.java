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

package com.steelkiwi.patheditor.widgets;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector3;
import com.steelkiwi.patheditor.path.Path;
import com.steelkiwi.patheditor.path.PathVertex;

public class GdxPath {
	private String name;
	private int pointsCnt;
	private String controlColor;
	private String segmentColor;
	private String selectColor;
	
	private String xmlPath;
	private String jsonPath;
	
	private ArrayList<Vector3> controlPath;
	private Path path;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPointsCnt() {
		return pointsCnt;
	}

	public void setPointsCnt(int pointsCnt) {
		this.pointsCnt = pointsCnt;
	}

	public String getControlColor() {
		return controlColor;
	}

	public void setControlColor(String controlColor) {
		this.controlColor = controlColor;
	}

	public String getSegmentColor() {
		return segmentColor;
	}

	public void setSegmentColor(String segmentColor) {
		this.segmentColor = segmentColor;
	}

	public String getSelectColor() {
		return selectColor;
	}

	public void setSelectColor(String selectColor) {
		this.selectColor = selectColor;
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

	public Path getPath() {
		return path;
	}

	public ArrayList<Vector3> getControlPath() {
		return controlPath;
	}

	public void setControlPath(ArrayList<Vector3> controlPath) {
		if (this.controlPath != null) {
			this.controlPath.clear();
			this.controlPath = null;
		}
		if (controlPath == null) { return; }
		this.controlPath = new ArrayList<Vector3>();
		Vector3 v;
		for (int i=0; i<controlPath.size(); i++) {
			v = controlPath.get(i);
			this.controlPath.add(new Vector3(v.x, v.y, 0f));
		}
	}
	
	public void setPath(Path path) {
		if (this.path != null) {
			this.path.clear();
			this.path = null;
		}
		if (path == null) { return; }
		this.path = new Path();
		PathVertex v;
		for (int i=0; i<path.getPathVerticesCount(); i++) {
			v = path.getPathVertexByIndex(i);
			this.path.addPathVertex(v.getPosition().x, v.getPosition().y, v.getTangentNornal().x, v.getTangentNornal().y);
		}
	}
}
