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