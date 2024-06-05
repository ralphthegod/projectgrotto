package com.deemaso.core.collisions;

import com.deemaso.core.Entity;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A pool of collisions. Used to avoid creating new collision objects every frame.
 */
public class CollisionPool {
    final private Queue<Collision> pool;

    /**
     * Creates a new collision pool.
     */
    public CollisionPool() {
        pool = new LinkedList<>();
    }

    /**
     * Gets a collision from the pool.
     * @param a The first entity
     * @param b The second entity
     * @return The collision
     */
    public Collision get(Entity a, Entity b) {
        Collision collision;
        if (pool.isEmpty()) {
            collision = new Collision(a, b);
        } else {
            collision = pool.poll();
            collision.a = a;
            collision.b = b;
        }
        return collision;
    }

    /**
     * Releases a collision back into the pool.
     * @param collision The collision to release
     */
    public void release(Collision collision) {
        pool.add(collision);
    }
}
