package edu.stanford.mobisocial.games.wordplay.tiles;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;

import android.graphics.Point;
import android.util.Log;
import edu.stanford.mobisocial.games.wordplay.WordPlayActivity;

public class Tile {

	private char letter;
	private Sprite sprite;
	private int pos;
	private float lastX, lastY;
	private int lastSize;
	WordPlayActivity context;
	Scene scene;
	boolean inHud;
	
	public Tile(final WordPlayActivity context, final Scene scene, char letter, int i) {
		this.letter = letter;
		this.context = context;
		this.scene = scene;
		pos = i;
		lastX = 3+45*pos;
		lastY = 338;
		lastSize = 45;
		inHud = true;
		
		sprite = new Sprite(lastX, lastY, context.letterTileRegions.get(Character.valueOf(letter))){
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                //3+i*21, 20+j*21
            	float x = pSceneTouchEvent.getX();
            	float y = pSceneTouchEvent.getY();
            	//Log.w("tile", "ontouch");
    			moveToBoard();
            	
            	if(pSceneTouchEvent.isActionUp()){
            		if (x >= 3 && x <= 339 && y >= 23 && y <= 338) {
            			if(context.tileSpaces[(int)x/21][(int)y/21-1].letter == '0' && context.tileRack.noOverlaps(pos)) {
	                        sprite.setWidth(21);
	                        sprite.setHeight(21);
	                        
	            			int tempX = ((int) x / 21);
	            			int tempY = ((int) y / 21) - 1;
	            			x = tempX * 21 + 3;
	            			y = tempY * 21 + 23;
	            			this.setPosition(x, y);
	            			lastX = x;
	            			lastY = y;
	            			lastSize = 21;
            			}
            			else {
            				this.setWidth(lastSize);
            				this.setHeight(lastSize);
            				if(lastSize == 45) {
            					returnToRack();
            				}
            				else {
                				this.setPosition(lastX, lastY);	
            				}
            			}
            		}
            		else if (y >= 338 && y <= 383) {
            			moveToHud();
            			returnToRack();
            			context.tileRack.insertTileAtPos(scene, Tile.this, ((int)x-3) / 45);
            		}
            		else {
            			returnToRack();
            		}
            	}
            	else {
        	        sprite.setWidth(45);
        	        sprite.setHeight(45);
            		this.setPosition(x-10, y-10);
            	}
            	return true;
            }
        };
        
        sprite.setWidth(45);
        sprite.setHeight(45);
        context.hud.attachChild(sprite);
        context.hud.registerTouchArea(sprite);
        context.hud.setTouchAreaBindingEnabled(true);
        scene.setTouchAreaBindingEnabled(true);
	}
	
	public char getLetter() {
		return letter;
	}
	
	public void setPos(int i) {
		pos = i;
		if (sprite.getY() >= 338 && sprite.getY() <= 383) {
			lastX = 4+45*pos;
			lastY = 338;
			lastSize = 45;
			sprite.setPosition(lastX, lastY);
		}
		if (inHud){
			Log.w("tile", "returning " + letter + " to rack");
			returnToRack();
		}
	}
	
	public int getPos() {
		return pos;
	}
	
	public void returnToRack() {
    	context.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                if (scene.getChildIndex(sprite) >= 0) {
                	scene.detachChild(sprite);
                	scene.unregisterTouchArea(sprite);
                }
		        if (context.hud.getChildIndex(sprite) < 0) {
		        	Log.w("Tile", "attaching " + letter + " to hud");
		        	context.hud.attachChild(sprite);
		        	context.hud.registerTouchArea(sprite);
		        }
		        
		        sprite.setWidth(45);
		        sprite.setHeight(45);
		        Log.w("Tile", scene.getChildIndex(sprite) + "");
				sprite.setPosition(3+45*pos, 338);
            }
    	});
	}
	
	private void moveToHud() {
		inHud = true;
    	context.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                if (scene.getChildIndex(sprite) >= 0) {
                	scene.detachChild(sprite);
                	scene.unregisterTouchArea(sprite);
                }
		        if (context.hud.getChildIndex(sprite) < 0) {
		        	context.hud.attachChild(sprite);
		        	context.hud.registerTouchArea(sprite);
		        }
            }
    	});
	}
	
	private void moveToBoard() {
		inHud = false;
    	context.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
            	if(inHud) return;
		        if (context.hud.getChildIndex(sprite) >= 0) {
		        	context.hud.detachChild(sprite);
		        	context.hud.unregisterTouchArea(sprite);
		        }
		        if (scene.getChildIndex(sprite) < 0) {
		        	Log.w("tile", "attaching " + letter + " to board");
		        	scene.attachChild(sprite);
		        	scene.registerTouchArea(sprite);
		        }
            }
    	});
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public void finalizeTile() {
    	context.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
				scene.unregisterTouchArea(sprite);
				scene.detachChild(sprite);
            }
    	});
	}
	
	public Point getCoordinates() {
		if(sprite.getY() == 338) {
			return null;
		}
		return new Point(((int) sprite.getX() / 21), ((int) sprite.getY() / 21) - 1);
	}
	
}
