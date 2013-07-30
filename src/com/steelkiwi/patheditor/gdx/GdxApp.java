package com.steelkiwi.patheditor.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.steelkiwi.patheditor.gdx.SplineBuilder.renderMode;
import com.steelkiwi.patheditor.gui.IProjectHandler;
import com.steelkiwi.patheditor.gui.IUIHandler;
import com.steelkiwi.patheditor.proj.ScreenData;
import com.steelkiwi.patheditor.widgets.GdxImage;
import com.steelkiwi.patheditor.widgets.GdxPath;

public class GdxApp implements ApplicationListener, IScreenStructureChangeListener {
	
	private Screen screen;
	private IUIHandler uiHandler;
	
	public GdxApp(IUIHandler uiHandler) {
		this.uiHandler = uiHandler;
	}

	@Override
	public void create() {}

	@Override
	public void render() {
		if (uiHandler != null) { uiHandler.updateMemoryInfo(getMemoryConsumption()); }
		
		if (screen != null) {
			screen.update(Gdx.graphics.getDeltaTime());
			screen.present(Gdx.graphics.getDeltaTime());
		}
		else {
			GL20 gl = Gdx.graphics.getGL20();
			gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			gl.glClearColor(0.698f, 0.698f, 0.698f, 1f);
		}
	}

	@Override
	public void resize(int arg0, int arg1) {
		if (screen != null) { screen.resize(arg0, arg1); }
	}

	@Override
	public void pause() {
		if (screen != null) { screen.pause(); }
	}
	
	@Override
	public void resume() {
		if (screen != null) { screen.resume(); }
	}

	@Override
	public void dispose() {
		if (screen != null) { screen.dispose(); }
		uiHandler = null;
	}
	
	public void setScreen(ScreenData scrData, int canvasW, int canvasH, IProjectHandler handler, int screenIndex) {
		if (this.screen != null) {
	        this.screen.pause();
	        this.screen.dispose();
		}
		
		if (scrData == null) {
			this.screen = null;
			return;
		}
		
		this.screen = new GdxScreen(this, scrData.getWidth(), scrData.getHeight(), canvasW, canvasH);
		((GdxScreen)this.screen).setBGImage(scrData.getBgImage());
		((GdxScreen)this.screen).setPath(scrData.getPath(), handler, screenIndex);
    }
	
	public Screen getCurScreen() {
		return screen;
	}

	@Override
	public void onAddBGTexture(String name, String path, float scaleCoef) {
		if (screen != null) { screen.onAddBGTexture(name, path, scaleCoef); }
	}

	@Override
	public GdxImage getBGImage() {
		if (screen != null) { return screen.getBGImage(); }
		return null;
	}

	@Override
	public boolean isPathInit() {
		if (screen != null) { return screen.isPathInit(); }
		return false;
	}

	@Override
	public void onAddPath(String name, int pointsCnt, String controlColor, String segmentColor, String selectColor, IProjectHandler handler, int screenIndex) {
		if (screen != null) { screen.onAddPath(name, pointsCnt, controlColor, segmentColor, selectColor, handler, screenIndex); }
	}

	@Override
	public GdxPath getPath() {
		if (screen != null) { return screen.getPath(); }
		return null;
	}

	@Override
	public void onClearPath() {
		if (screen != null) { screen.onClearPath(); }
	}

	@Override
	public renderMode getPathMode() {
		if (screen != null) { return screen.getPathMode(); }
		return null;
	}

	@Override
	public void setPathMode(renderMode mode) {
		if (screen != null) { screen.setPathMode(mode); }
	}

	public IUIHandler getUiHandler() {
		return uiHandler;
	}
	
	protected String getMemoryConsumption() {
		return String.format("native heap = %f, java heap = %f", Gdx.app.getNativeHeap() / (1024*1024f), Gdx.app.getJavaHeap() / (1024*1024f));
	}
}
