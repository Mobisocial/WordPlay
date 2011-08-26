package edu.stanford.mobisocial.games.wordplay;

import java.util.Arrays;
import java.util.Collections;

import android.util.Log;

public class TileBag {
	private Character bag[];
	private int nextTile;
	
	public TileBag() {
		
		bag = new Character[] {'a','a','a','a','a','a','a','a','a',
				'b','b',
				'c','c',
				'd','d','d','d',
				'e','e','e','e','e','e','e','e','e','e','e','e',
				'f','f',
				'g','g','g',
				'h','h',
				'i','i','i','i','i','i','i','i','i',
				'j',
				'k',
				'l','l','l','l',
				'm','m',
				'n','n','n','n','n','n',
				'o','o','o','o','o','o','o','o',
				'p','p',
				'q',
				'r','r','r','r','r','r',
				's','s','s','s',
				't','t','t','t','t','t',
				'u','u','u','u',
				'v','v',
				'w','w',
				'x',
				'y','y',
				'z',
				' ',' '};
		nextTile = 0;
		shuffle();
	}

	public void shuffle() {
		Collections.shuffle(Arrays.asList(bag));
	}
	
	public char getNextTile() {
		if (nextTile == bag.length) {
			return '0';
		}
		char tile = bag[nextTile].charValue();
		nextTile++;
		return tile;
	}
	
	public int tilesRemaining() {
		return bag.length-nextTile;
	}
}
