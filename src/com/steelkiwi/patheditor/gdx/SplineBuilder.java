package com.steelkiwi.patheditor.gdx;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.steelkiwi.patheditor.path.Path;
import com.steelkiwi.patheditor.path.PathSpline;

public class SplineBuilder {
	private boolean modeAddVertex    = false;
	private boolean modeEditVertex   = false;
	private boolean modeInsertVertex = false;
	private boolean modeRemoveVertex = false;
	
	private int curVertexIndex = -1;
	//for insertion
	private int leftVertexIndex  = -1;
	private int rightVertexIndex = -1;
	
	public static enum renderMode { ADD, EDIT, INSERT, REMOVE };
	private renderMode mode;
	
	private PathSpline spline;
	private SplineRenderer renderer;
	
	private Path resultPath;
	
	public SplineBuilder() {
		spline   = new PathSpline();
		renderer = new SplineRenderer(spline);
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
        
		boolean isCntrl = true;
    	int c = -1;
    	for (int i=0; i<spline.getSplineVerticesCount()-1; i++) {
    		resultPath.addPathVertex(spline.getSplineVertexByIndex(i).x,
				    				 spline.getSplineVertexByIndex(i).y,
				    				 spline.getSplineVertexTangentNormalByIndex(i).x,
				    				 spline.getSplineVertexTangentNormalByIndex(i).y,
				    				 isCntrl);
      		isCntrl = false;
      		c++;
      		if (c == 10) { //TODO it's a spline's intermediatePointsCount ???
      			isCntrl = true;
      			c = -1;
      		}
        }
    }
	
	public void clearSpline() {
		if (spline != null) { spline.clearSpline(); }
		clearResultPath();
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
}
