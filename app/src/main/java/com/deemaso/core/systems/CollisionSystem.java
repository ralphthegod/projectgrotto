package com.deemaso.core.systems;

import com.deemaso.core.GameWorld;
import com.deemaso.core.collisions.Collision;
import com.deemaso.core.collisions.CollisionPool;
import com.deemaso.core.components.Component;

import java.util.Collection;
import java.util.List;

public abstract class CollisionSystem extends System{

    protected final CollisionPool collisionPool;

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

    protected abstract Collection<Collision> getCollisions();

    protected abstract void handleCollision(Collision collision);

}
