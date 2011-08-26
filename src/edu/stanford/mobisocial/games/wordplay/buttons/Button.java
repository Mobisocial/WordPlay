package edu.stanford.mobisocial.games.wordplay.buttons;

import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
 
public class Button extends TiledSprite {
        public Button(float pX, float pY, TiledTextureRegion pTiledTextureRegion) {
                super(pX, pY, pTiledTextureRegion);
        }
       
        public void release() {
            this.setCurrentTileIndex(0);
        }
       
        public void press() {
            this.setCurrentTileIndex(1);
        }
        
        public void disable() {
        	this.setCurrentTileIndex(2);
        }
        
        public void enable() {
        	this.setCurrentTileIndex(0);
        }
}