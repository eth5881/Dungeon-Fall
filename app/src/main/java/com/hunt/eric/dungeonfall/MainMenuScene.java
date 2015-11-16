package com.hunt.eric.dungeonfall;

import android.opengl.GLES20;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.andengine.util.adt.color.Color;

/**
 * Created by Shawn on 11/16/2015.
 */
public class MainMenuScene extends BaseScene implements MenuScene.IOnMenuItemClickListener {

    protected MenuScene mMenuScene;
    private MenuScene mSubMenuScene;
    @Override
    public void createScene() {

      mMenuScene = createMenuScene();
      mSubMenuScene = createSubMenuScene();

    }
    /*protected MenuScene createMenuScene() {
        //TODO implement
        return null;
    }*/

    protected MenuScene createSubMenuScene() {
        //TODO implement
        return null;
    }

    @Override
    public void onBackKeyPressed() {
        if (mMenuScene.hasChildScene())
            mSubMenuScene.back();
        else
            mActivity.finish();
    }

    @Override
    public SceneManager.SceneType getSceneType() {
        return SceneManager.SceneType.SCENE_MENU;
    }

    @Override
    public void disposeScene() {

    }
    protected static final int MENU_PLAY = 0;
    protected static final int MENU_RATE = 1;
    protected static final int MENU_EXTRAS = 2;
    protected static final int MENU_QUIT = 3;

    protected MenuScene createMenuScene() {
        final MenuScene menuScene = new MenuScene(mCamera);

        /*final IMenuItem playMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_PLAY, mResourceManager.mFont3, "Play", mVertexBufferObjectManager), new Color(1,1,1), new Color(0.0f, 0.2f, 0.4f));
        playMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(playMenuItem);

        final IMenuItem rateMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_RATE, mResourceManager.mFont3, "Rate", mVertexBufferObjectManager), new Color(1,1,1), new Color(0.0f, 0.2f, 0.4f));
        rateMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(rateMenuItem);

        final IMenuItem extrasMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_EXTRAS, mResourceManager.mFont3, "Extras", mVertexBufferObjectManager), new Color(1,1,1), new Color(0.0f, 0.2f, 0.4f));
        extrasMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(extrasMenuItem);

        final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_QUIT, mResourceManager.mFont3, "Quit", mVertexBufferObjectManager), new Color(1,1,1), new Color(0.0f, 0.2f, 0.4f));
        quitMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(quitMenuItem);*/

        /*final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_PLAY,null,"Play", mVertexBufferObjectManager), new Color(1,1,1), new Color(0.0f, 0.2f, 0.4f));
        quitMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(quitMenuItem);*/

        menuScene.buildAnimations();

        menuScene.setBackgroundEnabled(false);

        menuScene.setOnMenuItemClickListener(this);
        return menuScene;
    }
    @Override
    public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {

        switch(pMenuItem.getID()) {
            case MENU_PLAY:
                /*if (mResourceManager.mMusic.isPlaying()) {
                    mResourceManager.mMusic.pause();
                }*/
                mMenuScene.closeMenuScene();
                mSceneManager.setScene(SceneManager.SceneType.SCENE_GAME);
                return true;

            case MENU_RATE:
                //TODO implement
                return true;

            case MENU_EXTRAS:
                pMenuScene.setChildSceneModal(mSubMenuScene);
                return true;

            case MENU_QUIT:
            /* End Activity. */
                mActivity.finish();
                return true;

            default:
                return false;
        }
    }

}
