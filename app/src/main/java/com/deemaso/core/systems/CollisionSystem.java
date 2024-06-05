package com.deemaso.core.systems;

import com.deemaso.core.GameWorld;
import com.deemaso.core.collisions.Collision;
import com.deemaso.core.collisions.CollisionPool;
import com.deemaso.core.components.Component;

import java.util.Collection;
import java.util.List;

/**
 * Represents a system that handles collisions.
 */
public abstract class CollisionSystem extends System{

    protected final CollisionPool collisionPool;

    /**
     * Creates a new collision system with the given required components.
     * @param gameWorld The game world to use
     * @param requiredComponents The required components
     */
    protected CollisionSystem(GameWorld gameWorld, List<Class<? extends Component>> requiredComponents) {
        super(gameWorld, requiredComponents, true);
        collisionPool = new CollisionPool();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        Collection<Collision> collisions = getCollisions();
        for(Collision collision : collisions) {
            handleCollision(collision);
            collisionPool.release(collision);
        }
    }

    /**
     * Gets the collisions for the system.
     * @return The collisions
     */
    protected abstract Collection<Collision> getCollisions();

    /**
     * Handles a collision.
     * @param collision The collision to handle
     */
    protected abstract void handleCollision(Collision collision);

}
