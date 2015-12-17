package com.ericandshawn.dungeonfall;

import android.graphics.Typeface;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import java.io.IOException;

/**
 * Created by Shawn on 11/17/2015.
 */
public class ResourceManager {
    private static final ResourceManager INSTANCE =  new ResourceManager();
    public Engine engine;
    public MainActivity activity;
    public Camera camera;
    public VertexBufferObjectManager vbom;

    //Get player selected
    public int playerChosen;


    //Bitmap Texture Atlas
    private BuildableBitmapTextureAtlas menuTextureAtlas;
    private BuildableBitmapTextureAtlas gameTextureAtlas;
    private BuildableBitmapTextureAtlas hudTextureAtlas;
    private BuildableBitmapTextureAtlas storeTextureAtlas;
    public BitmapTextureAtlas mBackgroundBitmapTextureAtlas;

    //Main Menu Textures
    public ITextureRegion menu_background_region;
    protected ITextureRegion about_background_region;
    protected ITextureRegion game_background_region;
    public ITextureRegion play_button_region;
    public ITextureRegion about_button_region;
    protected ITextureRegion closeAbout_region;

    //Game Textures
    protected ITiledTextureRegion player_region;
    protected ITiledTextureRegion playerSelection_region;
    protected ITiledTextureRegion bat_region;
    protected ITiledTextureRegion gold_region;
    protected ITextureRegion platform_region;
    protected ITextureRegion spikedPlatform_region;
    protected ITiledTextureRegion door_region;
    protected ITiledTextureRegion lives_region;
    protected ITiledTextureRegion mp_region;
    protected ITiledTextureRegion charge_region;
    protected ITiledTextureRegion recharge_region;
    protected ITiledTextureRegion blood_region;
    protected ITextureRegion goldHud_region;
    protected ITextureRegion nextFloor_region;

    //Game Over Textures
    protected ITextureRegion gameOver_region;
    protected ITextureRegion replay_region;
    protected ITextureRegion home_region;

    //Store Textures
    protected ITiledTextureRegion attackIncrease_region;
    protected ITiledTextureRegion addLife_region;
    protected ITiledTextureRegion defenseIncrease_region;
    protected ITiledTextureRegion addMp_region;
    protected ITiledTextureRegion store_region;
    protected ITextureRegion closeStore_region;


    //FONTS
    protected ITexture mainFontTexture;
    protected Font menuNameFont;
    protected ITexture hudFontTexture;
    protected Font hudNameFont;


    //SOUNDS
    protected Sound fireSound;
    protected Sound wHitSound;
    protected Sound mHitSound;
    protected Sound aHitSound;
    protected Sound attackSound;
    protected Sound lightningSound;
    protected Sound wDieSound;
    protected Sound mDieSound;
    protected Sound aDieSound;
    protected Sound coinSound;
    public Music bgMusic;




