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

        gameScene = new GameScene(ResourceManager.getInstance().activity);
        //adding Accelerometer through MainActivity
        //ResourceManager.getInstance().activity.enableAccelerometer();
        //gameScene.registerUpdateHandler(ResourceManager.getInstance().activity.mPhysicsWorld);
        setScene(gameScene);
    }

    /*public void setScene(SceneType mSceneType)
    {
        switch (mSceneType)
        {
            case SCENE_MENU:
                setScene(menuScene);
                break;
            case SCENE_GAME:
                setScene(gameScene);
                break;
            default:
                break;
        }
    }*/

    //---------------------------------------------
    // GETTERS AND SETTERS
    //---------------------------------------------

    public static SceneManager getInstance()
    {
        return INSTANCE;
    }

    /*public SceneType getCurrentSceneType()
    {
        return currentSceneType;
    }*/

    public BaseScene getCurrentScene()
    {
        return currentScene;
    }
}
