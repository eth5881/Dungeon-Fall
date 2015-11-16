package com.hunt.eric.dungeonfall;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.view.WindowManager;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.badlogic.gdx.math.Vector2;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import java.io.IOException;


public class GameActivity extends SimpleBaseGameActivity implements View.OnClickListener, IAccelerationListener {

    private TDView gameView;
    private Button mStoreButton;
    //private ResourcesManager resourceManager;
    private PhysicsWorld mPhysicsWorld;
    private Camera mCamera;
    Scene scene = new Scene();
    public static final int CAMERA_WIDTH = 320;
    public static final int CAMERA_HEIGHT = 480;
    private SceneManager mSceneManager;
    private ResourceManager mResourceManager;

    /*
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create an instance of our Tappy Defender View (TDView)
        // passing in "this" which is the Context of our app, and
        // screen resolution of device being used to run app
        gameView = new TDView(this, size.x, size.y);
        // Make our gameView the view for the Activity
        setContentView(gameView);
        //setContentView(R.layout.activity_game);
        //gameView = (TDView)findViewById(R.id.custom_view);



        // mStoreButton = (Button)findViewById(R.id.store_button);
        //setContentView(R.layout.activity_game);
        //mStoreButton.setOnClickListener(this);

    }

    // If the Activity is paused make sure to pause our thread
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    // If the Activity is resumed make sure to resume our thread
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
    */

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

    // If the player hits the back button, quit the app
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onAccelerationChanged(AccelerationData pAccelerationData) {
        final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX() * 3, pAccelerationData.getY() * 3);
        this.mPhysicsWorld.setGravity(gravity);
        Vector2Pool.recycle(gravity);

    }
    @Override
    public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
        // TODO Auto-generated method stub

    }

    @Override
    public EngineOptions onCreateEngineOptions() {

        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create an instance of our Tappy Defender View (TDView)
        // passing in "this" which is the Context of our app, and
        // screen resolution of device being used to run app
        //mCamera = new Camera(0,0,CAMERA_WIDTH,CAMERA_HEIGHT);
        //return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
        mCamera = new Camera(0,0,size.x,size.y);
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(size.x, size.y), mCamera);


    }

    @Override
    protected void onCreateResources() throws IOException {

    }

    @Override
    protected Scene onCreateScene() {
        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);


        //gameView = new TDView(this, size.x, size.y);
        // Make our gameView the view for the Activity
        //setContentView(gameView);

        mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                mResourceManager.loadGameResources();
                mSceneManager.setScene(SceneManager.SceneType.SCENE_MENU);
                mResourceManager.unloadSplashResources();
            }
        }));
        return mSceneManager.createSplashScene();
    }
    @Override
    public void onBackPressed() {
        if (mSceneManager.getCurrentScene() != null) {
            mSceneManager.getCurrentScene().onBackKeyPressed();
            return;
        }
        super.onBackPressed();
    }
}
