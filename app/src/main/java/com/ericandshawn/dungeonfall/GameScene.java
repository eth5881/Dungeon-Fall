package com.ericandshawn.dungeonfall;

import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
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
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

/**
 * Created by Shawn on 11/18/2015.
 */
public class GameScene extends BaseScene implements IOnSceneTouchListener,IAccelerationListener {

    private HUD gameHUD;
    private PhysicsWorld mPhysicsWorld;

    private Sprite player;
    private Body playerBody;
    protected ITextureRegion mPlayer;

    @Override
    public void createScene() {
        setBackground();
        createHUD();
        createPhysics();
        //addPlayer(50, 0);

        setOnSceneTouchListener(this);
        //camera.setChaseEntity(player);
    }

    @Override
    public void onBackKeyPressed() {

    }

    @Override
    public void disposeScene() {
        ResourceManager.getInstance().unloadGameResources();
        ResourceManager.getInstance().activity.disableAccelerometer();
    }

    private void setBackground(){
        setBackground(new Background(Color.BLUE));
    }
    private void createHUD(){
        gameHUD = new HUD();
        camera.setHUD(gameHUD);
    }
    private void createPhysics() {
        registerUpdateHandler(new FPSLogger());
        mPhysicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, SensorManager.GRAVITY_EARTH),false);
        //mPhysicsWorld = ResourceManager.getInstance().activity.mPhysicsWorld;
        registerUpdateHandler(mPhysicsWorld);

    }
    private void addPlayer(final float pX, final float pY) {

        final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.7f, 0.3f);

        player = createSprite(pX, pY, ResourceManager.getInstance().player_region, vbom);
        player.setScale(3, 3);
        playerBody = PhysicsFactory.createBoxBody(mPhysicsWorld, player, BodyDef.BodyType.DynamicBody, objectFixtureDef);

        //Sprite, body, updatePosition, updateRotation
        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(player, playerBody, true, true));

        attachChild(player);
    }

    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
        if (mPhysicsWorld != null) {
            if (pSceneTouchEvent.isActionDown()) {
                addPlayer(pSceneTouchEvent.getX(), -150);
                return true;
            }
        }
        return false;
    }


    @Override
    public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
        final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX() * 2, SensorManager.GRAVITY_EARTH * 1.5f);
        mPhysicsWorld.setGravity(gravity);
        Vector2Pool.recycle(gravity);
    }

    @Override
    public void onAccelerationChanged(AccelerationData pAccelerationData) {
        Log.d("GameScene", "Acclerometer = " + pAccelerationData);
    }
    //Enable Accelerometer through MainActivity
    public GameScene(MainActivity object) {
        object.getEngine().enableAccelerationSensor(object,this);
    }
}
