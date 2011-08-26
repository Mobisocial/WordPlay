package edu.stanford.mobisocial.games.wordplay.tiles;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;

import edu.stanford.mobisocial.games.wordplay.WordPlayActivity;

import android.content.Context;

public class BasicTileSpace extends TileSpace{
	
	public BasicTileSpace(WordPlayActivity context, int x, int y) {
		super(x, y);
		tile = new Sprite(x, y, context.baseTileRegion);
		
		tileModifier = 1;
		wordModifier = 1;
	}

}
