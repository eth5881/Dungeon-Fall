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
    protected ITextureRegion mBg;
    private BitmapTextureAtlas mBitmapTextureAtlas;
    private BitmapTextureAtlas mBackgroundBitmapTextureAtlas;

    public ITextureRegion menu_background_region;
    public ITextureRegion play_button_region;
    public ITextureRegion about_button_region;

    private BuildableBitmapTextureAtlas menuTextureAtlas;
    private BuildableBitmapTextureAtlas gameTextureAtlas;
    public ITextureRegion player_region;




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

        //setup random background
        int bg = (int)(Math.round(Math.random()*8));
        if(bg == 0){
            bg = 8;
        }
        mBackgroundBitmapTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
        mBg = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBackgroundBitmapTextureAtlas, activity, "" + bg + ".png", 0, 0);
        mBackgroundBitmapTextureAtlas.load();

        //menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
        menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),512, 512, TextureOptions.BILINEAR);
        menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "" + bg + ".png");
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
        gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),512, 512, TextureOptions.BILINEAR);
        player_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "hero.png");
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
