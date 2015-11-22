package com.ericandshawn.dungeonfall;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.extension.physics.box2d.PhysicsWorld;
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
import org.andengine.util.debug.Debug;

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

    private BitmapTextureAtlas mBitmapTextureAtlas;
    private BitmapTextureAtlas mBackgroundBitmapTextureAtlas;

    public ITextureRegion menu_background_region;
    protected ITextureRegion game_background_region;
    public ITextureRegion play_button_region;
    public ITextureRegion about_button_region;

    private BuildableBitmapTextureAtlas menuTextureAtlas;
    private BuildableBitmapTextureAtlas gameTextureAtlas;
    protected ITextureRegion player_region;
    protected ITiledTextureRegion bat_region;
    protected ITiledTextureRegion gold_region;
    protected ITextureRegion platform_region;
    protected ITextureRegion spikedPlatform_region;




    public void loadMenuResources()
    {
        loadMenuGraphics();
        loadMenuAudio();
    }
    public void loadGameResources()
    {
        loadGameGraphics();
        loadGameFonts();
        loadGameAudio();
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

    private void loadGameGraphics()
    {

        //setup random background
        int bg = (int)(Math.round(Math.random()*8));
        if(bg == 0){
            bg = 8;
        }
        mBackgroundBitmapTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
        game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBackgroundBitmapTextureAtlas, activity, "" + bg + ".png", 0, 0);
        mBackgroundBitmapTextureAtlas.load();


        gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),512, 512, TextureOptions.BILINEAR);
        player_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "hero.png");
        bat_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "bat.png",3,1);
        gold_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "gold.png",8,1);
        platform_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "platform.png");
        spikedPlatform_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "stakes.png");

        //mBat = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, activity, "bat.png", 35, 0, 3, 1);
        //mGold = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, activity, "gold.png", 185, 0, 8, 1);
        //mPlatform = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, activity, "platform.png", 0, 51);
        //mSpikedPlatform = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, activity, "stakes.png", 71, 51);
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

    private void loadGameFonts()
    {

    }

    private void loadGameAudio()
    {

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
