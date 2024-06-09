package com.deemaso.grotto.systems;

import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.components.RenderComponent;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.core.systems.System;
import com.deemaso.grotto.components.GrottoRenderComponent;
import com.deemaso.grotto.components.PhysicsComponent;

import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.Arrays;

/**
    * A physics system for Project Grotto.
    * It uses the (J)Box2D physics engine to simulate physics.
 */
public class PhysicsSystem extends System{

    World physicsWorld;
    ContactListener contactListener;

    private float gravityX = 0;
    private float gravityY = 0;

    private boolean isGravityChanged = false;

    private final int velocityIterations;
    private final int positionIterations;
    private final int particleIterations;

    /**
     * Creates a new physics system.
     * @param gameWorld The game world
     * @param contactListener The contact listener
     * @param velocityIterations The velocity iterations
     * @param positionIterations The position iterations
     * @param particleIterations The particle iterations
     */
    public PhysicsSystem(
            GameWorld gameWorld,
            ContactListener contactListener,
            int velocityIterations,
            int positionIterations,
            int particleIterations
    ) {
        super(gameWorld, Arrays.asList(PhysicsComponent.class), true);
        this.velocityIterations = velocityIterations;
        this.positionIterations = positionIterations;
        this.particleIterations = particleIterations;
        this.contactListener = contactListener;
        physicsWorld = new World(new Vec2(0, 0));
        physicsWorld.setContactListener(contactListener);

    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if(isGravityChanged) {
            physicsWorld.setGravity(new Vec2(gravityX, gravityY));
            isGravityChanged = false;
        }

        checkEntitiesValidity();

        //Log.d("PhysicsSystem", "Physics update, body count: " + physicsWorld.getBodyCount() + ", joint count: " + physicsWorld.getJointCount());
        if(physicsWorld.getBodyCount() > 0) physicsWorld.step(dt, velocityIterations, positionIterations);

        // Emit a PhysicsWorldEvent for other systems to listen to
        SystemEvent event = new SystemEvent("PHYSICS_WORLD");
        event.put("physicsWorld", physicsWorld);
        gameWorld.broadcastEvent(event);
    }

    private void checkEntitiesValidity(){
        for(Entity entity : entities){
            PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
            if(physics == null && physics.getBody().getWorld() != physicsWorld){
                Log.e("PhysicsSystem", "Entity with id " + entity.getId() + " has a body that is not in the physics world.");
                gameWorld.markEntityForDeletion(entity);
            }
        }
    }

    @Override
    public boolean registerEntity(Entity entity) {
        boolean hasBeenAdded = super.registerEntity(entity);
        if(hasBeenAdded) {
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
    public void deleteMarkedEntities(){
        for(Entity entity : entitiesToDelete){
            PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
            Log.d("PhysicsSystem", "Destroying body of entity with id " + entity.getId());
            if(physics != null && physicsWorld.getBodyCount() > 0){
                physicsWorld.destroyBody(physics.getBody());
                Log.d("PhysicsSystem", "Body destroyed, current bodycount: " + physicsWorld.getBodyCount());
            }
            else{
                Log.e("PhysicsSystem", "Error in destroying body.");
            }
        }
        super.deleteMarkedEntities();
    }

    /**
     * Updates the gravity of the physics world.
     * @param x The x component of the gravity
     * @param y The y component of the gravity
     */
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
        /*if(event.getCode().equals("LEVEL_GENERATION")){
            Log.d("PhysicsSystem", "Level generation event received, creating new physics world.");
            physicsWorld = new World(new Vec2(0, 0));
            physicsWorld.setContactListener(contactListener);
            SystemEvent physicsWorldEvent = new SystemEvent("PHYSICS_WORLD");
            physicsWorldEvent.put("physicsWorld", physicsWorld);
            gameWorld.broadcastEvent(physicsWorldEvent);
        }*/
    }
}
