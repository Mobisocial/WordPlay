package edu.stanford.mobisocial.games.wordplay;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;

import android.graphics.Point;
import android.util.Log;
import edu.stanford.mobisocial.games.wordplay.tiles.Tile;

public class TileRack {
	Tile tiles[];
	int numTiles;
	
	public static final String TAG = "TileRack";
	WordPlayActivity context;
	
	public TileRack(WordPlayActivity context) {
		tiles = new Tile[7];
		this.context = context;
		for (int i = 0; i < 7; i++) {
			tiles[i] = null;
		}
	}
	
	private boolean hasAdjacentTile(Point coordinate) {
		if (coordinate.x > 0 && context.tileSpaces[coordinate.x-1][coordinate.y].getLetter() != '0') {
			return true;
		}
		else if (coordinate.x < 14 && context.tileSpaces[coordinate.x+1][coordinate.y].getLetter() != '0') {
			return true;
		}
		else if (coordinate.y > 0 && context.tileSpaces[coordinate.x][coordinate.y-1].getLetter() != '0') {
			return true;
		}
		else if (coordinate.y < 14 && context.tileSpaces[coordinate.x][coordinate.y+1].getLetter() != '0') {
			return true;
		}
		else {
			return false;
		}
	}
	
	public TilePoint[] placeTiles(Scene scene, boolean firstPlay) {
		boolean atleastOne = false;
		//verify that tiles are aligned
		boolean vertical = false;
		boolean horizontal = false;
		Point anchor = null;
		int letterCount = 0;
		int minPos = 100;
		int maxPos = -1;
		boolean adjacentTile = false;
		boolean hitsStartTile = false;
		for (int i = 0; i < 7; i++) {
			if (tiles[i] != null) {
				Point coordinates = tiles[i].getCoordinates();
				if (coordinates != null) {
					if (context.startCoordinate.equals(coordinates.x, coordinates.y)) {
						hitsStartTile = true;
					}
					if (hasAdjacentTile(coordinates)) {
						adjacentTile = true;
					}
					letterCount++;
					if (!atleastOne) {
						Log.w(TAG, "atleast one");
						atleastOne = true;
						anchor = coordinates;
					}
					else {
						if (vertical) {
							if (coordinates.x != anchor.x) {
								Log.w(TAG, "not vertical");
								return null;
							}
							if (coordinates.y < minPos) {
								minPos = coordinates.y;
							}
							if (coordinates.y > maxPos) {
								maxPos = coordinates.y;
							}
						}
						else if (horizontal) {
							if (coordinates.y != anchor.y) {
								Log.w(TAG, "not horizontal");
								return null;
							}
							if (coordinates.x < minPos) {
								minPos = coordinates.x;
							}
							if (coordinates.x > maxPos) {
								maxPos = coordinates.x;
							}
						}
						else {
							if (coordinates.x == anchor.x) {
								Log.w(TAG, "checking vertical");
								vertical = true;
								minPos = Math.min(coordinates.y, anchor.y);
								maxPos = Math.max(coordinates.y, anchor.y);;
							}
							else if (coordinates.y == anchor.y) {
								Log.w(TAG, "checking horizontal");
								horizontal = true;
								minPos = Math.min(coordinates.x, anchor.x);
								maxPos = Math.max(coordinates.x, anchor.x);;
							}
							else {
								Log.w(TAG, "not aligned at all");
								return null;
							}
						}
					}

					Log.w(TAG, "min: " + minPos + ", max: " + maxPos + ", count: " + letterCount);
				}
			}
		}
		if (firstPlay && !hitsStartTile) {
			Log.w(TAG, "doesn't hit start tile");
			return null;
		}
		if (!firstPlay && !adjacentTile) {
			Log.w(TAG, "no adjacent tile");
			return null;
		}
		//no tiles placed on board
		if (!atleastOne) {
			Log.w(TAG, "no tiles placed");
			return null;
		}

		

		
		//placed 1 tile
		if (!vertical && !horizontal) {
			//check if horizontal is possible
			if (context.tileSpaces[anchor.x-1][anchor.y].getLetter() != '0' || context.tileSpaces[anchor.x+1][anchor.y].getLetter() != '0') {
				horizontal = true;
				maxPos = anchor.x;
				minPos = anchor.x;
			}
			//if not, check if vertical is possible
			else if(context.tileSpaces[anchor.x][anchor.y-1].getLetter() != '0' || context.tileSpaces[anchor.x][anchor.y+1].getLetter() != '0') {
				vertical = true;
				maxPos = anchor.y;
				minPos = anchor.y;
			}
			//how did we get here? time to quit
			else {
				return null;
			}
		}
		//check for letters starting before placed tiles
		for(int i = minPos-1; i >= 0; i--) {
			if (vertical) {
				if (context.tileSpaces[anchor.x][i].getLetter() != '0') {
					Log.w(TAG, "determining earlier letter " +context.tileSpaces[anchor.x][i].getLetter());
					minPos = i;
				}
				else {
					break;
				}
			}
			else if (horizontal) {
				if (context.tileSpaces[i][anchor.y].getLetter() != '0') {
					Log.w(TAG, "determining earlier letter " +context.tileSpaces[i][anchor.y].getLetter());
					minPos = i;
				}
				else {
					break;
				}
			}
		}
		//check for letters starting after placed tiles
		for(int i = maxPos+1; i < 15; i++) {
			if (vertical) {
				if (context.tileSpaces[anchor.x][i].getLetter() != '0') {
					Log.w(TAG, "determining later letter " +context.tileSpaces[anchor.x][i].getLetter());
					maxPos = i;
				}
				else {
					break;
				}
			}
			else if (horizontal) {
				if (context.tileSpaces[i][anchor.y].getLetter() != '0') {
					Log.w(TAG, "determining later letter " +context.tileSpaces[i][anchor.y].getLetter());
					maxPos = i;
				}
				else {
					break;
				}
			}
		}
		

		//start building letter coordinate list for word from placed tiles
		TilePoint[] wordCoordinates = new TilePoint[maxPos-minPos+1];
		Log.w(TAG, "building coordinates for " + wordCoordinates.length + " letters");
		for (int i = 0; i < 7; i++) {
			if (tiles[i] != null) {
				Point coordinates = tiles[i].getCoordinates();
				if (coordinates != null) {
					if (vertical) {
						wordCoordinates[coordinates.y-minPos] = new TilePoint(coordinates, true);
					}
					else if (horizontal) {
						wordCoordinates[coordinates.x-minPos] = new TilePoint(coordinates, true);;
					}
				}
			}
		}

		//start filling in the blanks in coordinate list
		for (int i = minPos; i <= maxPos; i++) {
			if (vertical) {
				if (wordCoordinates[i-minPos] == null) {
					if (context.tileSpaces[anchor.x][i].getLetter() != '0') {
						Log.w(TAG, "determining intermediate letter " +context.tileSpaces[anchor.x][i].getLetter());
						wordCoordinates[i-minPos] = new TilePoint(anchor.x, i, false);
					}
					else {
						Log.w(TAG, "not sequential");
						return null;
					}
				}
			}
			else if (horizontal) {
				if (wordCoordinates[i-minPos] == null) {
					if (context.tileSpaces[i][anchor.y].getLetter() != '0') {
						Log.w(TAG, "determining intermediate letter " +context.tileSpaces[i][anchor.y].getLetter());
						wordCoordinates[i-minPos] = new TilePoint(i, anchor.y, false);
					}
					else {
						Log.w(TAG, "not sequential");
						return null;
					}
				}
			}
		}
		
		for (int i = 0; i < 7; i++) {
			if (tiles[i] != null) {
				Point coordinates = tiles[i].getCoordinates();
				if (coordinates != null) {
					context.tileSpaces[coordinates.x][coordinates.y].setLetter(tiles[i].getLetter());
					//tiles[i].finalizeTile();
					//tiles[i] = null;
					//numTiles--;
				}
			}
		}
		return wordCoordinates;
	}
	
