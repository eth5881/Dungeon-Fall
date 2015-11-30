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
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
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
import org.andengine.util.HorizontalAlign;
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

    private Sprite mBg;

    private Sprite platform;
    private Body platformBody;

    private AnimatedSprite bat;
    private Body batBody;

    private Sprite stakes;
    private Body stakesBody;

    private AnimatedSprite gold;
    private Body goldBody;


    //HUD Sprites
    private Sprite mStore;
    private Sprite mGoldHud;
    private AnimatedSprite mMp;
    private AnimatedSprite mLives;


    private int floor;
    private int numbCoins;
    private long exp;
    private long level;

    @Override
    public void createScene() {
        setBackground(MainActivity.CAMERA_WIDTH/2-135, MainActivity.CAMERA_HEIGHT/2-240);
        createHUD();
        createPhysics();
        //addPlayer(50, 0);
        addFloorItems();
        setOnSceneTouchListener(this);
        //camera.setChaseEntity(player);
    }

    @Override
    public void onBackKeyPressed() {

    }

    @Override
    public void disposeScene() {
        ResourceManager.getInstance().unloadGameResources();
        //ResourceManager.getInstance().activity.disableAccelerometer();
    }
    private void createPhysics() {
        registerUpdateHandler(new FPSLogger());
        mPhysicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, SensorManager.GRAVITY_EARTH),false);
        //mPhysicsWorld = ResourceManager.getInstance().activity.mPhysicsWorld;
        registerUpdateHandler(mPhysicsWorld);

        //making sure hero doesn't go off screen
        final Rectangle left = new Rectangle(0, 0, 0, MainActivity.CAMERA_HEIGHT, vbom);
        final Rectangle right = new Rectangle(MainActivity.CAMERA_WIDTH, 0, 2, MainActivity.CAMERA_HEIGHT, vbom);

        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyDef.BodyType.StaticBody, wallFixtureDef);

        attachChild(left);
        attachChild(right);


    }
    private void setBackground(final float pX, final float pY){
        //final Sprite background;
        mBg = createSprite(pX, pY, ResourceManager.getInstance().game_background_region, vbom);
        mBg.setScale(4, 4);
        attachChild(mBg);
        //setBackground(new Background(Color.BLUE));
    }
    private void createHUD(){

        numbCoins = 10;
        level = 1;
        exp = 0;
        floor = 1;

        gameHUD = new HUD();
        // Draw the hud
        Text levelText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "Level: " + level, new TextOptions(HorizontalAlign.LEFT), vbom);
        //levelText.setColor(Color.BLACK);
        levelText.setPosition(MainActivity.CAMERA_WIDTH/2 + 220, 115);
        attachChild(levelText);

        Text floorText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "Floor: " + floor, new TextOptions(HorizontalAlign.LEFT), vbom);
        floorText.setPosition(20, 115);
        attachChild(floorText);

        Text expText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "Exp: " + exp, new TextOptions(HorizontalAlign.LEFT), vbom);
        expText.setPosition(MainActivity.CAMERA_WIDTH - 150, 115);
        attachChild(expText);

        Text mpText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "MP:", new TextOptions(HorizontalAlign.LEFT), vbom);
        mpText.setPosition((MainActivity.CAMERA_WIDTH /2) + 150,35);
        attachChild(mpText);

        Text coinText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, String.valueOf(numbCoins), new TextOptions(HorizontalAlign.LEFT), vbom);
        coinText.setPosition(270, 115);
        attachChild(coinText);

        mGoldHud = createSprite(220, 120, ResourceManager.getInstance().goldHud_region, vbom);
        mGoldHud.setScale(3, 3);
        attachChild(mGoldHud);

        mStore = createSprite(95, MainActivity.CAMERA_HEIGHT -70, ResourceManager.getInstance().store_region, vbom);
        mStore.setScale(3, 3);
        attachChild(mStore);

        mMp = createAnimatedSprite(MainActivity.CAMERA_WIDTH/2 + 350,  40, ResourceManager.getInstance().mp_region, vbom);
        mMp.setScale(3, 3);
        attachChild(mMp);

        mLives = createAnimatedSprite(120, 40, ResourceManager.getInstance().lives_region, vbom);
        mLives.setScale(3, 3);
        attachChild(mLives);


        //canvas.drawRect(screenY, 0, 10, 10, paint);
        //canvas.drawBitmap(hearts, 10, 20, paint);
        //canvas.drawBitmap(gold, 155, 78, paint);
        //canvas.drawText(String.valueOf(numbCoins), 205, 115, paint);
        //canvas.drawBitmap(powerups, 0, screenY - 100, paint);
        //canvas.drawBitmap(powerups2, screenX - 100, screenY - 100, paint);
        //canvas.drawRect((screenX / 3) + 200, 18, screenX - 10, 70, paint);


        //paint.setTextSize(35);
        //canvas.drawText("Level: " + level, (screenX / 3) + 190, 115, paint);
        //canvas.drawText("Floor: " + floor, 10, 115, paint);
        //canvas.drawText("Exp: " + exp, screenX - 130, 115, paint);
        //canvas.drawText("MP:", (screenX / 3) + 130, 55, paint);
        //canvas.drawText(String.valueOf(numbCoins), 205, 115, paint);

        camera.setHUD(gameHUD);


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

    private void addFloorItems(){
        final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0, 0.3f);

        for(int i=0;i<12;i++){
            if (Math.random() < 0.6) {
                //add bat
                bat = createAnimatedSprite((int) (Math.random() * 900), (int) (Math.random() * 1700), ResourceManager.getInstance().bat_region, vbom);
                bat.setScale(3, 3);
                batBody = PhysicsFactory.createCircleBody(mPhysicsWorld, bat, BodyDef.BodyType.StaticBody, objectFixtureDef);
                bat.animate(100);
                attachChild(bat);
                this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(bat, batBody, true, true));
            } else if (Math.round(Math.random() * 10) >= 9) {
                //add platform
                platform = createSprite((int) (Math.random() * 900), (int) (Math.random() * 1700), ResourceManager.getInstance().platform_region, vbom);
                platform.setScale(3, 3);
                platformBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, platform, BodyDef.BodyType.StaticBody, objectFixtureDef);
                attachChild(platform);
                mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(platform, platformBody, true, true));
            } else if (Math.round(Math.random() * 10) == 10) {
                //add platform with stakes
                stakes = createSprite((int) (Math.random() * 900), (int) (Math.random() * 1700), ResourceManager.getInstance().spikedPlatform_region, vbom);
                stakes.setScale(3, 3);
                stakesBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, stakes, BodyDef.BodyType.StaticBody, objectFixtureDef);
                attachChild(stakes);
                this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(stakes, stakesBody, true, true));
            } else {
                //add gold
                gold = createAnimatedSprite((int) (Math.random() * 900), (int) (Math.random() * 1700), ResourceManager.getInstance().gold_region, vbom);
                gold.setScale(3, 3);
                goldBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld, gold, BodyDef.BodyType.StaticBody, objectFixtureDef);
                gold.animate(100);
                attachChild(gold);
                this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(gold, goldBody, true, true));
            }
        }
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

    }

    @Override
    public void onAccelerationChanged(AccelerationData pAccelerationData) {
       // Log.d("GameScene", "Acclerometer = " + pAccelerationData);
        final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX() * 2, SensorManager.GRAVITY_EARTH * 1.5f);
        mPhysicsWorld.setGravity(gravity);
        Vector2Pool.recycle(gravity);
        //playerBody.applyForce(new Vector2(pAccelerationData.getX(), 0),playerBody.getWorldCenter());
    }
    //Enable Accelerometer through MainActivity
    public GameScene(MainActivity object) {
        //ResourceManager.getInstance().engine.enableAccelerationSensor(object,this);
        object.getEngine().enableAccelerationSensor(object,this);
    }
}
