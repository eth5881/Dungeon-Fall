package com.ericandshawn.dungeonfall;

import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.LongMap;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.Color;

import java.security.Key;

/**
 * Created by Shawn on 11/18/2015.
 */
public class GameScene extends BaseScene  implements IOnSceneTouchListener {

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
        addPlayer(250, 0);
        setOnSceneTouchListener(activity);
        //camera.setChaseEntity(player);
    }

    @Override
    public void onBackKeyPressed() {

    }

    @Override
    public void disposeScene() {
        ResourceManager.getInstance().unloadGameResources();

    }

    private void setBackground(){
        setBackground(new Background(Color.BLUE));
    }
    private void createHUD(){
        gameHUD = new HUD();
        camera.setHUD(gameHUD);
    }
    private void createPhysics() {
        mPhysicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, SensorManager.GRAVITY_EARTH),false);
        //mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
        registerUpdateHandler(mPhysicsWorld);


    }
    private void addPlayer(final float pX, final float pY) {
        final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.7f, 0.3f);

        //player = createSprite(250,0,ResourceManager.getInstance().player_region,vbom);
        player = createSprite(pX, pY, ResourceManager.getInstance().player_region, vbom);
        player.setScale(3,3);
        playerBody = PhysicsFactory.createBoxBody(mPhysicsWorld, player, BodyDef.BodyType.DynamicBody, objectFixtureDef);

        //mScene.attachChild(this.player);
        //Sprite, body, updatePosition, updateRotation
        attachChild(player);
        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(player, playerBody, true, true));

    }

    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
        if (this.mPhysicsWorld != null) {
            if (pSceneTouchEvent.isActionDown()) {
                addPlayer(pSceneTouchEvent.getX(), -150);
                Log.d("GameScene", "Player added");
                return true;
            }
        }
        return false;
    }
}
