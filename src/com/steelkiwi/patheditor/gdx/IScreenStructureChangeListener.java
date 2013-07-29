package com.steelkiwi.patheditor.gdx;

import com.steelkiwi.patheditor.widgets.GdxImage;

public interface IScreenStructureChangeListener {
	public void onAddBGTexture(String name, String path, float scaleCoef);
	public GdxImage getBGImage();
	public void onAddPath();
}
