package com.steelkiwi.patheditor.path;

import java.util.ArrayList;

public class Path {
	private ArrayList<PathVertex> path;
	
	public Path() {
		path = new ArrayList<PathVertex>();  //tip: path.size()+1=path.length (������ path ��� ���.���������� ��� spline.getPath())
	}
	
	public void addPathVertex(float pX, float pY, float tnX, float tnY, boolean isCntrl) {
		path.add(new PathVertex(pX, pY, tnX, tnY, isCntrl));
	}
	
	public PathVertex getPathVertexByIndex(int index) {
		if (path == null) { return null; }
		if ((index < 0) || (index >= path.size())) { return null; }		
		return path.get(index);
	}
	
	public int getNearestPathVertexIndex(float x, float y) {
		if (path == null) { return -1; }
		float nearestDistance = Float.MAX_VALUE;
		float tempDistance;
		int nearestIndex = -1;
		for (int i=0; i<path.size(); i++) {
			PathVertex tempVertex = path.get(i);
			tempDistance = Math.abs(x - tempVertex.getPosition().x);
			if (tempDistance < nearestDistance) {
				nearestIndex  = i;
				nearestDistance = tempDistance; 
			}								
		}
		return nearestIndex;
	}
	
	public int getPathVerticesCount() {
		if (path == null) { return 0; }
		return path.size();
	}

	public void clear() {
		if (path != null) { path.clear(); path = null; }
	}
	
/*	public void printPath(Spline spline) {
		if (path == null) { return; }
		
		StringWriter strWriter = new StringWriter();
		XmlWriter xmlWriter = new XmlWriter(strWriter);
		try {
			xmlWriter.element("path");
			
			ArrayList<Vector3> controlPoints = spline.getControlPoints();
			
			for (int i=0; i<controlPoints.size(); i++) {
				xmlWriter.element("controlPoint")
						 	 .element("id",    i)
						 	 .element("x",     controlPoints.get(i).x)	
						 	 .element("y",     controlPoints.get(i).y)
					     .pop();									
			}
					
			xmlWriter.element("pointCnt", spline.getIntermediatePointCnt());
	
			for (int i=0; i<path.size(); i++) {
				xmlWriter.element("point")
			 			 	.element("id",    i)
			 			 	.element("x",     path.get(i).getPos().x)	
			 			 	.element("y",     path.get(i).getPos().y)
			 			 	.element("angle", path.get(i).getAngle())
			 			 .pop();	
			}
			
			xmlWriter.pop();
			xmlWriter.close();
		
			FileHandle stream = Gdx.files.external("data/path.xml");
			stream.writeString(strWriter.toString(), false);
			
			System.out.println("*** PATH STARTS ***");
			System.out.println(strWriter.toString());
			System.out.println("*** PATH ENDS ***");
		} catch (IOException e) {
			throw new GdxRuntimeException("Couldn't save path!", e);
		}	 
	}*/
}