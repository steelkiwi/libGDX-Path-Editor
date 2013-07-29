package com.steelkiwi.patheditor.path;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.steelkiwi.patheditor.gdx.SplineBuilder.renderMode;

public class PathSpline {
	public static final int SPLINE_MIN_VERTICES_COUNT = 4;
	
	private CatmullRomSpline spline;            //catmullrom spline
	private ArrayList<Vector3> controlVertices; //spline's control vertices
	private Vector3[] totalVertices;            //spline's whole vertices (control points + intermediate points)
	private ArrayList<Vector3> tangentsNormal;  //vertices tangents normals
	
	private int segmentPointsCount = 1;
	
	public PathSpline() {
		spline = new CatmullRomSpline();
		controlVertices = new ArrayList<Vector3>();
	}
	
	public void addSplineControlVertex(float pX, float pY) {
		controlVertices.add(new Vector3(pX, pY, 0f));
		updateSpline();
	}
	
	public void updateSplineControlVertex(int index, float pX, float pY) {
		if (controlVertices == null) { return; }
		if ((index < 0) || (index >= getSplineControlVerticesCount())) { return; }	
		controlVertices.get(index).set(new Vector3(pX, pY, 0f));
		updateSpline();
	}
	
	public void insertSplineControlVertex(int index1, int index2, float pX, float pY) {
		if (controlVertices == null) { return; }
		if ((index1 < 0) || (index1 >= getSplineControlVerticesCount())) { return; }	
		if ((index2 < 0) || (index2 >= getSplineControlVerticesCount())) { return; }	
		
		addSplineControlVertex(controlVertices.get(getSplineControlVerticesCount()-1).x,
							   controlVertices.get(getSplineControlVerticesCount()-1).y);
		
		for (int i=getSplineControlVerticesCount()-3; i>=index2; i--) {
			updateSplineControlVertex(i+1, controlVertices.get(i).x, controlVertices.get(i).y);
		}
		
		updateSplineControlVertex(index2, pX, pY);
	}
	
	public void removeSplineControlVertex(int index) {
		if (controlVertices == null) { return; }
		if ((index < 0) || (index >= getSplineControlVerticesCount())) { return; }		
		controlVertices.remove(index);
		updateSpline();
	}
	
	private void updateSpline() {
		//clear old spline
		spline.getControlPoints().clear();
		
		//set new control vertices
		for (int i=0; i<getSplineControlVerticesCount(); i++) {
			spline.add(controlVertices.get(i));
		}
		if (getSplineControlVerticesCount() < SPLINE_MIN_VERTICES_COUNT) { return; }
		
		//generate all vertices
		//(getSplineControlVerticesCount() - 2) cause spline creation needs two extra points
		if (totalVertices != null) { totalVertices = null; }
        totalVertices = new Vector3[(((getSplineControlVerticesCount() - 2) - 1) * (segmentPointsCount + 2)) - ((getSplineControlVerticesCount() - 2) - 2)]; 
        for (int i=0; i<getSplineVerticesCount(); i++) {
            totalVertices[i] = new Vector3();
        }
        spline.getPath(totalVertices, segmentPointsCount);
        
        //get tangent normals for all vertices
        //getSplineVerticesCount() = tangentsNormal.size() + 1
        if (tangentsNormal != null) { tangentsNormal.clear(); tangentsNormal = null; } 
        tangentsNormal = (ArrayList<Vector3>) spline.getTangentNormals2D(segmentPointsCount);
	}

	public int getNearestSplineControlVertexIndex(float x, float y) {
		if (controlVertices == null) { return -1; }
		float nearestDistance = Float.MAX_VALUE;
		float tempDistance;
		int nearestIndex = -1;
		for (int i=0; i<getSplineControlVerticesCount(); i++) {
			tempDistance = Math.abs(x - controlVertices.get(i).x);
			if (tempDistance < nearestDistance) {
				nearestIndex  = i;
				nearestDistance = tempDistance; 
			}
		}
		return nearestIndex;
	}
	
	public Vector3 getSplineControlVertexByIndex(int index) {
		if (controlVertices == null) { return null; }
		if ((index < 0) || (index >= getSplineControlVerticesCount())) { return null; }		
		return controlVertices.get(index);
	}
	
    public ArrayList<Vector3> getSplineControlVertices() {
    	return controlVertices;
    }
    
	public int getSplineControlVerticesCount() {
		if (controlVertices == null) { return 0; }
		return controlVertices.size();
	}
	
	public Vector3 getSplineVertexByIndex(int index) {
		if (totalVertices == null) { return null; }
		if ((index < 0) || (index >= getSplineVerticesCount())) { return null; }		
		return totalVertices[index];
	}
	
	public int getSplineVerticesCount() {
		if (totalVertices == null) { return 0; }
		return totalVertices.length;
	}
	
	public Vector3 getSplineVertexTangentNormalByIndex(int index) {
		if (tangentsNormal == null) { return null; }
		if ((index < 0) || (index >= tangentsNormal.size())) { return null; }		
		return tangentsNormal.get(index);
	}
	
	public int getSplineVerticesTangentNormalsCount() {
		if (tangentsNormal == null) { return -1; }
		return tangentsNormal.size();
	}
	    
    public int getSplineSegmentVerticesCount() {
    	return segmentPointsCount;
    }
    
    public void setSplineSegmentPointsCount(int segmentPointsCount) {
		this.segmentPointsCount = segmentPointsCount;
	}
    
	public void present(ShapeRenderer renderer, int index, int leftIndex, int rightIndex, renderMode mode,
						 Color controlColor, Color segmentColor, Color selectColor) {
    	Vector3 v;
    	
    	//draw all vertices
    	if (totalVertices != null) {
	        for (int i=0; i<getSplineVerticesCount()-1; i++) {
	            v = totalVertices[i];
	            renderer.setColor(segmentColor);
	            renderer.filledCircle(v.x, v.y, 4);
	        }
        }

    	//draw control vertices
        if (controlVertices != null) {
	        for (int i=0; i<controlVertices.size(); i++) {
	            v = controlVertices.get(i);
	            renderer.setColor(controlColor);
	            renderer.filledCircle(v.x, v.y, 4);
	        }
        }
        
        //draw currently selected vertex
        if ((index != -1) || ((leftIndex != -1) || (rightIndex != -1))) {
        	if ((mode == renderMode.EDIT) || (mode == renderMode.INSERT)) {
        		if (controlVertices != null) {
        	        for (int i=0; i<controlVertices.size(); i++) {
        	        	if (mode == renderMode.EDIT) {
	        	        	if (i == index) {
	        	        		v = controlVertices.get(i);
	        	        		renderer.setColor(selectColor);
		    		            renderer.filledCircle(v.x, v.y, 8);
	        	        	}
        	        	}
        	        	if (mode == renderMode.INSERT) {
        	        		if (i == leftIndex) {
	        	        		v = controlVertices.get(i);
	        	        		renderer.setColor(selectColor);
		    		            renderer.filledCircle(v.x, v.y, 8);
	        	        	}
        	        		if (i == rightIndex) {
	        	        		v = controlVertices.get(i);
	        	        		renderer.setColor(selectColor);
		    		            renderer.filledCircle(v.x, v.y, 8);
	        	        	}
        	        	}
        	        }
                }
        	}
        }
    }
    
    public void clearSpline() {
    	if (controlVertices != null) {
    		controlVertices.clear();
    	}
    	totalVertices = null;
    	if (tangentsNormal != null) {
    		tangentsNormal.clear();
    	}
    }
}