package com.ericandshawn.dungeonfall;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

/**
 * Created by Shawn on 11/17/2015.
 */
public class MainMenuScene extends BaseScene implements MenuScene.IOnMenuItemClickListener{

    private MenuScene menu;
    private  final int MENU_PLAY = 0;
    private  final int MENU_ABOUT = 1;

    private Sprite menuBg;
    @Override
    public void createScene() {
        createBackground();
        createMenu();
    }

    private void createBackground() {
        menuBg = new Sprite(MainActivity.CAMERA_WIDTH/2-135, MainActivity.CAMERA_HEIGHT/2-240, ResourceManager.getInstance().menu_background_region, vbom);
        menuBg.setScale(4, 4);
        attachChild(menuBg);

        Text nameText = new Text(0, 0, ResourceManager.getInstance().menuNameFont, "Dungeon Drop", new TextOptions(HorizontalAlign.LEFT), vbom);
        nameText.setColor(Color.WHITE);
        nameText.setPosition((MainActivity.CAMERA_WIDTH - nameText.getWidth())/2f, 120);
        attachChild(nameText);
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
        //menu.setBackground(new Background(Color.GREEN));
        menu.setBackgroundEnabled(false);
        playItem.setPosition(playItem.getX(), playItem.getY() + 20);

        aboutItem.setPosition(aboutItem.getX(), aboutItem.getY() + 145);

        playItem.setScale(3,3);
        aboutItem.setScale(3, 3);

        menu.setOnMenuItemClickListener(this);
        setChildScene(menu);
    }
    @Override
    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pLocal, float pLocalY) {
        switch (pMenuItem.getID()){
            case MENU_PLAY:
                SceneManager.getInstance().setGameScene();
                return true;
            case MENU_ABOUT:
                System.exit(0);
                return true;
        }
        return false;
    }
}
