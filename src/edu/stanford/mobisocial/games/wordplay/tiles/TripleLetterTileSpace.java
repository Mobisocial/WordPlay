package edu.stanford.mobisocial.games.wordplay.tiles;

import org.anddev.andengine.entity.sprite.Sprite;

import edu.stanford.mobisocial.games.wordplay.WordPlayActivity;

public class TripleLetterTileSpace extends TileSpace{
	
	public TripleLetterTileSpace(WordPlayActivity context, int x, int y) {
		super(x, y);
		tile = new Sprite(x, y, context.tripleLetterTileRegion);
		
		tileModifier = 3;
		wordModifier = 1;
	}

}
