package com.steelkiwi.patheditor.gdx;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.steelkiwi.patheditor.gdx.SplineBuilder.renderMode;
import com.steelkiwi.patheditor.path.PathSpline;

public class SplineRenderer {
	private PathSpline spline;
	private ShapeRenderer renderer;
	
	public SplineRenderer(PathSpline spline) {
		this.spline = spline;
		renderer = new ShapeRenderer();
	}
	
	public void present(Matrix4 cameraCombined, int curVertexIndex, int leftVertexIndex, int rightVertexIndex, renderMode mode) {
		renderer.setProjectionMatrix(cameraCombined);
		
        renderer.begin(ShapeType.FilledCircle);
        spline.present(renderer, curVertexIndex, leftVertexIndex, rightVertexIndex, mode);
        renderer.end();
	}
	
	public void dispose() {
		spline = null;
		if (renderer != null) { renderer.dispose(); renderer = null; }
	}
}
