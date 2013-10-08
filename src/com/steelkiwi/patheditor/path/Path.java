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

package com.steelkiwi.patheditor.path;

import java.util.ArrayList;

public class Path {
	private ArrayList<PathVertex> path;
	
	public Path() {
		path = new ArrayList<PathVertex>();
	}
	
	public void addPathVertex(float pX, float pY, float tnX, float tnY) {
		path.add(new PathVertex(pX, pY, tnX, tnY));
	}
	
	public PathVertex getPathVertexByIndex(int index) {
		if (path == null) { return null; }
		if ((index < 0) || (index >= path.size())) { return null; }		
		return path.get(index);
	}
	
	public int getNearestPathVertexIndex(float x, float y) {
		if (path == null) { return -1; }
		float nearestDistance = Float.MAX_VALUE;
		float tempDistance;
		int nearestIndex = -1;
		for (int i=0; i<path.size(); i++) {
			PathVertex tempVertex = path.get(i);
			tempDistance = Math.abs(x - tempVertex.getPosition().x);
			if (tempDistance < nearestDistance) {
				nearestIndex  = i;
				nearestDistance = tempDistance; 
			}								
		}
		return nearestIndex;
	}
	
	public int getPathVerticesCount() {
		if (path == null) { return 0; }
		return path.size();
	}

	public void clear() {
		if (path != null) { path.clear(); path = null; }
	}
}