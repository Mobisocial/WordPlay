package edu.stanford.mobisocial.games.wordplay;

import android.graphics.Point;

public class TilePoint extends Point{
	public boolean played;
	
	public TilePoint(int x, int y, boolean played) {
		super(x, y);
		this.played = played;
	}
	
	public TilePoint(Point point, boolean played) {
		super(point);
		this.played = played;
	}
}
