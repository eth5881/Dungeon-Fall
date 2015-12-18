package com.ericandshawn.dungeonfall;

import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;

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
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObject;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.IGameInterface;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

/**
 * Created by Shawn on 11/18/2015.
 */
public class GameScene extends BaseScene implements IOnSceneTouchListener, IAccelerationListener {

    private PhysicsWorld mPhysicsWorld;

    //Scenes
    private Scene mStoreScene;
    private Scene mNextScreenScene;
    private Scene mGameOverScene;
    private HUD gameHUD;

    //Array Lists
    private ArrayList <Coin> coinList;
    private ArrayList <Enemy> enemyList;
    private ArrayList <GameObject> platformList;
    private ArrayList <GameObject> spikedPlatformList;
    private ArrayList <Sprite> levelItems;

    //HUD Sprites
    private AnimatedSprite mStore;
    private Sprite mGoldHud;
    private AnimatedSprite mMp;
    private AnimatedSprite mLives;

    //Store Buttons
    private Sprite closeStoreButton;
    private AnimatedSprite mLivesButton;
    private AnimatedSprite mAttackButton;
    private AnimatedSprite mDefenseButton;
    private AnimatedSprite mMpButton;

    //Game Sprites
    private AnimatedSprite mCharge;
    private AnimatedSprite mRecharge;
    private AnimatedSprite mBlood;
    private Hero player;
    private Sprite mBg;

    //NextScene Sprites
    private Sprite mNextScreen;

    //GameOver Scene Sprites
    private Sprite mGameOverScreen;
    private Sprite mReplayButton;
    private Sprite mHomeButton;

    //Text
    Text coinText;
    Text levelText;
    Text floorText;
    Text expText;
    Text mpText;
    Text warningText;

    //Game Values
    private int floor = 1;
    private int goldAmount = 0;
    private long exp = 0;
    private long level = 1;
    private int lives = 2;

    //HUD Values
    private int maxLives = 4;
    private int mp = 1;
    private int defense = 1;
    private int attack = 1;
    private int selectedPlayer;

    private boolean playerDrop = false;
    private boolean isAttacking = false;
    private boolean attackDisabled = false;
    private boolean isDead = false;


    // ===========================================================
    // Methods
    // ===========================================================

