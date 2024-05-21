package com.deemaso.core.collisions;

import com.deemaso.core.Entity;

import java.util.LinkedList;
import java.util.Queue;

public class CollisionPool {
    final private Queue<Collision> pool;

    public CollisionPool() {
        pool = new LinkedList<>();
    }

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

    public void release(Collision collision) {
        pool.add(collision);
    }
}
