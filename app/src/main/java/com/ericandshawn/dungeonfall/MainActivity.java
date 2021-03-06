package com.ericandshawn.dungeonfall;


import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;


public class MainActivity extends BaseGameActivity {
    // ===========================================================
    // Constants
    // ===========================================================

    protected static final int CAMERA_WIDTH = 1200;
    protected static final int CAMERA_HEIGHT = 1920;

    // ===========================================================
    // Fields
    // ===========================================================

    private Camera mCamera;

    // ===========================================================
    // Constructors
    // ===========================================================

    @Override
    public EngineOptions onCreateEngineOptions() {
        mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions options = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), mCamera);
        options.getAudioOptions().setNeedsMusic(true);
        options.getAudioOptions().setNeedsSound(true);
        return options;
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
        if (SceneManager.getInstance().bgMusicPlaying) {
            ResourceManager.getInstance().bgMusic.stop();
       }
    }
    @Override
    protected void onPause(){
        super.onPause();
        //if background music is playing
        if (SceneManager.getInstance().bgMusicPlaying){
            ResourceManager.getInstance().bgMusic.pause();
        }
    }

    @Override
    protected synchronized void onResume(){
        super.onResume();
        System.gc();
        if(isGameLoaded()) {
            if (SceneManager.getInstance().bgMusicPlaying) {
                ResourceManager.getInstance().bgMusic.play();
            }
        }
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
}