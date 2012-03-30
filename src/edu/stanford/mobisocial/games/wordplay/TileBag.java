package edu.stanford.mobisocial.games.wordplay;

import java.util.Arrays;
import java.util.Collections;

import org.json.JSONArray;

import android.util.Log;

public class TileBag {
	public static final String TAG = "TileBag";
	private Character bag[];
	
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
		
		shuffle();
	}

	public void shuffle() {
		Collections.shuffle(Arrays.asList(bag));
	}
	
	public char getNextTile() {
		if (bag.length == 0) {
			return '0';
		}
		char tile = bag[0].charValue();
		//nextTile++;
		
		Character[] original = bag;
		bag = new Character[original.length - 1];
		System.arraycopy(original, 1, bag, 0, bag.length);

		Log.w(TAG, Arrays.toString(original));
		Log.w(TAG, Arrays.toString(bag));
		
		return tile;
	}
	
	public Character[] swapTiles(Character[] tiles) {
		for(int i = 0; i < tiles.length; i++) {
			if (i >= bag.length) {
				break;
			}
			Character temp = bag[i];
			bag[i] = tiles[i];
			tiles[i] = temp;
		}
		this.shuffle();
		return tiles;
	}
	
	public int tilesRemaining() {
		return bag.length;
	}
	
	public JSONArray toJson() {

        JSONArray boardJson = new JSONArray();
		for(int i = 0; i < bag.length; i++) {
			boardJson.put(bag[i] + "");
		}
		return boardJson;
	}
	
	public void fromJson(JSONArray boardJson) {
		bag = new Character[boardJson.length()];
		for (int i = 0; i < boardJson.length(); i++) {
			bag[i] = new Character(boardJson.optString(i).charAt(0));
		}
	}
}
