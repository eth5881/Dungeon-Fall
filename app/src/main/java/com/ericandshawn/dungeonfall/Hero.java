package com.ericandshawn.dungeonfall;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Eric on 12/1/2015.
 */
public class Hero extends AnimatedSprite {

    final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.7f, 0.3f);

    Body body;

    public Hero(float pX, float pY, ITiledTextureRegion pTextureRegion, VertexBufferObjectManager vbom, PhysicsWorld pWorld, Scene scene, String userData, int scaleX, int scaleY)
    {
        super(pX, pY, pTextureRegion, vbom);
        this.setScale(scaleX, scaleY);
        body = PhysicsFactory.createBoxBody(pWorld, this, BodyDef.BodyType.DynamicBody, objectFixtureDef);
        body.setUserData(userData);
        this.setCurrentTileIndex(0);
        scene.attachChild(this);
        pWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, true));
    }
}