    @Override
    public void createScene() {
        createPhysics();
        setOnSceneTouchListener(this);
        resetScene();
    }
    //Called on first load and when "replay" button is pressed on GameOver Scene
    public void resetScene() {
        setBackground(MainActivity.CAMERA_WIDTH / 2 - 135, MainActivity.CAMERA_HEIGHT / 2 - 240);
        playerDrop = false;
        createHUD();
        addFloorItems();
        setOverlap();
        makeNextScreen();
        makeGameOverScene();
        attackDisabled = false;

    }
    private void createPhysics() {
        registerUpdateHandler(new FPSLogger());
        mPhysicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, SensorManager.GRAVITY_EARTH), false);
        mPhysicsWorld.setContactListener(createContactListener());

        //making sure hero doesn't go off screen
        final Rectangle left = new Rectangle(0, -200, 0, MainActivity.CAMERA_HEIGHT + 200, vbom);
        final Rectangle right = new Rectangle(MainActivity.CAMERA_WIDTH, -200, 2, MainActivity.CAMERA_HEIGHT + 200, vbom);

        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
        PhysicsFactory.createBoxBody(mPhysicsWorld, left, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(mPhysicsWorld, right, BodyDef.BodyType.StaticBody, wallFixtureDef);

        attachChild(left);
        attachChild(right);

        registerUpdateHandler(mPhysicsWorld);
        registerUpdateHandler(new IUpdateHandler() {
            public void reset() {}

            public void onUpdate(float pSecondsElapsed) {
                //Game loop
                if (playerDrop) {
                    //if hero leaves the screen, detach sprite from scene and destroy body
                    if (player.getY() > MainActivity.CAMERA_HEIGHT + 150) {
                        mPhysicsWorld.destroyBody(player.body);
                        player.detachSelf();
                        player.dispose();
                        playerDrop = false;
                        setChildScene(mNextScreenScene, false, true, true);
                        activity.getEngine().disableAccelerationSensor(activity);
                    }
                    if (isAttacking && !attackDisabled) {
                        mCharge.setVisible(true);
                        //Attach Charge to player
                        mCharge.setX((player.getX()) - (player.getWidth() + 5));
                        mCharge.setY((player.getY()) - (player.getHeight() - 45));
                    } else if (attackDisabled) {
                        mCharge.setVisible(false);
                        mRecharge.setVisible(true);
                        //Attach Recharge to Player
                        mRecharge.setX((player.getX()) - (player.getWidth() + 5));
                        mRecharge.setY((player.getY()) - (player.getHeight() - 45));
                    } else {

                        mCharge.setVisible(false);
                        mRecharge.setVisible(false);
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
                    //set user data for coins
                    for (int x = 0; x < spikedPlatformList.size(); x++) {
                        String spikedString = "spiked" + x;
                        spikedPlatformList.get(x).body.setUserData(spikedString);
                    }
                }
                //Game Over - no more lives
                if (isDead) {
                    mPhysicsWorld.destroyBody(player.body);
                    player.detachSelf();
                    player.dispose();
                    playerDrop = false;
                    if (selectedPlayer == 0) {
                        ResourceManager.getInstance().wDieSound.play();
                    }
                    if (selectedPlayer == 1) {
                        ResourceManager.getInstance().aDieSound.play();
                    }
                    if (selectedPlayer == 2) {
                        ResourceManager.getInstance().mDieSound.play();
                    }

                    //Set Scene to GameOver Scene
                    setChildScene(mGameOverScene, false, true, true);

                    //stop background music and clear it from scene
                    ResourceManager.getInstance().bgMusic.stop();
                    ResourceManager.getInstance().bgMusic.release();
                    ResourceManager.getInstance().bgMusic = null;
                    SceneManager.getInstance().bgMusicPlaying = false;
                }
            }
        });
    }

    //Creates player to fall and starts level. Creates charges on player
    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
        if (mPhysicsWorld != null) {
            //Create player to fall from top of screen wherever use touched on screen
            if (pSceneTouchEvent.isActionDown() && !playerDrop && !isDead) {
                addPlayer(pSceneTouchEvent.getX(), - 150);
                activity.getEngine().enableAccelerationSensor(activity, this);
                return true;

            }
            if (pSceneTouchEvent.isActionDown() && !attackDisabled) {
                //Run Charge Animation
                mCharge.animate(20, 0, new AnimatedSprite.IAnimationListener() {
                    @Override
                    public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
                        attackDisabled = true;
                        isAttacking = false;
                        mCharge.stopAnimation();
                        playDisableAttack();
                    }

                    @Override
                    public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
                        isAttacking = true;
                    }

                    @Override
                    public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {
                        // TODO Auto-generated method stub

                    }
                });
            }
            if (pSceneTouchEvent.isActionUp() && playerDrop) {
                isAttacking = false;
                mCharge.stopAnimation();
            }
        }
        return false;
    }
    //Recharge Sprite Animation
    private void playDisableAttack() {
        mRecharge.animate(40, 0, new AnimatedSprite.IAnimationListener() {
            @Override
            public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
                attackDisabled = false;
                mRecharge.stopAnimation();
            }

            @Override
            public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {
            }
        });
    }

    private void setBackground(final float pX, final float pY) {
        mBg = createSprite(pX, pY, ResourceManager.getInstance().game_background_region, vbom);
        mBg.setScale(4, 4);
        attachChild(mBg);
    }
    private void createHUD() {
        //For first level set all values
        if (floor < 1) {
            floor = 1;
            goldAmount = 0;
            exp = 1;
            level = 1;
            lives = 2;
            mp = 1;
            defense = 1;
            attack = 1;
        }

        gameHUD = new HUD();
        int textLength = 4;

        // Draw the hud
        levelText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "Level: " + level, new TextOptions(HorizontalAlign.LEFT), vbom);
        //levelText.setColor(Color.BLACK);
        levelText.setPosition(MainActivity.CAMERA_WIDTH / 2 + 165, 115);
        attachChild(levelText);

        floorText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "Floor: " + floor, new TextOptions(HorizontalAlign.LEFT), vbom);
        floorText.setPosition(20, 115);
        attachChild(floorText);

        expText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "Exp: " + exp, new TextOptions(HorizontalAlign.LEFT), vbom);
        expText.setPosition(MainActivity.CAMERA_WIDTH - 190, 115);
        attachChild(expText);

        mpText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "MP:", new TextOptions(HorizontalAlign.LEFT), vbom);
        mpText.setPosition((MainActivity.CAMERA_WIDTH / 2) + 145, 40);
        attachChild(mpText);

        coinText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, String.valueOf(goldAmount), textLength, new TextOptions(HorizontalAlign.LEFT), vbom);
        coinText.setPosition(340, 115);
        attachChild(coinText);

        mGoldHud = createSprite(290, 120, ResourceManager.getInstance().goldHud_region, vbom);
        mGoldHud.setScale(3, 3);
        attachChild(mGoldHud);

        //Animated Sprites
        mMp = createAnimatedSprite(MainActivity.CAMERA_WIDTH / 2 + 350, 40, ResourceManager.getInstance().mp_region, vbom);
        mMp.setCurrentTileIndex(mp);
        mMp.setScale(3, 3);
        attachChild(mMp);

        mLives = createAnimatedSprite(120, 40, ResourceManager.getInstance().lives_region, vbom);
        mLives.setCurrentTileIndex(lives);
        mLives.setScale(3, 3);
        attachChild(mLives);


        //Create Store Button for StoreScene
        mStore = new AnimatedSprite(MainActivity.CAMERA_WIDTH - 105, MainActivity.CAMERA_HEIGHT - 100, ResourceManager.getInstance().store_region, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                makeStoreScene();
                setChildScene(mStoreScene, false, true, true);
                return true;
            }
        };
        mStore.setScale(3, 3);

        // If you have less than 50 coins, display 50% opacity sprite
        if (goldAmount < 50) {
            mStore.setCurrentTileIndex(0);
        }
        //If you have enough coins to use Store(50 or more), display 100% opacity sprite
        else {
            mStore.setCurrentTileIndex(1);
        }
        registerTouchArea(mStore);
        attachChild(mStore);

        //Set HUD
        camera.setHUD(gameHUD);
    }

    private void addPlayer(final float pX, final float pY) {
        playerDrop = true;

        //Get player selected from MainMenu Scene
        selectedPlayer = ResourceManager.getInstance().getPlayerChosen();

        //Change player Sprite to match Image selected
        if(selectedPlayer==0){
            player = new Hero(pX,pY,ResourceManager.getInstance().wHit_region,vbom,mPhysicsWorld,this,"player",3,3);

        }else if(selectedPlayer == 1){
            player = new Hero(pX,pY,ResourceManager.getInstance().aHit_region,vbom,mPhysicsWorld,this,"player",3,3);

        }
        else if(selectedPlayer == 2){
            player = new Hero(pX,pY,ResourceManager.getInstance().mHit_region,vbom,mPhysicsWorld,this,"player",3,3);
        }

        //Detach the store sprite while running through level
        detachChild(mStore);
        unregisterTouchArea(mStore);

        //Set the actual position of sprites in onUpdate()
        mCharge = createAnimatedSprite(-100, -500, ResourceManager.getInstance().charge_region, vbom);
        mCharge.setScale(2.2f, 2.2f);
        attachChild(mCharge);
        mCharge.animate(20);

        mRecharge = createAnimatedSprite(-100, -500, ResourceManager.getInstance().recharge_region, vbom);
        mRecharge.setScale(2.2f, 2.2f);
        attachChild(mRecharge);
        mRecharge.animate(40);
    }

    private void addFloorItems() {
        coinList = new ArrayList < > ();
        enemyList = new ArrayList < > ();
        platformList = new ArrayList < > ();
        spikedPlatformList = new ArrayList < > ();
        levelItems = new ArrayList < > ();
        int enemyNum = 0;
        int platNum = 0;
        int spNum = 0;
        int coinNum = 0;
        for (int i = 0; i < 12; i++) {
            if (Math.random() < 0.5) {
                //add bat
                int randX = (int)(Math.random() * MainActivity.CAMERA_WIDTH - 100);
                int randY = (int)(Math.random() * 1700);
                if (randX < 100) {
                    if (randX < 0) {
                        randX += 200;
                    }
                    randX += 100;
                }
                if (randY < 400) {
                    randY = randY + 200;
                }
                if (randY > 1650) {
                    randY = randY - 50;
                }
                enemyList.add(new Enemy(randX, randY, ResourceManager.getInstance().bat_region, vbom, mPhysicsWorld, this, "", 3, 3, 100));
                levelItems.add(enemyList.get(enemyNum));
                enemyNum++;
            } else if (Math.round(Math.random() * 10) >= 8) {
                //add platform
                int randX = (int)(Math.random() * MainActivity.CAMERA_WIDTH - 150);
                int randY = (int)(Math.random() * 1700);
                if (randX < 150) {
                    if (randX < 0) {
                        randX += 300;
                    }
                    randX += 150;
                }
                if (randY < 400) {
                    randY = randY + 200;
                }
                if (randY > 1650) {
                    randY = randY - 50;
                }
                platformList.add(new GameObject(randX, randY, ResourceManager.getInstance().platform_region, vbom, mPhysicsWorld, this, "", 3, 3));
                levelItems.add(platformList.get(platNum));
                platNum++;
            } else if (Math.round(Math.random() * 10) >= 9) {
                if (floor >= 5) {
                    //add platform with stakes
                    int randX = (int)(Math.random() * MainActivity.CAMERA_WIDTH - 150);
                    int randY = (int)(Math.random() * 1700);
                    if (randX < 150) {
                        if (randX < 0) {
                            randX += 300;
                        }
                        randX += 150;
                    }
                    if (randY < 400) {
                        randY = randY + 200;
                    }
                    if (randY > 1650) {
                        randY = randY - 50;
                    }
                    spikedPlatformList.add(new GameObject(randX, randY, ResourceManager.getInstance().spikedPlatform_region, vbom, mPhysicsWorld, this, "", 3, 3));
                    levelItems.add(spikedPlatformList.get(spNum));
                    spNum++;
                }
            } else {
                //add gold
                int randX = (int)(Math.random() * MainActivity.CAMERA_WIDTH - 50);
                int randY = (int)(Math.random() * 1700);
                if (randX < 50) {
                    if (randX < 0) {
                        randX += 100;
                    }
                    randX += 50;
                }
                if (randY < 400) {
                    randY = randY + 200;
                }
                if (randY > 1650) {
                    randY = randY - 50;
                }
                coinList.add(new Coin(randX, randY, ResourceManager.getInstance().gold_region, vbom, mPhysicsWorld, this, "", 3, 3, 100));
                levelItems.add(coinList.get(coinNum));
                coinNum++;
            }
        }
    }

    private boolean itemsOverlap() {
        for (int i = 0; i < levelItems.size() - 1; i++) {
            int distance = (int)(Math.abs(levelItems.get(i).getX() - levelItems.get(levelItems.size() - 1).getX())) + (int)(Math.abs(levelItems.get(i).getY() - levelItems.get(levelItems.size() - 1).getY()));
            if (distance < 500) {
                return true;
            }
        }
        return false;
    }

    private void setOverlap() {
        for (int i = 0; i < levelItems.size() - 1; i++) {
            if (itemsOverlap()) {
                levelItems.get(i).setX((int)(Math.random() * 800 + 10));
                levelItems.get(i).setY((int)(Math.random() * 1600 + 10));
            }
        }
    }

    private void makeNextScreen() {
        mNextScreenScene = new Scene();
        mNextScreenScene.setBackgroundEnabled(false);
        mNextScreen = new Sprite(MainActivity.CAMERA_WIDTH / 2 - 135, MainActivity.CAMERA_HEIGHT / 2 - 240, ResourceManager.getInstance().nextFloor_region, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()){
                    mNextScreenScene.unregisterTouchArea(mNextScreen);
                    //NextScreenScene.setBackground(new Background(Color.GREEN));
                    //unload Game Resources
                    disposeScene();
                    floor++;
                    cleanScene();
                    ResourceManager.getInstance().loadGameResources();
                    clearChildScene();
                    resetScene();
                }
                return true;
            }
        };
        mNextScreen.setScale(4, 4);
        mNextScreenScene.registerTouchArea(mNextScreen);
        mNextScreenScene.attachChild(mNextScreen);

        //On floor 5 add text letting user know spiked platforms are now added
        if (floor == 4) {
            warningText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "Warning: \nSpiked Platforms Added", new TextOptions(HorizontalAlign.CENTER), vbom);
            warningText.setPosition(MainActivity.CAMERA_WIDTH / 3 - 120, MainActivity.CAMERA_HEIGHT / 2 + 300);
            warningText.setLeading(35f);
            warningText.setScale(1.8f);
            mNextScreenScene.attachChild(warningText);
        }

        //On floor 10 add text letting user know player will fall faster
        if (floor == 9) {
            warningText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "Warning: \nplayer drops faster", new TextOptions(HorizontalAlign.CENTER), vbom);
            warningText.setPosition(MainActivity.CAMERA_WIDTH / 3 - 60, MainActivity.CAMERA_HEIGHT / 2 + 300);
            warningText.setLeading(35f);
            warningText.setScale(2f);
            mNextScreenScene.attachChild(warningText);
        }
        //On floor 20 add text letting user know player will fall even faster
        if (floor == 19) {
            warningText = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "Warning: \nplayer drops even faster", new TextOptions(HorizontalAlign.CENTER), vbom);
            warningText.setPosition(MainActivity.CAMERA_WIDTH / 3 - 130, MainActivity.CAMERA_HEIGHT / 2 + 300);
            warningText.setLeading(35f);
            warningText.setScale(1.6f);
            mNextScreenScene.attachChild(warningText);
        }
    }


    // ===========================================================
    // StoreScene
    // ===========================================================


    private void makeStoreScene() {

        mStoreScene = new Scene();
        mStoreScene.setBackgroundEnabled(false);
        //mStoreScene.setBackground(new Background(Color.BLACK));
        //mStore.setVisible(false);
        mStore.setCurrentTileIndex(0);
        closeStoreButton = new Sprite(340, MainActivity.CAMERA_HEIGHT - 300, ResourceManager.getInstance().closeStore_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

                if (pSceneTouchEvent.isActionDown()) {
                    //SceneManager.getInstance().setGameScene();
                    clearChildScene();
                    //mStore.setVisible(true);
                    if (goldAmount < 50) {
                        mStore.setCurrentTileIndex(0);
                    } else {
                        mStore.setCurrentTileIndex(1);
                    }
                }
                return true;
            }
        };
        closeStoreButton.setScale(3, 3);
        mStoreScene.registerTouchArea(closeStoreButton);
        mStoreScene.attachChild(closeStoreButton);


        // ===========================================================
        // ATTACK BUTTON
        // ===========================================================

        /*mAttackButton = new AnimatedSprite(120, MainActivity.CAMERA_HEIGHT - 150, ResourceManager.getInstance().attackIncrease_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

                if (pSceneTouchEvent.isActionDown()) {
                    //if total coins are greater than cost, purchase power-up and subtract amount from total coins
                    if (goldAmount >= 25) {
                        attack += 5;
                        goldAmount -= 25;
                        coinText.setText(String.valueOf(goldAmount));
                        ResourceManager.getInstance().coinSound.play();

                        //Reset Store buttons
                        resetStoreButtonSprites();

                    }
                }
                return true;
            }
        };
        mAttackButton.setScale(3, 3);
        if (goldAmount < 25) {
            mAttackButton.setCurrentTileIndex(0);
        } else {
            mAttackButton.setCurrentTileIndex(1);
            mStoreScene.registerTouchArea(mAttackButton);
        }
        mStoreScene.attachChild(mAttackButton);
        */


        // ===========================================================
        // DEFENSE BUTTON
        // ===========================================================
        /* mDefenseButton = new AnimatedSprite(340, MainActivity.CAMERA_HEIGHT - 150, ResourceManager.getInstance().defenseIncrease_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

                if (pSceneTouchEvent.isActionDown()) {

                    if (goldAmount >= 25) {
                        defense += 5;
                        goldAmount -= 25;
                        coinText.setText(String.valueOf(goldAmount));
                        ResourceManager.getInstance().coinSound.play();

                        //Reset Store buttons
                        resetStoreButtonSprites();

                    } else {
                        mDefenseButton.setCurrentTileIndex(0);
                    }
                }
                return true;
            }
        };
        mDefenseButton.setScale(3, 3);
        if (goldAmount < 25) {
            mDefenseButton.setCurrentTileIndex(0);
        } else {
            mDefenseButton.setCurrentTileIndex(1);
            mStoreScene.registerTouchArea(mDefenseButton);
        }
        mStoreScene.attachChild(mDefenseButton);
        */


        // ===========================================================
        // LIVES BUTTON
        // ===========================================================

        mLivesButton = new AnimatedSprite(560, MainActivity.CAMERA_HEIGHT - 150, ResourceManager.getInstance().addLife_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

                if (pSceneTouchEvent.isActionDown()) {

                    if (goldAmount >= 100) {
                        lives++;
                        goldAmount -= 100;
                        coinText.setText(String.valueOf(goldAmount));
                        ResourceManager.getInstance().coinSound.play();
                        mLives.setCurrentTileIndex(lives);
                        if (lives >= 4) {
                            mLivesButton.setCurrentTileIndex(0);
                            mStoreScene.unregisterTouchArea(mLivesButton);

                        }
                        resetStoreButtonSprites();

                    } else {
                        mLivesButton.setCurrentTileIndex(0);
                    }
                }
                return true;
            }
        };
        mLivesButton.setScale(3, 3);
        if (lives >= 4 || goldAmount < 100) {
            mLivesButton.setCurrentTileIndex(0);
        } else {
            mLivesButton.setCurrentTileIndex(1);
            mStoreScene.registerTouchArea(mLivesButton);

        }
        mStoreScene.attachChild(mLivesButton);


        // ===========================================================
        // MP BUTTON
        // ===========================================================

        mMpButton = new AnimatedSprite(780, MainActivity.CAMERA_HEIGHT - 150, ResourceManager.getInstance().addMp_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

                if (pSceneTouchEvent.isActionDown()) {

                    if (goldAmount >= 50) {
                        mp++;
                        goldAmount -= 50;
                        coinText.setText(String.valueOf(goldAmount));
                        ResourceManager.getInstance().coinSound.play();
                        mMp.setCurrentTileIndex(mp);
                        if (mp >= 3) {
                            mMpButton.setCurrentTileIndex(0);
                            mStoreScene.unregisterTouchArea(mMpButton);
                        }
                        resetStoreButtonSprites();
                    }

                }
                return true;
            }
        };
        mMpButton.setScale(3, 3);
        if (mp >= 3 || goldAmount < 50) {
            mMpButton.setCurrentTileIndex(0);
        } else {
            mMpButton.setCurrentTileIndex(1);
            mStoreScene.registerTouchArea(mMpButton);

        }
        mStoreScene.attachChild(mMpButton);
    }

    //reset Store buttons after purchase
    private void resetStoreButtonSprites() {

        if (goldAmount < 100) {
            if (goldAmount < 50) {
                /*if (goldAmount < 25) {
                    mAttackButton.setCurrentTileIndex(0);
                    mStoreScene.unregisterTouchArea(mAttackButton);
                    mDefenseButton.setCurrentTileIndex(0);

                }*/

                mMpButton.setCurrentTileIndex(0);
                mStoreScene.unregisterTouchArea(mMpButton);
                mStoreScene.unregisterTouchArea(mDefenseButton);
                mStore.setCurrentTileIndex(0);

            }

            mLivesButton.setCurrentTileIndex(0);
            mStoreScene.unregisterTouchArea(mLivesButton);
        }
    }

    // ===========================================================
    // GAMEOVER SCENE
    // ===========================================================

    private void makeGameOverScene() {
        mGameOverScene = new Scene();
        mGameOverScreen = new Sprite(MainActivity.CAMERA_WIDTH / 2 - 135, MainActivity.CAMERA_HEIGHT / 2 - 300, ResourceManager.getInstance().gameOver_region, vbom);
        mGameOverScreen.setScale(4, 4);
        mGameOverScene.attachChild(mGameOverScreen);

        mReplayButton = new Sprite(MainActivity.CAMERA_WIDTH / 2 - 50, MainActivity.CAMERA_HEIGHT / 2 + 200, ResourceManager.getInstance().replay_region, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                restartGame();
                ResourceManager.getInstance().bgMusic.play();
                SceneManager.getInstance().bgMusicPlaying = true;
                return true;
            }
        };
        mReplayButton.setScale(3.5f, 3.5f);
        mGameOverScene.registerTouchArea(mReplayButton);
        mGameOverScene.attachChild(mReplayButton);

        mHomeButton = new Sprite(MainActivity.CAMERA_WIDTH / 2 - 50, MainActivity.CAMERA_HEIGHT / 2 + 400, ResourceManager.getInstance().home_region, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                SceneManager.getInstance().resetGame();
                return true;
            }
        };
        mHomeButton.setScale(3.5f, 3.5f);
        mGameOverScene.registerTouchArea(mHomeButton);
        mGameOverScene.attachChild(mHomeButton);

        Text floor_achieved = new Text(0, 0, ResourceManager.getInstance().hudNameFont, "Floor Reached: " + floor, new TextOptions(HorizontalAlign.LEFT), vbom);
        floor_achieved.setPosition(380, MainActivity.CAMERA_HEIGHT / 2 - 100);
        floor_achieved.setScale(2.2f);
        mGameOverScene.attachChild(floor_achieved);
    }

    // ===========================================================
    // CREATE CONTACT LISTENER
    // ===========================================================


    private ContactListener createContactListener() {
        ContactListener
                contactListener = new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();
                final Body body1 = x1.getBody();
                final Body body2 = x2.getBody();

                //check if player and coin collide
                for (int i = 0; i < coinList.size(); i++) {
                    String coinData = "coin" + i;
                    if (("player".equals(body1.getUserData()) && coinData.equals(body2.getUserData())) || ("player".equals(body2.getUserData()) && coinData.equals(body1.getUserData()))) {
                        //collect coin and add it to gold amount
                        destroyCoin(coinList.get(i));
                        int randNum = Math.round((int)(Math.random() * 25));
                        if (randNum == 0) {
                            randNum = 1;
                        }
                        goldAmount = goldAmount + randNum;
                        coinText.setText(String.valueOf(goldAmount));
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();
                final Body body1 = x1.getBody();
                final Body body2 = x2.getBody();

                //check if player and enemy collide
                for (int i = 0; i < enemyList.size(); i++) {
                    String batData = "bat" + i;
                    if (("player".equals(body1.getUserData()) && batData.equals(body2.getUserData())) || ("player".equals(body2.getUserData()) && batData.equals(body1.getUserData()))) {
                        if (isAttacking) {
                            //kill enemy and add experience
                            destroyEnemy(enemyList.get(i));
                            isAttacking = false;
                            mCharge.stopAnimation();
                            //add experience and level up if necessary
                            exp += 1;
                            if (exp > level * level) {
                                level++;
                                levelText.setText("Level: " + String.valueOf(level));
                                if (lives >= maxLives) {
                                    lives = maxLives;
                                } else {
                                    lives++;
                                    mLives.setCurrentTileIndex(lives);
                                }
                                exp = 0;
                            }
                            expText.setText("Exp: " + String.valueOf(exp));
                            //blood
                            mBlood = createAnimatedSprite(enemyList.get(i).getX(), enemyList.get(i).getY(), ResourceManager.getInstance().blood_region, vbom);
                            attachChild(mBlood);
                            mBlood.setScale(3, 3);
                            mBlood.animate(75, 0, new AnimatedSprite.IAnimationListener() {

                                @Override
                                public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
                                    mBlood.setVisible(false);
                                    detachChild(mBlood);
                                    mBlood.dispose();

                                }@Override
                                 public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {
                                    // TODO Auto-generated method stub

                                }
                            });
                        } else if (!isAttacking) {
                            if (lives != 0) {
								player.animate(75, 0, new AnimatedSprite.IAnimationListener() {

                                @Override
                                public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
                                        player.setCurrentTileIndex(0);
                                        player.stopAnimation();
                                        }
                                @Override
                                public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
                                        // TODO Auto-generated method stub

                                        }

                                @Override
                                public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
                                        // TODO Auto-generated method stub

                                        }

                                @Override
                                public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {
                                        // TODO Auto-generated method stub

                                        }});

                                //play Hit sound depending on character selected from MainMenu Scene
                                if (selectedPlayer == 0) {
                                    ResourceManager.getInstance().wHitSound.play();
                                }
                                if (selectedPlayer == 1) {
                                    ResourceManager.getInstance().aHitSound.play();
                                }
                                if (selectedPlayer == 2) {
                                    ResourceManager.getInstance().mHitSound.play();
                                }

                                //loose a life and update health Sprite
                                lives--;
                                mLives.setCurrentTileIndex(lives);
                            } else {
                                isDead = true;
                            }
                        }
                    }
                }
                //check if player and spiked platform collide
                for (int i = 0; i < spikedPlatformList.size(); i++) {
                    String spikedData = "spiked" + i;
                    if (("player".equals(body1.getUserData()) && spikedData.equals(body2.getUserData())) || ("player".equals(body2.getUserData()) && spikedData.equals(body1.getUserData()))) {
                        if (player.getY() < spikedPlatformList.get(i).getY()) {
                            if (lives != 0) {
								player.animate(75, 0, new AnimatedSprite.IAnimationListener() {

                                @Override
                                public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
                                        player.setCurrentTileIndex(0);
                                        player.stopAnimation();
                                        }

                                @Override
                                public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
                                        // TODO Auto-generated method stub

                                        }

                                @Override
                                public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
                                        // TODO Auto-generated method stub

                                        }

                                @Override
                                public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {
                                        // TODO Auto-generated method stub

                                        }
                                        });
                                if (selectedPlayer == 0) {
                                    ResourceManager.getInstance().wHitSound.play();
                                }
                                if (selectedPlayer == 1) {
                                    ResourceManager.getInstance().aHitSound.play();
                                }
                                if (selectedPlayer == 2) {
                                    ResourceManager.getInstance().mHitSound.play();
                                }
                                lives--;
                                mLives.setCurrentTileIndex(lives);
                            } else {
                                isDead = true;
                            }
                        }
                    }
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        };
        return contactListener;
    }



    // ===========================================================
    // REMOVE ITEMS & RE-SETTING GAME
    // ===========================================================


    private void destroyPlatforms(final GameObject platform) {
        activity.runOnUpdateThread(new Runnable() {

            @Override
            public void run() {
                //Remove Platforms
                final Body body = platform.body;
                mPhysicsWorld.unregisterPhysicsConnector(mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(platform));
                mPhysicsWorld.destroyBody(body);
                detachChild(platform);
                platformList.remove(platform);
            }
        });
    }


    private void destroySpikedPlatforms(final GameObject sp) {
        activity.runOnUpdateThread(new Runnable() {

            @Override
            public void run() {
                //Remove Spiked Platforms
                final Body body = sp.body;
                mPhysicsWorld.unregisterPhysicsConnector(mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(sp));
                mPhysicsWorld.destroyBody(body);
                detachChild(sp);
                spikedPlatformList.remove(sp);
            }
        });
    }

    private void destroyEnemy(final Enemy enemy) {
        activity.runOnUpdateThread(new Runnable() {

            @Override
            public void run() {
                //Remove Enemies
                final Body body = enemy.body;
                mPhysicsWorld.unregisterPhysicsConnector(mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(enemy));
                mPhysicsWorld.destroyBody(body);
                detachChild(enemy);
                enemyList.remove(enemy);
                if (playerDrop) {
                    ResourceManager.getInstance().attackSound.play();
                }
            }
        });
    }
    private void destroyCoin(final Coin coin) {
        activity.runOnUpdateThread(new Runnable() {

            @Override
            public void run() {
                //Remove Coin
                final Body body = coin.body;
                mPhysicsWorld.unregisterPhysicsConnector(mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(coin));
                mPhysicsWorld.destroyBody(body);
                detachChild(coin);
                coinList.remove(coin);
                if (playerDrop) {
                    ResourceManager.getInstance().coinSound.play();
                }
            }
        });
    }
    public void cleanScene() {


        if (enemyList.size() > 0) {
            for (int i = 0; i < enemyList.size(); i++) {
                destroyEnemy(enemyList.get(i));
            }
        }
        if (coinList.size() > 0) {
            for (int i = 0; i < coinList.size(); i++) {
                destroyCoin(coinList.get(i));
            }
        }
        if (platformList.size() > 0) {
            for (int i = 0; i < platformList.size(); i++) {
                destroyPlatforms(platformList.get(i));
            }
        }
        if (spikedPlatformList.size() > 0) {
            for (int i = 0; i < spikedPlatformList.size(); i++) {
                destroySpikedPlatforms(spikedPlatformList.get(i));
            }
        }
        if (levelItems.size() > 0) {
            for (int i = 0; i < levelItems.size(); i++) {
                //(levelItems.get(i));
            }
        }


        enemyList.clear();
        coinList.clear();
        platformList.clear();
        spikedPlatformList.clear();

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
        detachChild(mCharge);
        detachChild(mRecharge);
        detachChild(mNextScreen);


        mStore.dispose();
        mGoldHud.dispose();
        mMp.dispose();
        mLives.dispose();
        coinText.dispose();
        levelText.dispose();
        floorText.dispose();
        expText.dispose();
        mpText.dispose();
        mCharge.dispose();
        mRecharge.dispose();
        mNextScreen.dispose();


    }
    private void restartGame() {
        floor = 1;
        goldAmount = 0;
        exp = 0;
        level = 1;
        lives = 2;
        mp = 1;
        defense = 1;
        attack = 1;
        disposeScene();
        cleanScene();
        ResourceManager.getInstance().loadGameResources();
        resetScene();
        clearChildScene();
        isDead = false;
        attackDisabled = false;
    }


    @Override
    public void onBackKeyPressed() {}

    @Override
    public void disposeScene() {
        ResourceManager.getInstance().unloadGameResources();
    }


    // ===========================================================
    // ENABLING ACCELERATION
    // ===========================================================
    @Override
    public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {

    }

    @Override
    public void onAccelerationChanged(AccelerationData pAccelerationData) {

        if (floor < 10) {
            final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX() * 8, SensorManager.GRAVITY_EARTH * 1.2f);
            mPhysicsWorld.setGravity(gravity);
            Vector2Pool.recycle(gravity);
        }
        //change acceleration and gravity after level 10
        else if (floor < 20) {
            mPhysicsWorld.clearForces();
            final Vector2 gravity2 = Vector2Pool.obtain(pAccelerationData.getX() * 6, SensorManager.GRAVITY_EARTH * 1.5f);
            mPhysicsWorld.setGravity(gravity2);
            Vector2Pool.recycle(gravity2);
        } else {
            mPhysicsWorld.clearForces();
            final Vector2 gravity3 = Vector2Pool.obtain(pAccelerationData.getX() * 6, SensorManager.GRAVITY_EARTH * 1.7f);
            mPhysicsWorld.setGravity(gravity3);
            Vector2Pool.recycle(gravity3);
        }
    }

}