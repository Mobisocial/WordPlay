package edu.stanford.mobisocial.games.wordplay.tiles;

import org.anddev.andengine.entity.sprite.Sprite;

import edu.stanford.mobisocial.games.wordplay.WordPlayActivity;

public class StartTileSpace extends TileSpace{
	
	public StartTileSpace(WordPlayActivity context, int x, int y, int wordModifier) {
		super(x, y);
		tile = new Sprite(x, y, context.startTileRegion);
		tile.setWidth(21);
		tile.setHeight(21);
		
		tileModifier = 1;
		this.wordModifier = wordModifier;
	}

}