	public void unsetLetters(Scene scene) {

		
		for (int i = 0; i < 7; i++) {
			if (tiles[i] != null) {
				Point coordinates = tiles[i].getCoordinates();
				if (coordinates != null) {
					context.tileSpaces[coordinates.x][coordinates.y].unsetLetter();
				}
			}
		}
	}
	
	public void finalizeTiles(Scene scene) {
		for (int i = 0; i < 7; i++) {
			if (tiles[i] != null) {
				Point coordinates = tiles[i].getCoordinates();
				if (coordinates != null) {
					context.tileSpaces[coordinates.x][coordinates.y].finalizeLetter(context, scene);
					tiles[i].finalizeTile();
					tiles[i] = null;
					numTiles--;
				}
			}
		}
	}
	
	public void clearTiles() {
		for (int i = 0; i < 7; i++) {
			if (tiles[i] != null) {
				tiles[i].returnToRack();
			}
		}
	}
	
	public void addTile(Scene scene, char letter) {
		int i = freePos();
		
		if (i >= 0 && i < 7) {
			tiles[i] = new Tile(context, scene, letter, i);
			numTiles++;
		}
	}
	
	public int numTiles() {
		return numTiles;
	}
	
	public boolean noOverlaps(int pos) {
		if(tiles[pos] == null) return true;
		Sprite srcTile = tiles[pos].getSprite();
		float srcX = srcTile.getX()+10;
		float srcY = srcTile.getY()+10;
		for(int i = 0; i < 7; i++) {
			if (i == pos || tiles[i] == null) {
				continue;
			}
			Sprite dstTile = tiles[i].getSprite();
			float dstX = dstTile.getX();
			float dstY = dstTile.getY();
			if (srcX > dstX+21) {
				continue;
			}
			else if (srcX < dstX) {
				continue;
			}
			else if (srcY > dstY+21) {
				continue;
			}
			else if(srcY < dstY) {
				continue;
			}
			return false;
		}
		return true;
	}
	
