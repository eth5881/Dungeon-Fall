package com.ericandshawn.dungeonfall;

import android.hardware.SensorManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.IGameInterface;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;


public class MainActivity extends BaseGameActivity {
    // ===========================================================
    // Constants
    // ===========================================================

    protected static final int CAMERA_WIDTH = 1200;
    protected static final int CAMERA_HEIGHT = 1920;

    // ===========================================================
    // Fields
    // ===========================================================

    private BitmapTextureAtlas mBitmapTextureAtlas;
    private BitmapTextureAtlas mBackgroundBitmapTextureAtlas;

    private Scene mScene;
    private Camera mCamera;

    protected ITiledTextureRegion mBat;
    protected ITiledTextureRegion mGold;
    protected ITextureRegion mPlayer;
    protected ITextureRegion mPlatform;
    protected ITextureRegion mSpikedPlatform;
    protected ITextureRegion mBg;

    protected PhysicsWorld mPhysicsWorld;

    private Sprite player;
    private Body playerBody;
    private int accellerationSpeedX;


    // ===========================================================
    // Constructors
    // ===========================================================

    @Override
    public EngineOptions onCreateEngineOptions() {
        mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        //mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
        //mPhysicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, SensorManager.GRAVITY_EARTH),false);


        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), mCamera);
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback cb) throws Exception {
        ResourceManager.prepareManager(getEngine(), this, mCamera, getVertexBufferObjectManager());
        cb.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback cb) throws Exception {
        ResourceManager.getInstance().loadMenuResources();
        SceneManager.getInstance().setMenuScene(cb);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback cb) throws Exception {
        cb.onPopulateSceneFinished();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        System.exit(0);
    }
    // If the player hits the back button, quit the app
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //finish();
            SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
            return true;
        }
        return false;
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        final View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
    /*
    @Override
    public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
        final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX() * 2, SensorManager.GRAVITY_EARTH*1.5f);
        //Log.d("MainActivity", "physics = " + mPhysicsWorld);
        //get physics world from Game scene
        // tried passing physics world from MainActivity to gameScene but didn't work(this is what's currently set up now)
        // mPhysicsWorld = getPhysics();
        mPhysicsWorld.setGravity(gravity);
        Vector2Pool.recycle(gravity);
    }
    @Override
    public void onAccelerationChanged(AccelerationData pAccelerationData) {
        //accellerationSpeedX = (int)pAccelerationData.getX();

        //   accellerometerSpeedY = (int)pAccelerometerData.getY();
        Log.d("MainActivity", "Acclerometer = " + pAccelerationData);
        //Log.d("MainActivity", "Acclerometer Speed = " + accellerationSpeedX);
        //mySprite.setPosition(mySprite.getX() + myAccelerometerData.getX(), mySprite.getY() + myAccelerometerData.getY());

    }
    public void enableAccelerometer()
    {
        //only works with BaseGameActivity
        enableAccelerationSensor(this);
    }
    public void disableAccelerometer(){ disableAccelerationSensor(); }
    */

     /*@Override
    public void onResumeGame(){
        super.onResumeGame();
        this.enableAccelerationSensor(this);
    }*/
    /*@Override
    public void onPauseGame() {
        super.onPauseGame();
        this.disableAccelerationSensor();
    }*/

    public void disableAccelerometer(){ disableAccelerationSensor(); }
    public void enableAccelerometer(IAccelerationListener cb){ enableAccelerationSensor(cb); }

}