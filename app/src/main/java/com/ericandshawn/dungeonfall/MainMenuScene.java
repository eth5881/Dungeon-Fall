package com.ericandshawn.dungeonfall;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
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
    private AnimatedSprite chracterSelection;
    private int numChosen;


    //Swipe
    float x1,x2;

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
        characterHelp.setPosition(120, MainActivity.CAMERA_HEIGHT/2 + 400);

        //characterHelp.setColor(Color.WHITE);
        attachChild(characterHelp);



    }

    @Override
    public void onBackKeyPressed() {
        System.exit(0);
    }

    @Override
    public void disposeScene() {
        ResourceManager.getInstance().unloadMenuResources();

    }

    private void createMenu(){
        menu = new MenuScene(camera);
        menu.setPosition(0,0);

        final IMenuItem playItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY,ResourceManager.getInstance().play_button_region,vbom),3.2f,1);
        final IMenuItem aboutItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_ABOUT,ResourceManager.getInstance().about_button_region,vbom),5.2f,1);

        menu.addMenuItem(playItem);
        menu.addMenuItem(aboutItem);

        menu.buildAnimations();
        menu.setBackgroundEnabled(false);
        playItem.setPosition(playItem.getX(), playItem.getY() + 620);

        aboutItem.setPosition(aboutItem.getX(), aboutItem.getY() + 745);

        playItem.setScale(3,3);
        aboutItem.setScale(3, 3);

        menu.setOnMenuItemClickListener(this);
        setChildScene(menu);
    }
    private void createCharacterSelection() {

        numChosen=0;
        chracterSelection = new AnimatedSprite(MainActivity.CAMERA_WIDTH/2-50, MainActivity.CAMERA_HEIGHT/3 + 150, ResourceManager.getInstance().playerSelection_region, vbom) {

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
                        //DOING IT TWICE (OPPOSITE)
                        if (x1 < x2) {
                            numChosen += 1;
                            chracterSelection.setCurrentTileIndex(numChosen);

                            if (chracterSelection.getCurrentTileIndex() > 2) {
                                chracterSelection.setCurrentTileIndex(0);
                                numChosen = 0;
                            }
                        }

                        // if right to left sweep event on screen
                        if (x1 > x2) {
                            numChosen -= 1;
                            chracterSelection.setCurrentTileIndex(numChosen);
                            if (chracterSelection.getCurrentTileIndex() < 0) {
                                chracterSelection.setCurrentTileIndex(2);
                                numChosen = 2;
                            }
                        }
                    }
                     break;
                }
                return false;
            }
        };
        chracterSelection.setScale(8,8);
        registerTouchArea(chracterSelection);
        chracterSelection.setCurrentTileIndex(0);
        attachChild(chracterSelection);
    }

    @Override
    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pLocal, float pLocalY) {
        switch (pMenuItem.getID()){
            case MENU_PLAY:
                SceneManager.getInstance().setGameScene();
                ResourceManager.getInstance().bgMusic.play();
                ResourceManager.getInstance().setPlayerChosen(chracterSelection.getCurrentTileIndex());
                //Log.d("MainMenuScene", "player play = " + ResourceManager.getInstance().getPlayerChosen());
                //ResourceManager.getInstance().getPlayerChosen();
                return true;
            case MENU_ABOUT:
                System.exit(0);
                return true;
        }
        return false;
    }
}


