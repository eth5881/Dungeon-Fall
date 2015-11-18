package com.ericandshawn.dungeonfall;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Shawn on 11/17/2015.
 */
public abstract class BaseScene extends Scene {
    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------

    protected Engine engine;
    protected MainActivity activity;
    protected ResourceManager resourcesManager;
    protected VertexBufferObjectManager vbom;
    protected Camera camera;

    //---------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------

    public BaseScene()
    {
        this.resourcesManager = ResourceManager.getInstance();
        this.engine = resourcesManager.engine;
        this.activity = resourcesManager.activity;
        this.vbom = resourcesManager.vbom;
        this.camera = resourcesManager.camera;
        createScene();
    }

    protected Sprite createSprite(float x, float y, ITextureRegion region, VertexBufferObjectManager vbom){
        Sprite sprite = new Sprite(x , y, region, vbom) {

            @Override
            protected void preDraw(GLState mGlState, Camera camera){
                super.preDraw(mGlState, camera);
                mGlState.enableDither();
            }
        };
        return sprite;
    }

    //---------------------------------------------
    // ABSTRACTION
    //---------------------------------------------

    public abstract void createScene();

    public abstract void onBackKeyPressed();

    /*public abstract SceneType getSceneType();*/

    public abstract void disposeScene();
}
