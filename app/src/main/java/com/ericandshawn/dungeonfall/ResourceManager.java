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

    //IMAGES

    public BitmapTextureAtlas mBackgroundBitmapTextureAtlas;

    public ITextureRegion menu_background_region;
    protected ITextureRegion game_background_region;
    public ITextureRegion play_button_region;
    public ITextureRegion about_button_region;

    private BuildableBitmapTextureAtlas menuTextureAtlas;
    private BuildableBitmapTextureAtlas gameTextureAtlas;
    private BuildableBitmapTextureAtlas hudTextureAtlas;
    private BuildableBitmapTextureAtlas storeTextureAtlas;
    protected ITextureRegion player_region;
    protected ITiledTextureRegion bat_region;
    protected ITiledTextureRegion gold_region;
    protected ITextureRegion platform_region;
    protected ITextureRegion spikedPlatform_region;
    protected ITiledTextureRegion lives_region;
    protected ITiledTextureRegion mp_region;
    protected ITiledTextureRegion charge_region;
    protected ITextureRegion store_region;
    protected ITextureRegion goldHud_region;
    protected ITextureRegion attackIncrease_region;
    protected ITextureRegion addLife_region;
    protected ITextureRegion defenseIncrease_region;
    protected ITextureRegion addMp_region;
    protected ITextureRegion closeStore_region;
    protected ITextureRegion storeBackground_region;
    protected ITextureRegion nextFloor_region;
    protected ITextureRegion gameOver_region;

    //FONTS

    protected ITexture mainFontTexture;
    protected Font menuNameFont;
    protected ITexture hudFontTexture;
    protected Font hudNameFont;


    // SOUNDS

    protected Sound fireSound;
    protected Sound hitSound;
    protected Sound attackSound;
    protected Sound lightningSound;
    protected Sound dieSound;
    protected Sound coinSound;
    public Music bgMusic;


    public void loadMenuResources()
    {
        loadMenuGraphics();
        loadMenuAudio();
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
        //menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
        menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),512, 512, TextureOptions.BILINEAR);
        menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "1.png");
        play_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play.png");
        about_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "continue.png");

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

    private void loadMenuAudio()
    {

    }
    private void loadHudGraphics(){
        hudTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),1024, 1024, TextureOptions.BILINEAR);
        lives_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(hudTextureAtlas, activity, "heartSheet.png",5,1);
        mp_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(hudTextureAtlas, activity, "mpSheet.png", 4, 1);
        store_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(hudTextureAtlas, activity, "storeButton.png");
        goldHud_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(hudTextureAtlas, activity, "goldHud.png");
        //this.hudTextureAtlas.load();

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

        gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),4096, 1024, TextureOptions.BILINEAR);
        player_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "hero.png");
        bat_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "bat.png", 3, 1);
        gold_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "gold.png",8,1);
        platform_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "platform.png");
        spikedPlatform_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "stakes.png");
        nextFloor_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "nextScreen.png");
        gameOver_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "gameover.png");
        charge_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "charge.png", 12, 4);

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
        storeTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),1024, 1024, TextureOptions.BILINEAR);
        storeBackground_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(storeTextureAtlas, activity, "store.png");
        attackIncrease_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(storeTextureAtlas, activity, "increaseAttack_Button.png");
        defenseIncrease_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(storeTextureAtlas, activity, "increaseDefense_Button.png");
        addLife_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(storeTextureAtlas, activity, "heartButton.png");
        addMp_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(storeTextureAtlas, activity, "mpButton.png");
        closeStore_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(storeTextureAtlas, activity, "closeButton.png");

        //this.hudTextureAtlas.load();

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
        mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        //font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 50, true, Color.WHITE, 2, Color.BLACK);
        //menuNameFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 16, Color.BLACK);
        //menuNameFont = FontFactory.create(activity.getFontManager(),mainFontTexture,120,Color.WHITE_ARGB_PACKED_INT);
        menuNameFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "8BIT_WONDER.TTF", 75, true, Color.WHITE_ABGR_PACKED_INT, 2, Color.BLACK_ABGR_PACKED_INT);


        menuNameFont.load();

        hudFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        //font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 50, true, Color.WHITE, 2, Color.BLACK);
        //menuNameFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 16, Color.BLACK);
        hudNameFont = FontFactory.create(activity.getFontManager(),hudFontTexture,35,Color.WHITE_ABGR_PACKED_INT);

        hudNameFont.load();
    }

    private void loadGameAudio()
    {
        SoundFactory.setAssetBasePath("mfx/");
        try
        {
            fireSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(),activity,"fireball.wav");
            hitSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(),activity,"grunt.wav");
            attackSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(),activity,"hit.wav");
            lightningSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(),activity,"lightning.wav");
            coinSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(),activity,"money.wav");
            dieSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(),activity,"yell.wav");
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

    public void loadSplashScreen()
    {

    }

    public void unloadSplashScreen()
    {

    }
    public void unloadMenuResources(){
        menuTextureAtlas.unload();
        menuTextureAtlas = null;

    }
    public void unloadGameResources(){
        gameTextureAtlas.unload();
        gameTextureAtlas = null;

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
