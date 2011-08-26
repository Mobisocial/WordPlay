package edu.stanford.mobisocial.games.wordplay;

import java.util.Calendar;
import java.util.HashMap;

import mobisocial.socialkit.Dungbeetle;
import mobisocial.socialkit.Dungbeetle.StateObserver;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.ZoomCamera;
import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.ScrollDetector;
import org.anddev.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.anddev.andengine.input.touch.detector.SurfaceScrollDetector;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;
import edu.stanford.mobisocial.games.wordplay.buttons.Button;
import edu.stanford.mobisocial.games.wordplay.constants.BoardLayout;
import edu.stanford.mobisocial.games.wordplay.tiles.BasicTileSpace;
import edu.stanford.mobisocial.games.wordplay.tiles.DoubleLetterTileSpace;
import edu.stanford.mobisocial.games.wordplay.tiles.DoubleWordTileSpace;
import edu.stanford.mobisocial.games.wordplay.tiles.StartTileSpace;
import edu.stanford.mobisocial.games.wordplay.tiles.TileSpace;
import edu.stanford.mobisocial.games.wordplay.tiles.TripleLetterTileSpace;
import edu.stanford.mobisocial.games.wordplay.tiles.TripleWordTileSpace;
import edu.stanford.mobisocial.games.wordplay.verifiers.Dictionary;
import edu.stanford.mobisocial.games.wordplay.verifiers.TWLDictionary;

public class WordPlayActivity extends BaseGameActivity  implements IScrollDetectorListener, IOnSceneTouchListener {
	
	// ===========================================================
	// Constants
	// ===========================================================
	static final int CAMERA_WIDTH = 320;
	static final int CAMERA_HEIGHT = 480;
	 
	private static final String TAG = "WordPlayActivity";
	// ===========================================================
	// Fields
	// ===========================================================
	private ZoomCamera mCamera;
	
	public HUD hud;
	
	private BitmapTextureAtlas mTexture;
	private BitmapTextureAtlas startTile, baseTile, doubleLetterTile, tripleLetterTile, doubleWordTile, tripleWordTile;
	public TextureRegion startTileRegion, baseTileRegion, doubleLetterTileRegion, tripleLetterTileRegion, doubleWordTileRegion, tripleWordTileRegion;
	
	private BitmapTextureAtlas aTile, bTile, cTile, dTile, eTile, fTile, gTile, hTile, iTile, jTile, kTile, lTile, mTile, nTile, oTile, pTile, qTile, rTile, sTile, tTile, uTile, vTile, wTile, xTile, yTile, zTile, blankTile;
	public HashMap<Character, TextureRegion> letterTileRegions;
	
	private BitmapTextureAtlas playButtonTexture, shuffleButtonTexture, clearButtonTexture, swapButtonTexture, passButtonTexture;
	private TiledTextureRegion playButtonRegion, shuffleButtonRegion, clearButtonRegion, swapButtonRegion, passButtonRegion;
	
	private TextureRegion mFaceTextureRegion;
    public TileSpace[][] tileSpaces;
    public TileRack tileRack;
	private SurfaceScrollDetector mScrollDetector;
    
	boolean firstPlay;
	public Point startCoordinate;
	
	public TileBag bag;
	
	public Dictionary dictionary;
	

    private BitmapTextureAtlas mFontTexture;
	private Font mFont;

	Scene scene;
	

	public long lastTap;
	

    Dungbeetle mDungBeetle;
	// ===========================================================
	// Constructors
	// ===========================================================
	 
