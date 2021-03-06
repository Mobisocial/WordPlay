package edu.stanford.mobisocial.games.wordplay.tiles;

import org.anddev.andengine.entity.sprite.Sprite;

import edu.stanford.mobisocial.games.wordplay.WordPlayActivity;

public class DoubleLetterTileSpace extends TileSpace{
	
	public DoubleLetterTileSpace(WordPlayActivity context, int x, int y) {
		super(x, y);
		tile = new Sprite(x, y, context.doubleLetterTileRegion);
		tile.setWidth(21);
		tile.setHeight(21);
		
		tileModifier = 2;
		wordModifier = 1;
	}

}
