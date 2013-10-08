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

package com.steelkiwi.patheditor.gdx;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class Camera extends OrthographicCamera {
	private static final float MIN_ZOOM = 0.01f;
	private static final float MAX_ZOOM = 1f;
	private static final float DELTA_ZOOM = 0.01f;
	
	private int difX, difY;
	private Vector2 lastCamTouch = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
	
	public Camera(int w, int h) {
		super(w, h);
	}
	
	public void move(float x, float y) {
		difX = (int) (lastCamTouch.x - x);
		difY = (int) (y - lastCamTouch.y);
		lastCamTouch.set(x, y);
		
		this.position.x += difX;
		this.position.y += difY;
	}
	
	public void zoom(int dir) {
		if (this.zoom + DELTA_ZOOM*dir <= MIN_ZOOM) {
			this.zoom = MIN_ZOOM;
		}
		else if (this.zoom + DELTA_ZOOM*dir >= MAX_ZOOM) {
			this.zoom = MAX_ZOOM;
		}
		else { this.zoom += DELTA_ZOOM*dir; }
	}

	public void setLastCamTouch(float x, float y) {
		this.lastCamTouch.set(x, y);
	}
	
	public void resetLastCamTouch() {
		this.lastCamTouch.set(Float.MAX_VALUE, Float.MAX_VALUE);
	}
	
	public boolean isMapMoving() {
		return ((lastCamTouch.x != Float.MAX_VALUE) && (lastCamTouch.y != Float.MAX_VALUE));
	}
}
