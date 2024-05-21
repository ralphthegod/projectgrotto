package com.deemaso.grotto.systems;

import com.deemaso.core.GameWorld;
import com.deemaso.core.collisions.Collision;
import com.deemaso.core.systems.CollisionSystem;
import com.deemaso.grotto.components.PhysicsComponent;
import com.deemaso.grotto.listeners.CollisionListener;
import com.google.fpl.liquidfun.ContactListener;

import java.util.Arrays;
import java.util.Collection;

public class GrottoCollisionSystem extends CollisionSystem {

    private final ContactListener contactListener;

    public GrottoCollisionSystem(GameWorld gameWorld) {
        super(gameWorld, Arrays.asList(PhysicsComponent.class));
        contactListener = new CollisionListener(collisionPool);
    }

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
        // TO DO: handle collision
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
}
