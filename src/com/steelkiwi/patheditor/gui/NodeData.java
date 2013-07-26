package com.steelkiwi.patheditor.gui;

import com.steelkiwi.patheditor.consts.MenuConsts.TREE_NODE_TYPE;
import com.steelkiwi.patheditor.proj.ScreenData;

public class NodeData {
	private TREE_NODE_TYPE type;
	private Object data;
	
	public TREE_NODE_TYPE getType() {
		return type;
	}
	public void setType(TREE_NODE_TYPE type) {
		this.type = type;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		if (data instanceof String)     { return (String) data; }
		if (data instanceof ScreenData) { return ((ScreenData) data).getName(); }
		return super.toString();
	}
}
