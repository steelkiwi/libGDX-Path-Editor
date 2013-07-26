package com.steelkiwi.patheditor.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class WidgetManager {
	public static GdxImage createBGImage(String bgName, String bgTexPath,
										  float bgScaleX, float bgScaleY,
										  float bgX, float bgY, float bgAngle) {
		GdxImage bgImage = new GdxImage(new Texture(Gdx.files.absolute(bgTexPath)), bgName);
		bgImage.setTexPath(bgTexPath);
		bgImage.scaleX = bgScaleX;
		bgImage.scaleY = bgScaleY;
		bgImage.x = (int)(bgX);
		bgImage.y = (int)(bgY);
		return bgImage;
	}
}
