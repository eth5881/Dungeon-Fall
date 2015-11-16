package com.hunt.eric.dungeonfall;

/**
 * Created by Shawn on 11/16/2015.
 */
public class ResourceManager {

    private static final ResourceManager INSTANCE = new ResourceManager();

    public GameActivity mActivity;

    private ResourceManager() {}

    public static ResourceManager getInstance() {
        return INSTANCE;
    }

    public void prepare(GameActivity activity) {
        INSTANCE.mActivity = activity;
    }

    public void loadSplashResources() {
        //TODO implement
    }

    public void unloadSplashResources() {
        //TODO implement
    }

    public void loadGameResources() {
        //TODO implement
    }

    public void unloadGameResources() {
        //TODO implement
    }
}
