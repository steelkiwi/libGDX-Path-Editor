package com.steelkiwi.patheditor.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.steelkiwi.patheditor.gdx.SplineBuilder.renderMode;
import com.steelkiwi.patheditor.path.PathSpline;

public class SplineRenderer {
	private PathSpline spline;
	private ShapeRenderer renderer;
	
	private Color controlColor;
	private Color segmentColor;
	private Color selectColor;
	
	public SplineRenderer(PathSpline spline, String controlColorHex, String segmentColorHex, String selectColorHex) {
		this.spline = spline;
		java.awt.Color tempControlColor = java.awt.Color.decode(controlColorHex);
		this.controlColor = new Color(tempControlColor.getRed()/255f, tempControlColor.getGreen()/255f, tempControlColor.getBlue()/255f, 1f);
		java.awt.Color tempSegmentColor = java.awt.Color.decode(segmentColorHex);
		this.segmentColor = new Color(tempSegmentColor.getRed()/255f, tempSegmentColor.getGreen()/255f, tempSegmentColor.getBlue()/255f, 1f);
		java.awt.Color tempSelectColor  = java.awt.Color.decode(selectColorHex);
		this.selectColor  = new Color(tempSelectColor.getRed()/255f, tempSelectColor.getGreen()/255f, tempSelectColor.getBlue()/255f, 1f);
		renderer = new ShapeRenderer();
	}
	
	public void present(Matrix4 cameraCombined, int curVertexIndex, int leftVertexIndex, int rightVertexIndex, renderMode mode) {
		renderer.setProjectionMatrix(cameraCombined);
		
        renderer.begin(ShapeType.FilledCircle);
        spline.present(renderer, curVertexIndex, leftVertexIndex, rightVertexIndex, mode, controlColor, segmentColor, selectColor);
        renderer.end();
	}
	
	public void dispose() {
		spline = null;
		if (renderer != null) { renderer.dispose(); renderer = null; }
	}
}
