package com.ericandshawn.dungeonfall;

import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
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
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import java.util.ArrayList;

/**
 * Created by Shawn on 11/18/2015.
 */
public class GameScene extends BaseScene implements IOnSceneTouchListener,IAccelerationListener {

    private HUD gameHUD;
    private PhysicsWorld mPhysicsWorld;

    private Sprite player;
    private Body playerBody;

    private Sprite mBg;

    private ArrayList<Coin> coinList;
    private ArrayList<Enemy> enemyList;

    private boolean playerDrop = false;


    //HUD Sprites
    private Sprite mStore;
    private Sprite mGoldHud;
    private AnimatedSprite mMp;
    private AnimatedSprite mLives;

    Text coinText;

    private int floor;

    private int goldAmount;
    private long exp;
    private long level;
    private int lives;
    private int mp;

    private Scene mStoreScene;
    @Override
    public void createScene() {
        setBackground(MainActivity.CAMERA_WIDTH / 2 - 135, MainActivity.CAMERA_HEIGHT / 2 - 240);
        createHUD();
        createPhysics();
        addFloorItems();
        setOnSceneTouchListener(this);
        //camera.setChaseEntity(player);
        makeStoreScene();
    }

    @Override
    public void onBackKeyPressed() {

    }

    @Override
    public void disposeScene() {
        ResourceManager.getInstance().unloadGameResources();
    }
    private void createPhysics() {
        registerUpdateHandler(new FPSLogger());
        mPhysicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, SensorManager.GRAVITY_EARTH),false);
        mPhysicsWorld.setContactListener(createContactListener());

        //making sure hero doesn't go off screen
        final Rectangle left = new Rectangle(0, 0, 0, MainActivity.CAMERA_HEIGHT, vbom);
        final Rectangle right = new Rectangle(MainActivity.CAMERA_WIDTH, 0, 2, MainActivity.CAMERA_HEIGHT, vbom);

        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
        PhysicsFactory.createBoxBody(mPhysicsWorld, left, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(mPhysicsWorld, right, BodyDef.BodyType.StaticBody, wallFixtureDef);

        attachChild(left);
        attachChild(right);

        registerUpdateHandler(mPhysicsWorld);
        registerUpdateHandler(new IUpdateHandler() {
            public void reset() {
            }

            public void onUpdate(float pSecondsElapsed) {
                //Game loop
                if (player != null) {
                    //if hero leaves the screen, detach sprite from scene and destroy body
                    if (player.getY() > MainActivity.CAMERA_HEIGHT) {
                        mPhysicsWorld.destroyBody(playerBody);
                        player.detachSelf();
                        player.dispose();
                        player = null;
                        playerDrop = false;
                    }
                    //set user data for enemies
                    for (int i = 0; i < enemyList.size(); i++) {
                        String batString = "bat" + i;
                        enemyList.get(i).body.setUserData(batString);
                    }
                    //set user data for coins
                    for (int j = 0; j < coinList.size(); j++) {
                        String coinString = "coin" + j;
                        coinList.get(j).body.setUserData(coinString);
                    }
                }
            }
        });
    }
    private ContactListener createContactListener()
    {
        ContactListener contactListener = new ContactListener()
        {
            @Override
            public void beginContact(Contact contact)
            {
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();
                final Body body1 = x1.getBody();
                final Body body2 = x2.getBody();

                //check if player and coin collide
                for(int i=0;i<coinList.size();i++) {
                    String coinData = "coin" + i;
                    if(("player".equals(body1.getUserData()) && coinData.equals(body2.getUserData())) || ("player".equals(body2.getUserData()) && coinData.equals(body1.getUserData()))) {
                        //collect coin and add it to gold amount
                        destroyCoin(coinList.get(i));
                        goldAmount = goldAmount + Math.round((int)(Math.random() * 25));
                        //coinText.setText(String.valueOf(goldAmount));
                    }
                }
            }

            @Override
            public void endContact(Contact contact)
            {
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();
                final Body body1 = x1.getBody();
                final Body body2 = x2.getBody();

                //check if player and enemy collide
                for(int i=0;i<enemyList.size();i++) {
                    String batData = "bat" + i;
                    if(("player".equals(body1.getUserData()) && batData.equals(body2.getUserData())) || ("player".equals(body2.getUserData()) && batData.equals(body1.getUserData()))) {
                        //kill enemy and add experience
                        destroyEnemy(enemyList.get(i));
                    }
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold)
            {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse)
            {

            }
        };
        return contactListener;
    }

    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
        if (mPhysicsWorld != null) {
            if (pSceneTouchEvent.isActionDown() && !playerDrop) {
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
        final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX() * 2, SensorManager.GRAVITY_EARTH * 1.5f);
        mPhysicsWorld.setGravity(gravity);
        Vector2Pool.recycle(gravity);
    }
    //Enable Accelerometer through MainActivity
    public GameScene(MainActivity object) {
        object.getEngine().enableAccelerationSensor(object,this);
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void setBackground(final float pX, final float pY){
        mBg = createSprite(pX, pY, ResourceManager.getInstance().game_background_region, vbom);
        mBg.setScale(4, 4);
        attachChild(mBg);
    }
    private void createHUD(){


        goldAmount = 0;
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

        coinText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, String.valueOf(goldAmount), new TextOptions(HorizontalAlign.LEFT), vbom);
        coinText.setPosition(270, 115);
        attachChild(coinText);

        mGoldHud = createSprite(220, 120, ResourceManager.getInstance().goldHud_region, vbom);
        mGoldHud.setScale(3, 3);
        attachChild(mGoldHud);

        //mStore =  createSprite(95, MainActivity.CAMERA_HEIGHT -70, ResourceManager.getInstance().store_region, vbom);{
        //mStore.setScale(3, 3);
        //attachChild(mStore);

        mStore = new Sprite(MainActivity.CAMERA_WIDTH - 105, MainActivity.CAMERA_HEIGHT -100, ResourceManager.getInstance().store_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                Log.d("GameScene", "Store touched");
                //SceneManager.getInstance().setStoreScene();
                setChildScene(mStoreScene, false, true, true);
                //makeStoreScene();
                //activity.disableAccelerometer();
                return true;
            }
        };
        mStore.setScale(3, 3);
        registerTouchArea(mStore);
        attachChild(mStore);


        mMp = createAnimatedSprite(MainActivity.CAMERA_WIDTH/2 + 350,  40, ResourceManager.getInstance().mp_region, vbom);
        mMp.setScale(3, 3);
        attachChild(mMp);

        mLives = createAnimatedSprite(120, 40, ResourceManager.getInstance().lives_region, vbom);
        mLives.setScale(3, 3);
        attachChild(mLives);

        camera.setHUD(gameHUD);
    }

    private void addPlayer(final float pX, final float pY) {
        final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.7f, 0.3f);

        playerDrop = true;
        player = new Sprite(pX, pY, ResourceManager.getInstance().player_region, vbom);
        player.setScale(3,3);
        playerBody = PhysicsFactory.createBoxBody(mPhysicsWorld, player, BodyDef.BodyType.DynamicBody, objectFixtureDef);
        playerBody.setUserData("player");

        attachChild(player);
        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(player, playerBody, true, true));
    }

    private void addFloorItems(){
        final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0, 0.3f);
        coinList = new ArrayList<>();
        enemyList = new ArrayList<>();
        for(int i=0;i<12;i++){
            if (Math.random() < 0.5) {
                //add bat
                enemyList.add(new Enemy((int) (Math.random() * 900), (int) (Math.random() * 1700), ResourceManager.getInstance().bat_region, vbom, mPhysicsWorld, this, null, 3, 3, 100));
            } else if (Math.round(Math.random() * 10) >= 8) {
                //add platform
                final Sprite platform;
                final Body platformBody;

                platform = new Sprite((int) (Math.random() * 900), (int) (Math.random() * 1700), ResourceManager.getInstance().platform_region, vbom);
                platform.setScale(3, 3);
                platformBody = PhysicsFactory.createBoxBody(mPhysicsWorld, platform, BodyDef.BodyType.StaticBody, objectFixtureDef);

                attachChild(platform);
                mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(platform, platformBody, true, true));
            } else if (Math.round(Math.random() * 10) >= 9) {
                //add platform with stakes
                final Sprite stakes;
                final Body stakesBody;

                stakes = new Sprite((int) (Math.random() * 900), (int) (Math.random() * 1700), ResourceManager.getInstance().spikedPlatform_region, vbom);
                stakes.setScale(3, 3);
                stakesBody = PhysicsFactory.createBoxBody(mPhysicsWorld, stakes, BodyDef.BodyType.StaticBody, objectFixtureDef);

                attachChild(stakes);
                mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(stakes, stakesBody, true, true));
            } else {
                //add gold
                coinList.add(new Coin((int) (Math.random() * 900), (int) (Math.random() * 1700), ResourceManager.getInstance().gold_region, vbom, mPhysicsWorld, this, null, 3, 3, 100));
            }
        }
    }
    private void makeStoreScene(){

        mStoreScene =  new Scene();
        mStoreScene.setBackground(new Background(Color.BLACK));
        //setBackground(MainActivity.CAMERA_WIDTH, MainActivity.CAMERA_HEIGHT - 90);


        Text levelText = new Text(0, 0, ResourceManager.getInstance().menuNameFont, "Store", new TextOptions(HorizontalAlign.LEFT), vbom);
        levelText.setColor(Color.WHITE);
        levelText.setPosition(MainActivity.CAMERA_WIDTH / 2 - 150, 30);
        mStoreScene.attachChild(levelText);

        mStore = new Sprite(MainActivity.CAMERA_WIDTH-85, 45, ResourceManager.getInstance().closeStore_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                //Insert Code Here
                //SceneManager.getInstance().setGameScene();
                clearChildScene();
                //activity.enableAccelerometer();
                //engine.enableAccelerationSensor(MainActivity,this);
                return true;
            }
        };
        mStore.setScale(3, 3);
        mStoreScene.registerTouchArea(mStore);
        mStoreScene.attachChild(mStore);

        mStore = new Sprite(220, MainActivity.CAMERA_HEIGHT/3 +150, ResourceManager.getInstance().attackIncrease_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

                 //Insert Code Here
                //SceneManager.getInstance().setGameScene();
                //goldAmount+=10;
                //Log.d("GameScene", "numbCoins = " + goldAmount);
                //engine.start();
                return true;
            }
        };
        mStore.setScale(3, 3);
        mStoreScene.registerTouchArea(mStore);
        mStoreScene.attachChild(mStore);

        mStore = new Sprite(MainActivity.CAMERA_WIDTH/2 + 300, MainActivity.CAMERA_HEIGHT/3 +150, ResourceManager.getInstance().defenseIncrease_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                //Insert Code Here
                //SceneManager.getInstance().setGameScene();
                //goldAmount+=10;
                //Log.d("GameScene", "numbCoins = " + goldAmount);
                //engine.start();
                return true;
            }
        };
        mStore.setScale(3, 3);
        mStoreScene.registerTouchArea(mStore);
        mStoreScene.attachChild(mStore);

        mStore = new Sprite(220, MainActivity.CAMERA_HEIGHT/2 +350, ResourceManager.getInstance().addLife_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                //Insert Code Here
                //SceneManager.getInstance().setGameScene();
                lives += 1;
                //Log.d("GameScene", "numbCoins = " + lives);
                //engine.start();
                return true;
            }
        };
        mStore.setScale(3, 3);
        mStoreScene.registerTouchArea(mStore);
        mStoreScene.attachChild(mStore);

        mStore = new Sprite(MainActivity.CAMERA_WIDTH/2 + 300, MainActivity.CAMERA_HEIGHT/2 +350, ResourceManager.getInstance().addMp_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                //Insert Code Here
                //SceneManager.getInstance().setGameScene();
                mp+=1;
                Log.d("GameScene", "mp = " + mp);
                //engine.start();
                return true;
            }
        };
        mStore.setScale(3, 3);
        mStoreScene.registerTouchArea(mStore);
        mStoreScene.attachChild(mStore);
        //mStoreScene.setBackgroundEnabled(false);


    }

    private void destroyEnemy(final Enemy enemy)
    {
        activity.runOnUpdateThread(new Runnable() {

            @Override
            public void run() {
                final Body body = enemy.body;
                mPhysicsWorld.unregisterPhysicsConnector(mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(enemy));
                mPhysicsWorld.destroyBody(body);
                detachChild(enemy);
                enemyList.remove(enemy);
            }
        });
    }

    private void destroyCoin(final Coin coin)
    {
        activity.runOnUpdateThread(new Runnable() {

            @Override
            public void run() {
                final Body body = coin.body;
                mPhysicsWorld.unregisterPhysicsConnector(mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(coin));
                mPhysicsWorld.destroyBody(body);
                detachChild(coin);
                enemyList.remove(coin);
            }
        });
    }
}


