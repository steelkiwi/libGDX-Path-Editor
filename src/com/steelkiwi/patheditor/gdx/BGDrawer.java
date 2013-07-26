package com.steelkiwi.patheditor.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;

public class BGDrawer {
	private ShapeRenderer renderer;
	private Texture overlay;
	private Sprite l, r, t, b;
	private float overlayBGAlpha = 0.7f;
	
	public BGDrawer() {
		renderer = new ShapeRenderer();
		overlay = new Texture(Gdx.files.internal("data/overlay.jpg"));
		t = new Sprite(overlay);
		b = new Sprite(overlay);
		l = new Sprite(overlay);
		r = new Sprite(overlay);
	}
	
	public void presentFakeBG(int scrW, int scrH, Matrix4 cameraCombined) {
		renderer.setProjectionMatrix(cameraCombined);
		
		renderer.begin(ShapeType.FilledRectangle);
        renderer.setColor(Color.BLACK);
        renderer.filledRect(0, 0, scrW, scrH);
        renderer.end();
        
        renderer.begin(ShapeType.Rectangle);
        renderer.setColor(Color.YELLOW);
        renderer.rect(0, 0, scrW, scrH);
        renderer.end();
	}
	
	public void presentOverlayBG(int scrW, int scrH, int camX, int camY, int camW, int camH, SpriteBatch batch) {
		batch.begin();
		
		t.setSize(camW, (int)(camY + camH/2 - scrH));
		t.setPosition((int)(camX - camW/2), scrH);
		t.draw(batch, overlayBGAlpha);
		
		b.setSize(camW, (int)(-camY + camH/2));
		b.setPosition((int)(camX - camW/2), (int)(camY - camH/2));
    	b.draw(batch, overlayBGAlpha);
    	
    	l.setSize((int)(-camX + camW/2), scrH);
		l.setPosition((int)(camX - camW/2), 0);
    	l.draw(batch, overlayBGAlpha);
    	
		r.setSize((int)(camX + camW/2 - scrW), scrH);
		r.setPosition(scrW, 0);
    	r.draw(batch, overlayBGAlpha);
    	
    	batch.end();
	}

	public void setOverlayBGAlpha(float overlayBGAlpha) {
		this.overlayBGAlpha = overlayBGAlpha;
	}
	
	public void dispose() {
		if (renderer != null) { renderer.dispose(); renderer = null; }
		if (overlay  != null) { overlay.dispose();  overlay = null;  }
		l = null;
		r = null;
		t = null;
		b = null;
	}
}
