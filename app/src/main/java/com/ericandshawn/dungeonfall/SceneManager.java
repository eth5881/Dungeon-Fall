package com.ericandshawn.dungeonfall;

import org.andengine.engine.Engine;
import org.andengine.ui.IGameInterface;

/**
 * Created by Shawn on 11/17/2015.
 */
public class SceneManager {
    //---------------------------------------------
    // SCENES
    //---------------------------------------------

    private BaseScene splashScene;
    private BaseScene menuScene;
    private BaseScene gameScene;
    private BaseScene mStoreScene;

    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------

    private static final SceneManager INSTANCE = new SceneManager();

    //private SceneType currentSceneType = SceneType.SCENE_MENU;

    private BaseScene currentScene;

    private Engine engine = ResourceManager.getInstance().engine;

    public enum SceneType
    {
        //SCENE_SPLASH,
        SCENE_MENU,
        SCENE_GAME,
        //SCENE_LOADING,
    }

    //---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------

    public void setScene(BaseScene scene)
    {
        if (currentScene !=null) {
            currentScene.disposeScene();
        }
        engine.setScene(scene);
        currentScene = scene;
        //currentSceneType = scene.getSceneType();
    }
   public void setMenuScene(IGameInterface.OnCreateSceneCallback cb){
       ResourceManager.getInstance().loadMenuResources();
       menuScene = new MainMenuScene();
       setScene(menuScene);
       currentScene.createScene();
       cb.onCreateSceneFinished(menuScene);
   }
    public void setGameScene(){
        ResourceManager.getInstance().loadGameResources();

        gameScene = new GameScene();
        setScene(gameScene);
    }


    //---------------------------------------------
    // GETTERS AND SETTERS
    //---------------------------------------------

    public static SceneManager getInstance()
    {
        return INSTANCE;
    }


    public BaseScene getCurrentScene()
    {
        return currentScene;
    }
}
