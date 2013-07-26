package com.steelkiwi.patheditor.gui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.steelkiwi.patheditor.gdx.GdxApp;

public class LwjglPanel extends JPanel {
	private static final long serialVersionUID = -2348473081141831690L;
	
	private LwjglCanvas canvas;
	private GdxApp gdxApp;

	public LwjglPanel(IUIHandler handler) {
		setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
		setLayout(new GridLayout());
		
		gdxApp = new GdxApp(handler);
		canvas = new LwjglCanvas(gdxApp, true);
		add(canvas.getCanvas());
	}
	
	public int getCanvasWidth() {
		return canvas.getCanvas().getWidth();
	}
	
	public int getCanvasHeight() {
		return canvas.getCanvas().getHeight();
	}

	public GdxApp getGdxApp() {
		return gdxApp;
	}
}
