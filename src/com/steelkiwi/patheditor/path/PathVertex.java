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

package com.steelkiwi.patheditor.path;

import com.badlogic.gdx.math.Vector2;

public class PathVertex {
	private Vector2 position;
	private Vector2 tangentNornal;
	private float angle; //in degrees
	
	public PathVertex(float pX, float pY, float tnX, float tnY) {
		position      = new Vector2(pX, pY);
		tangentNornal = new Vector2(tnX, tnY);
		angle = tangentNornal.angle();
	}
	
	public Vector2 getPosition() {
		return position;
	}

	public Vector2 getTangentNornal() {
		return tangentNornal;
	}

	public float getAngle() {
		return angle;
	}
}