	// ===========================================================
	// Getter &amp; Setter
	// ===========================================================
	 
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Engine onLoadEngine() {
		//this.mCamera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 1, 1, 5.0f);
		hud = new HUD();
		firstPlay = true;
		startCoordinate = null;

		this.mCamera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);  
		final int alturaTotal = CAMERA_HEIGHT*3;
		this.mCamera.setBounds(0, CAMERA_WIDTH, 0, CAMERA_HEIGHT);
		this.mCamera.setBoundsEnabled(true);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		return new Engine(new EngineOptions(false, ScreenOrientation.PORTRAIT, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
		this.mTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "gfx/ui_ball_1.png", 0, 0);
		
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		

		this.startTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.baseTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.doubleLetterTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.tripleLetterTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.doubleWordTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.tripleWordTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		this.aTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.bTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.cTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.dTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.eTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.fTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.gTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.hTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.iTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.jTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.kTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.lTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.nTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.oTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.pTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.qTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.rTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.sTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.tTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.uTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.vTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.wTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.xTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.yTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.zTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.blankTile = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		this.startTileRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.startTile, this, "gfx/start_tile.png", 0, 0);
		this.baseTileRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.baseTile, this, "gfx/tile_base.png", 0, 0);
		this.doubleLetterTileRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.doubleLetterTile, this, "gfx/double_letter_tile.png", 0, 0);
		this.tripleLetterTileRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.tripleLetterTile, this, "gfx/triple_letter_tile.png", 0, 0);
		this.doubleWordTileRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.doubleWordTile, this, "gfx/double_word_tile.png", 0, 0);
		this.tripleWordTileRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.tripleWordTile, this, "gfx/triple_word_tile.png", 0, 0);

		

		playButtonTexture = new BitmapTextureAtlas(256, 256, TextureOptions.DEFAULT);
		playButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playButtonTexture, this, "gfx/play_button.png", 0, 0, 3, 1);
		
		passButtonTexture = new BitmapTextureAtlas(256, 256, TextureOptions.DEFAULT);
		passButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(passButtonTexture, this, "gfx/pass_button.png", 0, 0, 3, 1);
		
		swapButtonTexture = new BitmapTextureAtlas(256, 256, TextureOptions.DEFAULT);
		swapButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(swapButtonTexture, this, "gfx/swap_button.png", 0, 0, 3, 1);
		
		shuffleButtonTexture = new BitmapTextureAtlas(256, 256, TextureOptions.DEFAULT);
		shuffleButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(shuffleButtonTexture, this, "gfx/shuffle_button.png", 0, 0, 3, 1);
		
		clearButtonTexture = new BitmapTextureAtlas(256, 256, TextureOptions.DEFAULT);
		clearButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(clearButtonTexture, this, "gfx/clear_button.png", 0, 0, 3, 1);
		
		letterTileRegions = new HashMap<Character, TextureRegion>();
		letterTileRegions.put('a', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.aTile, this, "gfx/a_tile.png", 0, 0));
		letterTileRegions.put('b', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bTile, this, "gfx/b_tile.png", 0, 0));
		letterTileRegions.put('c', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.cTile, this, "gfx/c_tile.png", 0, 0));
		letterTileRegions.put('d', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.dTile, this, "gfx/d_tile.png", 0, 0));
		letterTileRegions.put('e', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.eTile, this, "gfx/e_tile.png", 0, 0));
		letterTileRegions.put('f', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.fTile, this, "gfx/f_tile.png", 0, 0));
		letterTileRegions.put('g', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gTile, this, "gfx/g_tile.png", 0, 0));
		letterTileRegions.put('h', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.hTile, this, "gfx/h_tile.png", 0, 0));
		letterTileRegions.put('i', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.iTile, this, "gfx/i_tile.png", 0, 0));
		letterTileRegions.put('j', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.jTile, this, "gfx/j_tile.png", 0, 0));
		letterTileRegions.put('k', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.kTile, this, "gfx/k_tile.png", 0, 0));
		letterTileRegions.put('l', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.lTile, this, "gfx/l_tile.png", 0, 0));
		letterTileRegions.put('m', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTile, this, "gfx/m_tile.png", 0, 0));
		letterTileRegions.put('n', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.nTile, this, "gfx/n_tile.png", 0, 0));
		letterTileRegions.put('o', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.oTile, this, "gfx/o_tile.png", 0, 0));
		letterTileRegions.put('p', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.pTile, this, "gfx/p_tile.png", 0, 0));
		letterTileRegions.put('q', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.qTile, this, "gfx/q_tile.png", 0, 0));
		letterTileRegions.put('r', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.rTile, this, "gfx/r_tile.png", 0, 0));
		letterTileRegions.put('s', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.sTile, this, "gfx/s_tile.png", 0, 0));
		letterTileRegions.put('t', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.tTile, this, "gfx/t_tile.png", 0, 0));
		letterTileRegions.put('u', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.uTile, this, "gfx/u_tile.png", 0, 0));
		letterTileRegions.put('v', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.vTile, this, "gfx/v_tile.png", 0, 0));
		letterTileRegions.put('w', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.wTile, this, "gfx/w_tile.png", 0, 0));
		letterTileRegions.put('x', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.xTile, this, "gfx/x_tile.png", 0, 0));
		letterTileRegions.put('y', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.yTile, this, "gfx/y_tile.png", 0, 0));
		letterTileRegions.put('z', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.zTile, this, "gfx/z_tile.png", 0, 0));
		letterTileRegions.put(' ', BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.blankTile, this, "gfx/blank_tile.png", 0, 0));

		this.mEngine.getTextureManager().loadTexture(playButtonTexture);
		this.mEngine.getTextureManager().loadTexture(passButtonTexture);
		this.mEngine.getTextureManager().loadTexture(shuffleButtonTexture);
		this.mEngine.getTextureManager().loadTexture(swapButtonTexture);
		this.mEngine.getTextureManager().loadTexture(clearButtonTexture);
		
		this.mEngine.getTextureManager().loadTexture(this.startTile);
		this.mEngine.getTextureManager().loadTexture(this.baseTile);
		this.mEngine.getTextureManager().loadTexture(this.doubleLetterTile);
		this.mEngine.getTextureManager().loadTexture(this.tripleLetterTile);
		this.mEngine.getTextureManager().loadTexture(this.doubleWordTile);
		this.mEngine.getTextureManager().loadTexture(this.tripleWordTile);

		this.mEngine.getTextureManager().loadTexture(aTile);
		this.mEngine.getTextureManager().loadTexture(bTile);
		this.mEngine.getTextureManager().loadTexture(cTile);
		this.mEngine.getTextureManager().loadTexture(dTile);
		this.mEngine.getTextureManager().loadTexture(eTile);
		this.mEngine.getTextureManager().loadTexture(fTile);
		this.mEngine.getTextureManager().loadTexture(gTile);
		this.mEngine.getTextureManager().loadTexture(hTile);
		this.mEngine.getTextureManager().loadTexture(iTile);
		this.mEngine.getTextureManager().loadTexture(jTile);
		this.mEngine.getTextureManager().loadTexture(kTile);
		this.mEngine.getTextureManager().loadTexture(lTile);
		this.mEngine.getTextureManager().loadTexture(mTile);
		this.mEngine.getTextureManager().loadTexture(nTile);
		this.mEngine.getTextureManager().loadTexture(oTile);
		this.mEngine.getTextureManager().loadTexture(pTile);
		this.mEngine.getTextureManager().loadTexture(qTile);
		this.mEngine.getTextureManager().loadTexture(rTile);
		this.mEngine.getTextureManager().loadTexture(sTile);
		this.mEngine.getTextureManager().loadTexture(tTile);
		this.mEngine.getTextureManager().loadTexture(uTile);
		this.mEngine.getTextureManager().loadTexture(vTile);
		this.mEngine.getTextureManager().loadTexture(wTile);
		this.mEngine.getTextureManager().loadTexture(xTile);
		this.mEngine.getTextureManager().loadTexture(yTile);
		this.mEngine.getTextureManager().loadTexture(zTile);
		this.mEngine.getTextureManager().loadTexture(blankTile);
		
		this.mFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        this.mFont = new Font(this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 16, true, Color.WHITE);

        this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
        this.mEngine.getFontManager().loadFont(this.mFont);
        

        tileSpaces = new TileSpace[15][15];
        
        tileRack = new TileRack(this);
        
        dictionary = new TWLDictionary(this);
        

    	lastTap = 0;
    	

        if (!Dungbeetle.isDungbeetleIntent(getIntent())) {
            Toast.makeText(this, "Please launch with 2-players!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // All app code is in Board.
        mDungBeetle = Dungbeetle.getInstance(this, getIntent());
        mDungBeetle.getFeed().registerStateObserver(mStateObserver);
        //mToken = (1 == mDungBeetle.getFeed().getMemberNumber()) ? "O" : "X";
        //mBoard = new Board();
        //mBoard.render(mDungBeetle.getFeed().getLatestState());
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		 
		scene = new Scene();
		scene.setBackground(new ColorBackground(0.1686f, 0.1686f, 0.1686f));
		scene.setOnAreaTouchTraversalFrontToBack();
		 
		this.mScrollDetector = new SurfaceScrollDetector(this);
		this.mScrollDetector.setEnabled(true);

		bag = new TileBag();
		
        /* Calculate the coordinates for the face, so its centered on the camera. */
        final int centerX = (CAMERA_WIDTH - this.mFaceTextureRegion.getWidth()) / 2;
        final int centerY = (CAMERA_HEIGHT - this.mFaceTextureRegion.getHeight()) / 2;

        
		 
        for (int i = 0; i < 15; i++) {
        	for (int j = 0; j < 15; j++) {
        		if (BoardLayout.board[i][j].equals("DL")) {
        			tileSpaces[i][j] = new DoubleLetterTileSpace(this, 3+i*21, 23+j*21);
        		}
        		else if (BoardLayout.board[i][j].equals("TL")) {
        			tileSpaces[i][j] = new TripleLetterTileSpace(this, 3+i*21, 23+j*21);
        		}
        		else if (BoardLayout.board[i][j].equals("DW")) {
        			tileSpaces[i][j] = new DoubleWordTileSpace(this, 3+i*21, 23+j*21);
        		}
        		else if (BoardLayout.board[i][j].equals("TW")) {
        			tileSpaces[i][j] = new TripleWordTileSpace(this, 3+i*21, 23+j*21);
        		}
        		else if (BoardLayout.board[i][j].equals("ST")) {
        			tileSpaces[i][j] = new StartTileSpace(this, 3+i*21, 23+j*21);
        			startCoordinate = new Point(i, j);
        		}
        		else {
        			tileSpaces[i][j] = new BasicTileSpace(this, 3+i*21, 23+j*21);
        		}
        		tileSpaces[i][j].draw(scene);
        	}
        }
        /* Create the face and add it to the scene. */
        /*final Sprite face = new Sprite(centerX, centerY, this.mFaceTextureRegion){
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                    this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
                    return true;
            }
        };*/
        
        /*tileSpaces[0][0].setLetter(WordPlayActivity.this, scene, 'a');
        tileSpaces[0][1].setLetter(WordPlayActivity.this, scene, 'a');
        tileSpaces[0][2].setLetter(WordPlayActivity.this, scene, 'a');
        tileSpaces[0][3].setLetter(WordPlayActivity.this, scene, 'a');
        tileSpaces[0][4].setLetter(WordPlayActivity.this, scene, 'a');*/
        //scene.attachChild(face);
        //scene.registerTouchArea(face);
        //scene.setTouchAreaBindingEnabled(true);
        
		final Button playButton = new Button(5, 385, playButtonRegion){
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        		if (pSceneTouchEvent.isActionDown()) {
        			this.press();
        		}
        		if (pSceneTouchEvent.isActionUp()) {
        			TilePoint wordCoordinates[] = tileRack.placeTiles(scene, firstPlay);
        			if (wordCoordinates != null) {
        				Point[][] wordSet = calculateWordSet(wordCoordinates);
        				if (!verifyWordSet(wordSet)) {
        					tileRack.unsetLetters(scene);

                			this.release();
        					return true;
        				}
    					tileRack.finalizeTiles(scene);
        				
        				int totalPoints = 0;
        				for (int i = 0; i < wordSet.length; i++) {
        					if (wordSet[i] != null) {
        						totalPoints += calculatePointsForWord(wordSet[i]);
        					}
        				}
        				
        				for (int i = 0; i < wordCoordinates.length; i++) {
        					tileSpaces[wordCoordinates[i].x][wordCoordinates[i].y].setUsed();
        				}
        				firstPlay = false;
        				while(tileRack.numTiles < 7 && bag.tilesRemaining() > 0) {
        					tileRack.addTile(scene, bag.getNextTile());
        				}

                		pushUpdate();
        			}
        			this.release();
        		}
        		return true;
            }
        };
        hud.attachChild(playButton);
        hud.registerTouchArea(playButton);
        

		final Button clearButton = new Button(65, 385, clearButtonRegion){
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        		if (pSceneTouchEvent.isActionDown()) {
        			this.press();
        		}
        		if (pSceneTouchEvent.isActionUp()) {
        			this.release();
        			tileRack.clearTiles();
        		}
        		return true;
            }
        };
        hud.attachChild(clearButton);
        hud.registerTouchArea(clearButton);
        

		final Button shuffleButton = new Button(5, 430, shuffleButtonRegion){
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        		if (pSceneTouchEvent.isActionDown()) {
        			this.press();
        		}
        		if (pSceneTouchEvent.isActionUp()) {
        			this.release();
        		}
        		return true;
            }
        };
        hud.attachChild(shuffleButton);
        hud.registerTouchArea(shuffleButton);
        

		final Button swapButton = new Button(65, 430, swapButtonRegion){
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        		if (pSceneTouchEvent.isActionDown()) {
        			this.press();
        		}
        		if (pSceneTouchEvent.isActionUp()) {
        			this.release();
        		}
        		return true;
            }
        };
        hud.attachChild(swapButton);
        hud.registerTouchArea(swapButton);
        

		final Button passButton = new Button(125, 430, passButtonRegion){
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        		if(pSceneTouchEvent.isActionDown()) {
        			this.press();
        		}
        		if(pSceneTouchEvent.isActionUp()) {
        			this.release();
        		}
        		return true;
            }
        };
        hud.attachChild(passButton);
        hud.registerTouchArea(passButton);
        

		while(tileRack.numTiles < 7 && bag.tilesRemaining() > 0) {
			tileRack.addTile(scene, bag.getNextTile());
		}
        
        mCamera.setHUD(hud);
        scene.setOnSceneTouchListener(this);
        scene.setTouchAreaBindingEnabled(true);
        
        final ChangeableText fpsText = new ChangeableText(0, 3, this.mFont, "FPS:", "FPS: XXXXX".length());

        scene.attachChild(fpsText);
        
		return scene;
	}

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub
		
	}
	// ===========================================================
	// Methods
	// ===========================================================

	public Point[][] calculateWordSet(TilePoint[] wordCoordinates) {
		boolean horizontal = false;
		
		//length 1, arbitrarily pick horizontal as alignment
		if (wordCoordinates.length == 1) {
			horizontal = true;
		}
		//vertical alignment
		else if (wordCoordinates[0].x == wordCoordinates[1].x) {
			horizontal = false;
		}
		//horizontal alignment
		else {
			horizontal = true;
		}
		Point[][] wordSet = new Point[wordCoordinates.length+1][];
		wordSet[0] = wordCoordinates;
		
		for (int i = 0; i < wordCoordinates.length; i++) {
			//get orthogonally aligned word
			if(wordCoordinates[i].played) {
				Point[] altWord = getWordCoordinates(wordCoordinates[i], !horizontal);
				//check that word is atleast 2 letters long
				if (altWord.length > 1) {
					wordSet[i+1] = altWord;
				}
			}
		}
		return wordSet;
	}
	
	public Point[] getWordCoordinates(Point anchor, boolean horizontal) {
		int maxPos, minPos;
		Point[] wordCoordinates = null;
		if (horizontal) {
			minPos = anchor.x;
			maxPos = anchor.x;
			//get earliest letter coordinate
			for (int i = anchor.x; i >= 0; i--) {
				if (tileSpaces[i][anchor.y].getLetter() != '0') {
					minPos = i;
				}
				else {
					break;
				}
			}
			//get latest letter coordinate
			for (int i = anchor.x; i < 15; i++) {
				if (tileSpaces[i][anchor.y].getLetter() != '0') {
					maxPos = i;
				}
				else {
					break;
				}
			}
			//construct word coordinates
			wordCoordinates = new Point[maxPos-minPos+1];
			for (int i = minPos; i <= maxPos; i++) {
				wordCoordinates[i-minPos] = new Point(i, anchor.y);
			}
		}
		else {
			minPos = anchor.y;
			maxPos = anchor.y;
			//get earliest letter coordinate
			for (int i = anchor.y; i >= 0; i--) {
				if (tileSpaces[anchor.x][i].getLetter() != '0') {
					minPos = i;
				}
				else {
					break;
				}
			}
			//get latest letter coordinate
			for (int i = anchor.y; i < 15; i++) {
				if (tileSpaces[anchor.x][i].getLetter() != '0') {
					maxPos = i;
				}
				else {
					break;
				}
			}
			//construct word coordinates
			wordCoordinates = new Point[maxPos-minPos+1];
			for (int i = minPos; i <= maxPos; i++) {
				wordCoordinates[i-minPos] = new Point(anchor.x, i);
			}
		}
		return wordCoordinates;
	}
	
	public boolean verifyWordSet(Point[][] wordSet) {
		for (int i = 0; i < wordSet.length; i++) {
			if (wordSet[i] == null) {
				continue;
			}
			String word = "";
			for (int j = 0; j < wordSet[i].length; j++) {
				word += tileSpaces[wordSet[i][j].x][wordSet[i][j].y].getLetter();
			}
			if (!dictionary.isWord(word)) {
				return false;
			}
		}
		return true;
	}
	
	public int calculatePointsForWord(Point[] wordCoordinates) {
		int wordModifier = 1;
		int wordPoints = 0;
		String word = "";
		
		for(int i = 0; i < wordCoordinates.length; i++) {
			TileSpace tile = tileSpaces[wordCoordinates[i].x][wordCoordinates[i].y];
			wordPoints += tile.getPoints();
			wordModifier *= tile.getMultiplier();
			word += tile.getLetter();
		}
		
		
		int totalPoints = wordPoints * wordModifier;
		Log.w(TAG, word + " for " + totalPoints + " points");
		return totalPoints;
	}
	
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
		if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
	    	Calendar rightNow = Calendar.getInstance();
			long currentTap = rightNow.getTimeInMillis();
			if (currentTap - lastTap < 500) {
				lastTap = 0;
				if (mCamera.getZoomFactor() == 1) {
        			mCamera.setZoomFactor(2);
        			mCamera.setCenter(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
				}
				else {
        			mCamera.setZoomFactor(1);
				}
			}
			else {
				lastTap = currentTap;
			}
        }
		return true;
	}

	@Override
	public void onScroll(ScrollDetector pScollDetector, TouchEvent pTouchEvent,
			float pDistanceX, float pDistanceY) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Scroll {x:"+pDistanceX+", y: "+pDistanceY+"}");
		this.mCamera.offsetCenter(-pDistanceX, -pDistanceY);
	}
	 
	// ===========================================================
	// Inner and Anonymous Classes
	// =========================================================== 
	public void pushUpdate() {
        //mDungBeetle.getFeed().setApplicationState(getApplicationState(), getSnapshotText());
        mDungBeetle.getFeed().postObjectWithHtml(getApplicationState(), getSnapshotHtml());
    }
	public String getSnapshotHtml() {
        StringBuilder html = new StringBuilder("here is a board lol");
        return html.toString();
    }
	private JSONObject getApplicationState() {
        JSONObject o = new JSONObject();
        JSONArray board = new JSONArray();
        try {
        	for (int i = 0; i < 15; i++) {
        		JSONArray row = new JSONArray();
        		for (int j = 0; j < 15; j++) {
        			row.put(tileSpaces[i][j].getLetter() + "");
        		}
        		board.put(row);
        	}
            /*for (Button b : mmSquares) {
                s.put(b.getText());
            }*/
            o.put("board", board);
        } catch (JSONException e) {
            Log.wtf(TAG, "Failed to get board state", e);
        }
        return o;
    }
	private StateObserver mStateObserver = new StateObserver() {
        @Override
        public void onUpdate(JSONObject state) {
        	Log.w(TAG, state.toString());
        	Toast.makeText(WordPlayActivity.this, "Your turn!", Toast.LENGTH_SHORT).show();
            JSONArray board = state.optJSONArray("board");
            for (int i = 0; i < 15; i++) {
            	JSONArray row = board.optJSONArray(i);
            	for (int j = 0; j < 15; j++) {
            		char c = row.optString(j).charAt(0);
            		tileSpaces[i][j].setLetter(c);
            		if (c != '0') {
            			tileSpaces[i][j].setUsed();
                		tileSpaces[i][j].finalizeLetter(WordPlayActivity.this, scene);
            		}
            	}
            }
        }
    };
}