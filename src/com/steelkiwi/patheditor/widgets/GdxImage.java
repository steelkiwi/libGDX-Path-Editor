package com.steelkiwi.patheditor.widgets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;

public class GdxImage extends Image {
	private String texPath;

	public GdxImage(Texture t, String name) {
		super(t, Scaling.none, Align.CENTER, name);
	}
	
	public String getTexPath() {
		return texPath;
	}

	public void setTexPath(String texPath) {
		this.texPath = texPath;
	}
}
