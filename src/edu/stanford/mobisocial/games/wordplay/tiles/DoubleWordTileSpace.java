package edu.stanford.mobisocial.games.wordplay.tiles;

import org.anddev.andengine.entity.sprite.Sprite;

import edu.stanford.mobisocial.games.wordplay.WordPlayActivity;

public class DoubleWordTileSpace extends TileSpace{
	
	public DoubleWordTileSpace(WordPlayActivity context, int x, int y) {
		super(x, y);
		tile = new Sprite(x, y, context.doubleWordTileRegion);
		tile.setWidth(21);
		tile.setHeight(21);
		
		tileModifier = 1;
		wordModifier = 2;
	}

}
