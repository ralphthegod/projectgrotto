package com.deemaso.grotto.systems;

import android.util.Log;

import com.deemaso.core.GameWorld;
import com.deemaso.core.collisions.Collision;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.core.systems.CollisionSystem;
import com.deemaso.grotto.components.PhysicsComponent;
import com.deemaso.grotto.listeners.CollisionListener;

import org.jbox2d.callbacks.ContactListener;

import java.util.Arrays;
import java.util.Collection;

/**
 * A collision system for Project Grotto.
 */
public class GrottoCollisionSystem extends CollisionSystem {

    private final ContactListener contactListener;

    /**
     * Creates a new collision system.
     * @param gameWorld The game world
     */
    public GrottoCollisionSystem(GameWorld gameWorld) {
        super(gameWorld, Arrays.asList(PhysicsComponent.class));
        contactListener = new CollisionListener(collisionPool);
    }

    /**
     * Creates a new collision system.
     * @param gameWorld The game world
     * @param contactListener The contact listener
     */
    public GrottoCollisionSystem(GameWorld gameWorld, ContactListener contactListener) {
        super(gameWorld, Arrays.asList(PhysicsComponent.class));
        this.contactListener = contactListener;
    }

    @Override
    protected Collection<Collision> getCollisions() {
        return ((CollisionListener) contactListener).getCollisions();
    }

    @Override
    protected void handleCollision(Collision collision) {
        SystemEvent event = new SystemEvent("COLLISION");
        event.put("collision", collision);
        gameWorld.broadcastEvent(event);

    }

    /**
     * Gets the contact listener.
     * @return The contact listener
     */
    public ContactListener getContactListener() {
        return contactListener;
    }

    @Override
    protected void finalize() {

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
