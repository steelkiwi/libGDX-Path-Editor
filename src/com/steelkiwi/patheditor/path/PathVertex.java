package com.steelkiwi.patheditor.path;

import com.badlogic.gdx.math.Vector2;

public class PathVertex {
	private Vector2 position;
	private Vector2 tangentNornal;
	private float angle; //in degrees
	private boolean isControl;
	
	public PathVertex(float pX, float pY, float tnX, float tnY, boolean isCntrl) {
		position      = new Vector2(pX, pY);
		tangentNornal = new Vector2(tnX, tnY);
		angle = tangentNornal.angle();
		isControl = isCntrl;
	}
	
	public Vector2 getPosition() {
		return position;
	}

	public float getAngle() {
		return angle;
	}
	
	public boolean getIsControl() {
		return isControl;
	}
}