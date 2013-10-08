package com.steelkiwi.patheditor.gdx;

import com.steelkiwi.patheditor.gdx.SplineBuilder.renderMode;
import com.steelkiwi.patheditor.gui.IProjectHandler;
import com.steelkiwi.patheditor.widgets.GdxImage;
import com.steelkiwi.patheditor.widgets.GdxPath;

public interface IScreenStructureChangeListener {
	public void onAddBGTexture(String name, String path, float scaleCoef);
	public GdxImage getBGImage();
	
	public boolean isPathInit();
	public void onAddPath(String name, int pointsCnt, String controlColor, String segmentColor, String selectColor, IProjectHandler handler, int screenIndex);
	public GdxPath getPath();
	public void onClearPath();
	public renderMode getPathMode();
	public void setPathMode(renderMode mode);
}
