package com.deemaso.grotto.systems;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.components.RenderComponent;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.core.systems.System;
import com.deemaso.grotto.components.GrottoRenderComponent;
import com.deemaso.grotto.components.PhysicsComponent;
import com.deemaso.grotto.events.PhysicsWorldEvent;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
    * PhysicsSystem is a system that handles the physics of the game.
    * It uses the (J)Box2D physics engine to simulate physics.
 */
public class PhysicsSystem extends System{


    World physicsWorld;

    private float gravityX = 0;
    private float gravityY = 0;

    private boolean isGravityChanged = false;

    private final int velocityIterations;
    private final int positionIterations;
    private final int particleIterations;

    public PhysicsSystem(
            GameWorld gameWorld,
            World physicsWorld,
            int velocityIterations,
            int positionIterations,
            int particleIterations
    ) {
        super(gameWorld, Arrays.asList(PhysicsComponent.class), true);
        this.velocityIterations = velocityIterations;
        this.positionIterations = positionIterations;
        this.particleIterations = particleIterations;
        this.physicsWorld = physicsWorld;
    }

    @Override
    public void update(float dt) {

        super.update(dt);

        if(isGravityChanged) {
            physicsWorld.setGravity(new Vec2(gravityX, gravityY));
            isGravityChanged = false;
        }

        physicsWorld.step(dt, velocityIterations, positionIterations);

        // Emit a PhysicsWorldEvent for other systems to listen to
        SystemEvent event = new SystemEvent("PHYSICS_WORLD");
        event.put("physicsWorld", physicsWorld);
        gameWorld.broadcastEvent(event);
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
            bodyDef.position.set(physics.getX(), physics.getY());
            bodyDef.type = physics.getBodyType() == BodyType.STATIC ? BodyType.STATIC : BodyType.DYNAMIC;
            bodyDef.gravityScale = physics.getGravityScale();
            physics.setBody(physicsWorld.createBody(bodyDef));
            Body body = physics.getBody();
            body.setSleepingAllowed(false);
            body.setUserData(entity);
            PolygonShape box = new PolygonShape();

            RenderComponent render = entity.getComponent(GrottoRenderComponent.class);
            if(render != null) {
                box.setAsBox(physics.getShapeWidth(), physics.getShapeHeight());
            } else {
                box.setAsBox(1, 1);
            }
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = box;
            fixtureDef.density = physics.getDensity();
            fixtureDef.friction = physics.getFriction();
            fixtureDef.restitution = 0.3f;
            fixtureDef.isSensor = physics.isSensor();
            body.createFixture(fixtureDef);
        }
        return hasBeenAdded;
    }

    @Override
    public void unregisterEntity(Entity entity) {
        super.unregisterEntity(entity);
        PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
        physicsWorld.destroyBody(physics.getBody());
    }

    public void updateGravity(float x, float y) {
        gravityX = x;
        gravityY = y;
        isGravityChanged = true;
    }

    @Override
    protected void finalize() {
        physicsWorld.clearForces();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void onEvent(SystemEvent event) {

    }
}
