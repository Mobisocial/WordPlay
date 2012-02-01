package edu.stanford.mobisocial.games.wordplay.tiles;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.util.Log;
import edu.stanford.mobisocial.games.wordplay.WordPlayActivity;
import edu.stanford.mobisocial.games.wordplay.constants.LetterValues;

public abstract class TileSpace {
	protected int x, y;
	protected int points;
	protected int tileModifier;
	protected int wordModifier;
	protected Sprite tile, letterTile, overlay;
	protected char letter;
	
	public TileSpace(int x, int y) {
		this.x = x;
		this.y = y;
		letter = '0';
		points = 0;
		tileModifier = 1;
		wordModifier = 1;
	}
	
	public void draw(Scene scene) {
		scene.attachChild(tile);
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	public int getPoints() {
		if (Character.isUpperCase(letter)) {
			return 0;
		}
		else {
			return tileModifier*LetterValues.getLetterValue(letter);
		}
	}
	
	public int getMultiplier() {
		return wordModifier;
	}
	
	public void setLetter(char letter) {

		TileSpace.this.letter = letter;
	}
	
	public void finalizeLetter(final WordPlayActivity context, final Scene scene) {
    	context.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
        		scene.detachChild(TileSpace.this.tile);
        		//Log.w("tilespace", "finalizing: " + letter);
        		
        		TileSpace.this.letterTile = new Sprite(x, y, (TextureRegion) context.letterTileRegions.get(Character.toLowerCase(letter)));
        		TileSpace.this.letterTile.setWidth(21);
        		TileSpace.this.letterTile.setHeight(21);
        		TileSpace.this.overlay = new Sprite(x, y, context.pointTileRegions[getPoints()]);
        		TileSpace.this.overlay.setWidth(21);
        		TileSpace.this.overlay.setHeight(21);
        		scene.attachChild(TileSpace.this.letterTile);
        		scene.attachChild(TileSpace.this.overlay);
            }
    	});
	}
	
	
	public char getLetter() {
		return letter;
	}
	
	public void unsetLetter() {
		TileSpace.this.letter = '0';
	}
	
	public void setUsed() {
		tileModifier = 1;
		wordModifier = 1;
	}
}
