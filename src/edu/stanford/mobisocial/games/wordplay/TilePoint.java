package edu.stanford.mobisocial.games.wordplay;

import android.graphics.Point;

public class TilePoint extends Point{
	public boolean played;
	public char letter;
	
	public TilePoint(int x, int y, char let, boolean played) {
		super(x, y);
		this.played = played;
		this.letter = let;
	}
	
	public TilePoint(Point point, char let, boolean played) {
		super(point);
		this.played = played;
		this.letter = let;
	}
}
