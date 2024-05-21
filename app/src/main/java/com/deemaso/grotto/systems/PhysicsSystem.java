package com.deemaso.grotto.systems;

import android.util.Log;

import com.deemaso.core.ComponentFactory;
import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.components.RenderComponent;
import com.deemaso.core.events.Event;
import com.deemaso.core.events.EventEmitter;
import com.deemaso.core.events.EventListener;
import com.deemaso.core.systems.System;
import com.deemaso.grotto.components.GrottoRenderComponent;
import com.deemaso.grotto.components.PhysicsComponent;
import com.deemaso.grotto.events.PhysicsWorldEvent;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.World;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
    * PhysicsSystem is a system that handles the physics of the game.
    * It uses the (J)LiquidFun physics engine to simulate physics.
 */
public class PhysicsSystem extends System implements EventEmitter {

    final private static Map<Class<? extends Event>, List<EventListener>> listeners = new HashMap<>();

    World physicsWorld;

    private float gravityX = 0;
    private float gravityY = 0;

    private boolean isGravityChanged = false;

    private final int velocityIterations;
    private final int positionIterations;
    private final int particleIterations;

    public PhysicsSystem(
            GameWorld gameWorld,
            int velocityIterations,
            int positionIterations,
            int particleIterations
    ) {
        super(gameWorld, Arrays.asList(PhysicsComponent.class), true);
        this.velocityIterations = velocityIterations;
        this.positionIterations = positionIterations;
        this.particleIterations = particleIterations;
        physicsWorld = new World(0, 0);
    }

    @Override
    public void update(float dt) {

        if(isGravityChanged) {
            physicsWorld.setGravity(gravityX, gravityY);
            isGravityChanged = false;
        }

        physicsWorld.step(dt, velocityIterations, positionIterations, particleIterations);

        // Emit a PhysicsWorldEvent for other systems to listen to
        emitEvent(new PhysicsWorldEvent(physicsWorld));
    }

    /**
     * Register an entity with the PhysicsSystem.
     * This method creates a body for the entity and adds it to the physics world.
     */
    @Override
    public boolean registerEntity(Entity entity) {
        boolean hasBeenAdded = super.registerEntity(entity);
        if(hasBeenAdded) {
            // Create a body for the entity
            PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
            BodyDef bodyDef = new BodyDef();
            bodyDef.setPosition(physics.getX(), physics.getY());
            bodyDef.setType(physics.getBodyType() == null ? BodyType.dynamicBody : physics.getBodyType());
            physics.setBody(physicsWorld.createBody(bodyDef));
            Body body = physics.getBody();
            body.setSleepingAllowed(false);
            body.setUserData(entity);
            PolygonShape box = new PolygonShape();

            RenderComponent render = entity.getComponent(GrottoRenderComponent.class);
            if(render != null) {
                Log.d("PhysicsSystem", "Registering entity with render component");
                box.setAsBox(render.getWidth()/2, render.getHeight()/2);
            } else {
                Log.d("PhysicsSystem", "Registering entity without render component");
                box.setAsBox(1, 1);
            }
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.setShape(box);
            fixtureDef.setFriction(physics.getFriction());
            fixtureDef.setRestitution(0.4f);
            fixtureDef.setDensity(physics.getDensity());
            body.createFixture(fixtureDef);

            fixtureDef.delete();
            bodyDef.delete();
            box.delete();
        }
        return hasBeenAdded;
    }

    public void updateGravity(float x, float y) {
        gravityX = x;
        gravityY = y;
        isGravityChanged = true;
    }

    @Override
    protected void finalize() {
        physicsWorld.delete();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public Map<Class<? extends Event>, List<EventListener>> getListeners() {
        return listeners;
    }

    /*
     * Register the PhysicsComponent within the ComponentFactory
     */
    static {
        ComponentFactory.registerComponent(
            "PhysicsComponent",
            element -> {
                return new PhysicsComponent();
            }
        );
    }


}
