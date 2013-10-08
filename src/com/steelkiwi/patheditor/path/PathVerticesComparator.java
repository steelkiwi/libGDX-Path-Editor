package com.steelkiwi.patheditor.path;

import java.util.Comparator;
import com.badlogic.gdx.math.Vector3;

public class PathVerticesComparator implements Comparator<Vector3> {
	@Override
	public int compare(Vector3 arg0, Vector3 arg1) {
		if (arg0.z > arg1.z) { return 1; }
        else if (arg0.z < arg1.z) { return -1; }
        else { return 0; }   
	}
}