	public void insertTileAtPos(Scene scene, Tile tile, int pos) {
		//attempting to move to same spot, no need to change
		Log.w(TAG, "moving " + tile.getPos() + " to " + pos);
		if (tile.getPos() == pos) {
			tile.setPos(pos);
			return;
		}
		tiles[tile.getPos()] = null;
		if (tiles[pos] == null) {
			tile.setPos(pos);
			tiles[pos] = tile;
			return;
		}
		//attempting to move to earlier spot
		if (pos < tile.getPos()) {
			int counter = pos+1;
			Tile previous = tiles[counter-1];
			Tile current = tiles[counter];

			tile.setPos(pos);
			tiles[pos] = tile;
			while (counter < 6 && current != null) {
				Log.w(TAG, counter + "");
				previous.setPos(counter);
				tiles[counter] = previous;
				previous = current;
				counter++;
				current = tiles[counter];
			}
			
			if(previous != null) previous.setPos(counter);
			tiles[counter] = previous;
		}
		//attempting to move to later spot
		else {
			int counter = pos-1;
			Tile later = tiles[counter+1];
			Tile current = tiles[counter];
			
			tile.setPos(pos);
			tiles[pos] = tile;
			
			while(counter > 0 && current != null) {
				Log.w(TAG, counter + "");
				later.setPos(counter);
				tiles[counter] = later;
				later = current;
				counter--;
				current = tiles[counter];
			}
			if (later != null) later.setPos(counter);
			tiles[counter] = later;
		}
	}
	
	private int freePos() {
		for (int i = 0; i < 7; i++) {
			if (tiles[i] == null) {
				return i;
			}
		}
		return -1;
	}

}