    public void loadMenuResources()
    {
        loadMenuGraphics();
        loadGameFonts();
    }
    public void loadGameResources()
    {
        loadGameGraphics();
        loadHudGraphics();
        loadGameFonts();
        loadGameAudio();
        loadStoreGraphics();
    }
    private void loadMenuGraphics()
    {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),1024, 1024, TextureOptions.BILINEAR);
        menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "1.png");
        about_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "8.png");
        play_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play.png");
        about_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "about.png");
        playerSelection_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(menuTextureAtlas, activity, "playerSpriteSelection2.png", 3, 1);
        closeAbout_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "closeButton.png");

        try
        {
            this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0,1,0));
            this.menuTextureAtlas.load();
        }
        catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e)
        {
            Debug.e(e);
        }
    }

    private void loadHudGraphics(){
        hudTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),1024, 512, TextureOptions.BILINEAR);
        lives_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(hudTextureAtlas, activity, "heartSheet.png", 5, 1);
        mp_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(hudTextureAtlas, activity, "mpSheet.png", 4, 1);
        store_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(hudTextureAtlas, activity, "storeButton.png", 2, 1);
        goldHud_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(hudTextureAtlas, activity, "goldHud.png");

        try
        {
            this.hudTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
            this.hudTextureAtlas.load();
        }
        catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e)
        {
            Debug.e(e);
        }
    }

    private void loadGameGraphics()
    {
        int bg = (int) (Math.round(Math.random() * 8));
        if (bg == 0) {
            bg = 8;
        }
        mBackgroundBitmapTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
        game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBackgroundBitmapTextureAtlas, activity, "" + bg + ".png", 0, 0);
        mBackgroundBitmapTextureAtlas.load();

        gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),2048, 1024, TextureOptions.BILINEAR);
        player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "playerSprite.png",3,1);
        bat_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "bat.png", 3, 1);
        gold_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "gold.png",8,1);
        platform_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "platform.png");
        spikedPlatform_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "stakes.png");
        charge_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "charge.png", 12, 4);
        recharge_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "recharge.png", 12, 4);
        nextFloor_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "nextScreen.png");
        gameOver_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "gameover.png");
        replay_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "replay.png");
        home_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "home.png");
        blood_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "bloodSheet.png", 6, 1);
        door_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "door.png", 5, 1);


        try
        {
            this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0,1,0));
            this.gameTextureAtlas.load();
        }
        catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e)
        {
            Debug.e(e);
        }
    }

    private void loadStoreGraphics(){
        storeTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),512, 512, TextureOptions.BILINEAR);
        attackIncrease_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(storeTextureAtlas, activity, "increaseAttack_Button.png", 2, 1);
        defenseIncrease_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(storeTextureAtlas, activity, "increaseDefense_Button.png", 2, 1);
        addLife_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(storeTextureAtlas, activity, "heartButton.png",2,1);
        addMp_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(storeTextureAtlas, activity, "mpButton.png", 2, 1);
        closeStore_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(storeTextureAtlas, activity, "closeButton.png");

        try
        {
            this.storeTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
            this.storeTextureAtlas.load();
        }
        catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e)
        {
            Debug.e(e);
        }
    }


    private void loadGameFonts()
    {

        FontFactory.setAssetBasePath("font/");
        mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256,500, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        menuNameFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "8BIT_WONDER.TTF", 40, true, Color.WHITE_ABGR_PACKED_INT, 1, Color.BLACK_ABGR_PACKED_INT);
        menuNameFont.load();

        hudFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        hudNameFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "8BIT_WONDER.TTF", 30, true, Color.WHITE_ABGR_PACKED_INT, 3, Color.BLACK_ABGR_PACKED_INT);
        hudNameFont.load();

    }

    private void loadGameAudio()
    {
        SoundFactory.setAssetBasePath("mfx/");
        try
        {
            fireSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(),activity,"fireball.wav");
            wHitSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(),activity,"grunt.wav");
            mHitSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(),activity,"grunt2.wav");
            aHitSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(),activity,"grunt3.wav");
            attackSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(),activity,"hit.wav");
            lightningSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(),activity,"lightning.wav");
            coinSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(),activity,"money.wav");
            wDieSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(),activity,"yell.wav");
            mDieSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(),activity,"yell2.wav");
            aDieSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(),activity,"yell3.wav");

        }catch (final IOException e) {
            Debug.e(e);
        }

        MusicFactory.setAssetBasePath("mfx/");
        try {
            bgMusic = MusicFactory.createMusicFromAsset(activity.getEngine().getMusicManager(), activity, "bgMusic.mp3");
            bgMusic.setLooping(true);
        } catch (final IOException e) {
            Debug.e(e);
        }

    }
    public void unloadMenuResources(){
        menuTextureAtlas.unload();
        menuTextureAtlas = null;

    }
    public void unloadGameResources(){
        gameTextureAtlas.unload();
        gameTextureAtlas = null;
        storeTextureAtlas.unload();
        storeTextureAtlas = null;
        hudTextureAtlas.unload();
        hudTextureAtlas = null;

    }

    public void setPlayerChosen(int mPlayerChosen){
        playerChosen = mPlayerChosen;
    }
    public int getPlayerChosen(){
        return playerChosen;
    }



    public static void prepareManager(Engine engine, MainActivity activity, Camera camera, VertexBufferObjectManager vbom){
        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }
    public static ResourceManager getInstance(){
        return  INSTANCE;
    }
}
