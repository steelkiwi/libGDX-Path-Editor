package com.steelkiwi.patheditor.gdx;

public abstract class Screen implements IScreenStructureChangeListener {
	GdxApp gdxApp;
    
	public Screen(GdxApp gdxApp) {
		this.gdxApp = gdxApp;
    }
	
	public abstract void resize(int width, int height);
	
    public abstract void update(float deltaTime);
    
    public abstract void present(float deltaTime);
    
    public abstract void pause();
    
    public abstract void resume();
    
    public abstract void dispose();
}
