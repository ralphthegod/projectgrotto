package com.deemaso.core.collisions;

import com.deemaso.core.Entity;

/**
 * Represents a collision between two entities.
 */
public class Collision {
    Entity a, b;

    /**
     * Creates a new collision between two entities.
     * @param a The first entity
     * @param b The second entity
     */
    public Collision(Entity a, Entity b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public int hashCode() {
        return a.hashCode() ^ b.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Collision))
            return false;
        Collision otherCollision = (Collision) other;
        return (a.equals(otherCollision.a) && b.equals(otherCollision.b)) ||
               (a.equals(otherCollision.b) && b.equals(otherCollision.a));
    }

    /**
     * Gets the first entity in the collision.
     * @return The first entity
     */
    public Entity getA() {
        return a;
    }

    /**
     * Gets the second entity in the collision.
     * @return The second entity
     */
    public Entity getB() {
        return b;
    }
}
