package com.steelkiwi.patheditor.gdx;

import com.steelkiwi.patheditor.gdx.SplineBuilder.renderMode;
import com.steelkiwi.patheditor.widgets.GdxImage;

public interface IScreenStructureChangeListener {
	public void onAddBGTexture(String name, String path, float scaleCoef);
	public GdxImage getBGImage();
	public boolean isPathInit();
	public void onAddPath(int pointsCnt, String controlColor, String segmentColor, String selectColor);
	public void onClearPath();
	public renderMode getPathMode();
	public void setPathMode(renderMode mode);
}
