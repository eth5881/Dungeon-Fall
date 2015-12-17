package com.ericandshawn.dungeonfall;

import android.util.Log;
import android.view.MotionEvent;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

/**
 * Created by Shawn on 11/17/2015.
 */
public class MainMenuScene extends BaseScene implements MenuScene.IOnMenuItemClickListener {

    private MenuScene menu;
    private  final int MENU_PLAY = 0;
    private  final int MENU_ABOUT = 1;

    private Sprite menuBg;
    private Sprite mAboutBg;
    private AnimatedSprite characterSelection;
    private int numChosen;
    private Scene mAboutScene;
    private Sprite closeAboutButton;

    //Swipe
    float x1,x2;


    //MainMenu Scene
    @Override
    public void createScene() {
        createBackground();
        createMenu();
        createCharacterSelection();
    }

    private void createBackground() {
        menuBg = new Sprite(MainActivity.CAMERA_WIDTH/2-135, MainActivity.CAMERA_HEIGHT/2-240, ResourceManager.getInstance().menu_background_region, vbom);
        menuBg.setScale(4, 4);
        attachChild(menuBg);

        Text nameText = new Text(0, 0, ResourceManager.getInstance().menuNameFont, "Dungeon Drop", new TextOptions(HorizontalAlign.LEFT), vbom);
        nameText.setColor(Color.WHITE);
        nameText.setPosition((MainActivity.CAMERA_WIDTH - nameText.getWidth()) / 2f, 220);
        nameText.setScale(2.2f);
        attachChild(nameText);

        Text characterHelp = new Text(0, 0, ResourceManager.getInstance().menuNameFont, "Swipe to select character", new TextOptions(HorizontalAlign.LEFT), vbom);
        characterHelp.setPosition(120, MainActivity.CAMERA_HEIGHT / 2 + 400);
        attachChild(characterHelp);
    }

    private void createMenu(){
        menu = new MenuScene(camera);
        menu.setPosition(0,0);

        final IMenuItem playItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY,ResourceManager.getInstance().play_button_region,vbom),3.2f,1);
        final IMenuItem aboutItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_ABOUT,ResourceManager.getInstance().about_button_region,vbom),3.2f,1);

        menu.addMenuItem(playItem);
        menu.addMenuItem(aboutItem);
        menu.buildAnimations();
        menu.setBackgroundEnabled(false);

        //Set position on screen and scale them
        playItem.setPosition(playItem.getX(), playItem.getY() + 615);
        aboutItem.setPosition(aboutItem.getX(), aboutItem.getY() + 750);
        playItem.setScale(3.5f, 3.5f);
        aboutItem.setScale(3.5f, 3.5f);

        menu.setOnMenuItemClickListener(this);
        setChildScene(menu);
    }
    private void createCharacterSelection() {

        numChosen=0;
        characterSelection = new AnimatedSprite(MainActivity.CAMERA_WIDTH/2 - 150, MainActivity.CAMERA_HEIGHT/3, ResourceManager.getInstance().playerSelection_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent touchevent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

                switch (touchevent.getAction())
                {
                    // when user first touches the screen we get x and y coordinate
                    case MotionEvent.ACTION_DOWN:
                    {
                        x1 = touchevent.getX();

                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        x2 = touchevent.getX();

                        //if left to right sweep event on screen
                        if (x1 > x2) {
                            numChosen -= 1;
                            characterSelection.setCurrentTileIndex(numChosen);
                            if (characterSelection.getCurrentTileIndex() < 0) {
                                characterSelection.setCurrentTileIndex(2);
                                numChosen = 2;
                            }
                        }

                        // if right to left sweep event on screen
                        if (x1 < x2) {
                            numChosen += 1;
                            characterSelection.setCurrentTileIndex(numChosen);

                            if (characterSelection.getCurrentTileIndex() > 2) {
                                characterSelection.setCurrentTileIndex(0);
                                numChosen = 0;
                            }
                        }
                    }
                     break;
                }
                return false;
            }
        };
        characterSelection.setScale(2.6f,2.6f);
        registerTouchArea(characterSelection);
        characterSelection.setCurrentTileIndex(0);
        attachChild(characterSelection);
    }

    //MainMenu About Scene
    private void createAboutScene(){
        mAboutScene = new Scene();
        mAboutBg = new Sprite(MainActivity.CAMERA_WIDTH/2-135, MainActivity.CAMERA_HEIGHT/2-240, ResourceManager.getInstance().about_background_region, vbom);
        mAboutBg.setScale(4, 4);
        mAboutBg.setAlpha(.8f);
        mAboutScene.attachChild(mAboutBg);

        //Header Text
        Text aboutText = new Text(0, 0, ResourceManager.getInstance().menuNameFont, "About", new TextOptions(HorizontalAlign.LEFT), vbom);
        aboutText.setPosition((MainActivity.CAMERA_WIDTH - aboutText.getWidth()) / 2f, 120);
        aboutText.setScale(2f);
        mAboutScene.attachChild(aboutText);

        //description text about the game and how to play
        Text descText = new Text(0, 0, ResourceManager.getInstance().menuNameFont,
                "Tap the screen to start the \nlevel and tilt the device to \nmove your character side to \nside while they're falling." +
                "\nCollect coins to use at the \nstore and tap and hold to \ndestroy enemies. The longer \nthe charge the more damage" +
                "\nyou will get and the faster \nyou will level up. Select \nyour character and try to \nsee how far you can make it \nthrough the Dungeon.",
                new TextOptions(HorizontalAlign.LEFT), vbom);
        descText.setPosition(90, 480);
        descText.setLeading(35);
        descText.setScale(1.1f);
        mAboutScene.attachChild(descText);

        //Game creators Text
        Text byText = new Text(0, 0, ResourceManager.getInstance().menuNameFont, "By: Shawn Kilcoyne & Eric Hunt", new TextOptions(HorizontalAlign.LEFT), vbom);
        byText.setColor(Color.WHITE);
        byText.setPosition(80, MainActivity.CAMERA_HEIGHT - 100);
        mAboutScene.attachChild(byText);

        //Close Button to get back to MainMenu Scene
        closeAboutButton = new Sprite(MainActivity.CAMERA_WIDTH - 100, 55, ResourceManager.getInstance().closeAbout_region, vbom) {

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    clearChildScene();
                    createMenu();
                }
                return true;
            }
        };
        closeAboutButton.setScale(3, 3);
        mAboutScene.registerTouchArea(closeAboutButton);
        mAboutScene.attachChild(closeAboutButton);
    }

    @Override
    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pLocal, float pLocalY) {
        switch (pMenuItem.getID()){
            case MENU_PLAY:
                SceneManager.getInstance().setGameScene();
                ResourceManager.getInstance().bgMusic.play();
                ResourceManager.getInstance().setPlayerChosen(characterSelection.getCurrentTileIndex());
                return true;
            case MENU_ABOUT:
                createAboutScene();
                setChildScene(mAboutScene, false, true, true);
                return true;
        }
        return false;
    }

    @Override
    public void onBackKeyPressed() {
        System.exit(0);
    }

    @Override
    public void disposeScene() {
        ResourceManager.getInstance().unloadMenuResources();

    }
}