package com.ericandshawn.dungeonfall;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.background.Background;
import org.andengine.ui.IGameInterface;
import org.andengine.util.color.Color;

/**
 * Created by Shawn on 11/17/2015.
 */
public class SceneManager {
    //---------------------------------------------
    // SCENES
    //---------------------------------------------

    private BaseScene menuScene;
    private BaseScene gameScene;

    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------

    private static final SceneManager INSTANCE = new SceneManager();
    private BaseScene currentScene;
    private Engine engine = ResourceManager.getInstance().engine;

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

    }
    //When click on home button on GameOver Scene
    public void resetGame(){
        ResourceManager.getInstance().loadMenuResources();
        menuScene = new MainMenuScene();
        setScene(menuScene);
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
