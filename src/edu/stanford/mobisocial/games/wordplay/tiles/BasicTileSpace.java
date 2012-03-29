package edu.stanford.mobisocial.games.wordplay.tiles;

import org.anddev.andengine.entity.sprite.Sprite;

import edu.stanford.mobisocial.games.wordplay.WordPlayActivity;

public class BasicTileSpace extends TileSpace{
	
	public BasicTileSpace(WordPlayActivity context, int x, int y) {
		super(x, y);
		tile = new Sprite(x, y, context.baseTileRegion);
		tile.setWidth(21);
		tile.setHeight(21);
		
		tileModifier = 1;
		wordModifier = 1;
	}

}
