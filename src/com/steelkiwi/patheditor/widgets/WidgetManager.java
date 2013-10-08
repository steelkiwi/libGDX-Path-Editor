/*
 * Copyright (C) 2013 Steelkiwi Development, Julia Zudikova
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
