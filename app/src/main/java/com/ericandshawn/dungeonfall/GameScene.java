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
    private ArrayList<GameObject> gameItems;

    private boolean playerDrop = false;
    private boolean isAttacking = false;
    private boolean isDead = false;

    //HUD Sprites
    private Sprite mStore;
    private Sprite mGoldHud;
    private AnimatedSprite mMp;
    private AnimatedSprite mLives;
    private Sprite mNextScreen;
    private Sprite mGameOverScreen;
    private AnimatedSprite mCharge;

    Text coinText;
    Text levelText;
    Text floorText;
    Text expText;
    Text mpText;

    private int floor = 1;
    private int goldAmount = 0;
    private long exp = 0;
    private long level = 1;
    private int lives = 2;
    private int mp = 3;
    private int help;

    private Scene mStoreScene;
    private Scene mNextScreenScene;
    private Scene mGameOverScene;

    @Override
    public void createScene() {
        createPhysics();
        setOnSceneTouchListener(this);
        if (!ResourceManager.getInstance().bgMusic.isPlaying()) {
            ResourceManager.getInstance().bgMusic.play();
        }
        resetScene();
    }

    public void resetScene() {
        setBackground(MainActivity.CAMERA_WIDTH / 2 - 135, MainActivity.CAMERA_HEIGHT / 2 - 240);
        createHUD();
        addFloorItems();
        makeStoreScene();
        makeNextScreen();
        makeGameOverScene();
    }

    public void cleanScene(){
        for(int i=0;i<enemyList.size();i++){
            destroyEnemy(enemyList.get(i));
            //Log.d("GameScene", "loop list reset = " + enemyList.size());
        }
        for(int i=0;i<coinList.size();i++){
            destroyCoin(coinList.get(i));
            //Log.d("GameScene", "Loop coin list removed = " + coinList.size());
        }
        for(int i=0;i<gameItems.size();i++){
            destroyGameItems(gameItems.get(i));
            //Log.d("GameScene", "Loop game list removed = " + gameItems.size());

        }

        enemyList.clear();
        coinList.clear();
        gameItems.clear();

        detachChild(mBg);
        mBg.dispose();
        detachChild(mStore);
        detachChild(mGoldHud);
        detachChild(mMp);
        detachChild(mLives);
        detachChild(coinText);
        detachChild(levelText);
        detachChild(floorText);
        detachChild(expText);
        detachChild(mpText);
        mStore.dispose();
        mGoldHud.dispose();
        mMp.dispose();
        mLives.dispose();
        coinText.dispose();
        levelText.dispose();
        floorText.dispose();
        expText.dispose();
        mpText.dispose();
    }

    private void restartGame(){
        floor = 1;
        goldAmount = 0;
        exp = 0;
        level = 1;
        lives = 2;
        mp = 3;
        disposeScene();
        cleanScene();
        ResourceManager.getInstance().loadGameResources();
        resetScene();
        clearChildScene();
        isDead = false;
        if (!ResourceManager.getInstance().bgMusic.isPlaying()) {
            ResourceManager.getInstance().bgMusic.play();
        }
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
                if (playerDrop) {
                    //if hero leaves the screen, detach sprite from scene and destroy body
                    if ((player.getY() > MainActivity.CAMERA_HEIGHT) && !isDead) {
                        mPhysicsWorld.destroyBody(playerBody);
                        player.detachSelf();
                        player.dispose();
                        playerDrop = false;
                        setChildScene(mNextScreenScene, false, true, true);
                    }
                    if(isAttacking){
                        mCharge.setVisible(true);
                        mCharge.setX((player.getX()) - (player.getWidth()+40));
                        mCharge.setY((player.getY()) - (player.getHeight()+15));
                    }else{
                        mCharge.setVisible(false);
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
                else{
                    //Log.d("GameScene", "Broken on update");
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
                        Log.d("HIT", "" + coinList.get(i));
                        destroyCoin(coinList.get(i));
                        int randNum = Math.round((int)(Math.random() * 25));
                        if(randNum == 0){
                            randNum = 1;
                        }
                        goldAmount = goldAmount + randNum;
                        coinText.setText(String.valueOf(goldAmount));
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
                        if(isAttacking){
                            //kill enemy and add experience
                            Log.d("HIT", "" + enemyList.get(i));
                            destroyEnemy(enemyList.get(i));
                        }else {
                            if(lives != 0) {
                                ResourceManager.getInstance().hitSound.play();
                                lives--;
                                mLives.setCurrentTileIndex(lives);
                            }else {
                                ResourceManager.getInstance().bgMusic.stop();
                                ResourceManager.getInstance().dieSound.play();
                                isDead = true;
                                mPhysicsWorld.destroyBody(playerBody);
                                player.detachSelf();
                                player.dispose();
                                playerDrop = false;
                                setChildScene(mGameOverScene, false, true, true);
                            }
                        }
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
            }else if(pSceneTouchEvent.isActionDown()){
                isAttacking = true;
                mCharge.animate(20);
                Log.d("HOLDING", "" + isAttacking);
            }else if(pSceneTouchEvent.isActionUp() && playerDrop){
                isAttacking = false;
                mCharge.stopAnimation();
                Log.d("HOLDING", "" + isAttacking);
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
        if(floor < 1) {
            gameHUD = new HUD();
            int textLength = 4;
            // Draw the hud
            levelText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "Level: 1", new TextOptions(HorizontalAlign.LEFT), vbom);
            //levelText.setColor(Color.BLACK);
            levelText.setPosition(MainActivity.CAMERA_WIDTH / 2 + 220, 115);
            attachChild(levelText);

            floorText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "Floor: 1", new TextOptions(HorizontalAlign.LEFT), vbom);
            floorText.setPosition(20, 115);
            attachChild(floorText);

            expText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "Exp: 0", new TextOptions(HorizontalAlign.LEFT), vbom);
            expText.setPosition(MainActivity.CAMERA_WIDTH - 150, 115);
            attachChild(expText);

            mpText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "MP:", new TextOptions(HorizontalAlign.LEFT), vbom);
            mpText.setPosition((MainActivity.CAMERA_WIDTH / 2) + 150, 35);
            attachChild(mpText);

            coinText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, String.valueOf(goldAmount), textLength, new TextOptions(HorizontalAlign.LEFT), vbom);
            coinText.setPosition(270, 115);
            attachChild(coinText);

            mGoldHud = createSprite(220, 120, ResourceManager.getInstance().goldHud_region, vbom);
            mGoldHud.setScale(3, 3);
            attachChild(mGoldHud);

            mStore = new Sprite(MainActivity.CAMERA_WIDTH - 105, MainActivity.CAMERA_HEIGHT - 100, ResourceManager.getInstance().store_region, vbom) {

                @Override
                public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                    setChildScene(mStoreScene, false, true, true);
                    return true;
                }
            };
            mStore.setScale(3, 3);
            registerTouchArea(mStore);
            attachChild(mStore);


            mMp = createAnimatedSprite(MainActivity.CAMERA_WIDTH / 2 + 350, 40, ResourceManager.getInstance().mp_region, vbom);
            mMp.setCurrentTileIndex(3);
            mMp.setScale(3, 3);
            attachChild(mMp);

            mLives = createAnimatedSprite(120, 40, ResourceManager.getInstance().lives_region, vbom);
            mLives.setCurrentTileIndex(2);
            mLives.setScale(3, 3);
            attachChild(mLives);

            camera.setHUD(gameHUD);
        }else{
            gameHUD = new HUD();
            int textLength = 4;
            // Draw the hud
            levelText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "Level: " + level, new TextOptions(HorizontalAlign.LEFT), vbom);
            //levelText.setColor(Color.BLACK);
            levelText.setPosition(MainActivity.CAMERA_WIDTH / 2 + 220, 115);
            attachChild(levelText);

            floorText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "Floor: " + floor, new TextOptions(HorizontalAlign.LEFT), vbom);
            floorText.setPosition(20, 115);
            attachChild(floorText);

            expText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "Exp: " + exp, new TextOptions(HorizontalAlign.LEFT), vbom);
            expText.setPosition(MainActivity.CAMERA_WIDTH - 150, 115);
            attachChild(expText);

            mpText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "MP:", new TextOptions(HorizontalAlign.LEFT), vbom);
            mpText.setPosition((MainActivity.CAMERA_WIDTH / 2) + 150, 35);
            attachChild(mpText);

            coinText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, String.valueOf(goldAmount), textLength, new TextOptions(HorizontalAlign.LEFT), vbom);
            coinText.setPosition(270, 115);
            attachChild(coinText);

            mGoldHud = createSprite(220, 120, ResourceManager.getInstance().goldHud_region, vbom);
            mGoldHud.setScale(3, 3);
            attachChild(mGoldHud);

            mStore = new Sprite(MainActivity.CAMERA_WIDTH - 105, MainActivity.CAMERA_HEIGHT - 100, ResourceManager.getInstance().store_region, vbom) {

                @Override
                public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                    setChildScene(mStoreScene, false, true, true);
                    return true;
                }
            };
            mStore.setScale(3, 3);
            registerTouchArea(mStore);
            attachChild(mStore);


            mMp = createAnimatedSprite(MainActivity.CAMERA_WIDTH / 2 + 350, 40, ResourceManager.getInstance().mp_region, vbom);
            mMp.setCurrentTileIndex(mp);
            mMp.setScale(3, 3);
            attachChild(mMp);

            mLives = createAnimatedSprite(120, 40, ResourceManager.getInstance().lives_region, vbom);
            mLives.setCurrentTileIndex(lives);
            mLives.setScale(3, 3);
            attachChild(mLives);

            camera.setHUD(gameHUD);
        }
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

        mCharge = createAnimatedSprite(0, -500, ResourceManager.getInstance().charge_region, vbom);
        attachChild(mCharge);
        mCharge.animate(20);
    }

    private void addFloorItems(){
        coinList = new ArrayList<>();
        enemyList = new ArrayList<>();
        gameItems = new ArrayList<>();
        for(int i=0;i<12;i++){
            if (Math.random() < 0.5) {
                //add bat
                int randX = (int) (Math.random() * 900);
                int randY = (int) (Math.random() * 1700);
                if(randX < 50){
                    randX = randX + 50;
                }
                if(randX > 850){
                    randX = randX - 50;
                }
                if(randY < 400){
                    randY = randY + 200;
                }
                if(randY > 1650){
                    randY = randY - 50;
                }
                enemyList.add(new Enemy(randX, randY, ResourceManager.getInstance().bat_region, vbom, mPhysicsWorld, this, "", 3, 3, 100));
                //Log.d("GameScene", "emeylist add = " + enemyList.size());
            }else if (Math.round(Math.random() * 10) >= 8) {
                //add platform
                int randX = (int) (Math.random() * 900);
                int randY = (int) (Math.random() * 1700);
                if(randX < 50){
                    randX = randX + 50;
                }
                if(randX > 850){
                    randX = randX - 50;
                }
                if(randY < 400){
                    randY = randY + 200;
                }
                if(randY > 1650){
                    randY = randY - 50;
                }
                GameObject platform = new GameObject(randX, randY, ResourceManager.getInstance().platform_region, vbom, mPhysicsWorld, this, "", 3, 3);
                gameItems.add(platform);
            } else if (Math.round(Math.random() * 10) >= 9) {
                //add platform with stakes
                int randX = (int) (Math.random() * 900);
                int randY = (int) (Math.random() * 1700);
                if(randX < 50){
                    randX = randX + 50;
                }
                if(randX > 850){
                    randX = randX - 50;
                }
                if(randY < 400){
                    randY = randY + 200;
                }
                if(randY > 1650){
                    randY = randY - 50;
                }
                gameItems.add(new GameObject(randX, randY, ResourceManager.getInstance().spikedPlatform_region, vbom, mPhysicsWorld, this, "", 3, 3));
            } else {
                //add gold
                int randX = (int) (Math.random() * 900);
                int randY = (int) (Math.random() * 1700);
                if(randX < 50){
                    randX = randX + 50;
                }
                if(randX > 850){
                    randX = randX - 50;
                }
                if(randY < 400){
                    randY = randY + 200;
                }
                if(randY > 1650){
                    randY = randY - 50;
                }
                coinList.add(new Coin(randX, randY, ResourceManager.getInstance().gold_region, vbom, mPhysicsWorld, this, "", 3, 3, 100));
            }
        }
    }
    private void makeNextScreen(){
        mNextScreenScene = new Scene();
        mNextScreen = new Sprite(MainActivity.CAMERA_WIDTH / 2 - 135, MainActivity.CAMERA_HEIGHT / 2 - 240, ResourceManager.getInstance().nextFloor_region, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                disposeScene();
                floor++;
                cleanScene();
                ResourceManager.getInstance().loadGameResources();
                resetScene();
                clearChildScene();
                return true;
            }
        };
        mNextScreen.setScale(4, 4);
        mNextScreenScene.registerTouchArea(mNextScreen);
        mNextScreenScene.attachChild(mNextScreen);
    }
    private void makeGameOverScene(){
        mGameOverScene = new Scene();
        mGameOverScreen = new Sprite(MainActivity.CAMERA_WIDTH / 2 - 135, MainActivity.CAMERA_HEIGHT / 2 - 240, ResourceManager.getInstance().gameOver_region, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                restartGame();
                return true;
            }
        };
        mGameOverScreen.setScale(4, 4);
        mGameOverScene.registerTouchArea(mGameOverScreen);
        mGameOverScene.attachChild(mGameOverScreen);
    }
    private void makeStoreScene(){

        mStoreScene = new Scene();
        mStoreScene.setBackground(new Background(Color.BLACK));

        Text levelText = new Text(0, 0, ResourceManager.getInstance().menuNameFont, "Store", new TextOptions(HorizontalAlign.LEFT), vbom);
        levelText.setColor(Color.WHITE);
        levelText.setPosition(MainActivity.CAMERA_WIDTH / 2 - 190, 180);
        mStoreScene.attachChild(levelText);

        mStore = new Sprite(MainActivity.CAMERA_WIDTH-85, 45, ResourceManager.getInstance().closeStore_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
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
                if (goldAmount>=50){
                    goldAmount = goldAmount - 50;
                    coinText.setText(String.valueOf(goldAmount));
                    ResourceManager.getInstance().coinSound.play();

                }
                return true;
            }
        };
        mStore.setScale(3, 3);
        mStoreScene.registerTouchArea(mStore);
        mStoreScene.attachChild(mStore);

        mStore = new Sprite(MainActivity.CAMERA_WIDTH/2 + 300, MainActivity.CAMERA_HEIGHT/3 +150, ResourceManager.getInstance().defenseIncrease_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (goldAmount>=50){
                    goldAmount = goldAmount - 50;
                    coinText.setText(String.valueOf(goldAmount));
                    ResourceManager.getInstance().coinSound.play();

                }
                return true;
            }
        };
        mStore.setScale(3, 3);
        mStoreScene.registerTouchArea(mStore);
        mStoreScene.attachChild(mStore);

        mStore = new Sprite(220, MainActivity.CAMERA_HEIGHT/2 +350, ResourceManager.getInstance().addLife_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (goldAmount>=100){
                    lives += 1;
                    goldAmount = goldAmount - 50;
                    coinText.setText(String.valueOf(goldAmount));
                    ResourceManager.getInstance().coinSound.play();

                }
                return true;
            }
        };
        mStore.setScale(3, 3);
        mStoreScene.registerTouchArea(mStore);
        mStoreScene.attachChild(mStore);

        mStore = new Sprite(MainActivity.CAMERA_WIDTH/2 + 300, MainActivity.CAMERA_HEIGHT/2 +350, ResourceManager.getInstance().addMp_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (goldAmount>=50){
                    mp +=1;
                    goldAmount = goldAmount - 50;
                    coinText.setText(String.valueOf(goldAmount));
                    ResourceManager.getInstance().coinSound.play();

                }
                return true;
            }
        };
        mStore.setScale(3, 3);
        mStoreScene.registerTouchArea(mStore);
        mStoreScene.attachChild(mStore);
        //mStoreScene.setBackgroundEnabled(false);


    }

    private void destroyGameItems(final GameObject go)
    {

       activity.runOnUpdateThread(new Runnable() {

           @Override
           public void run() {
               final Body body = go.body;
               mPhysicsWorld.unregisterPhysicsConnector(mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(go));
               mPhysicsWorld.destroyBody(body);
               detachChild(go);
               gameItems.remove(go);
               //Log.d("GameScene", "Removed Game Item = " + gameItems.size());
           }
       });
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
                if (playerDrop) {
                    ResourceManager.getInstance().attackSound.play();
                }
                //Log.d("GameScene", "Removed enemy = " + enemyList.size());
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
                coinList.remove(coin);
                if (playerDrop) {
                    ResourceManager.getInstance().coinSound.play();
                }

                //Log.d("GameScene", "Removed coin = " + coinList.size());
            }
        });
    }
}


