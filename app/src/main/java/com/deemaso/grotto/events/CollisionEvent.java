package com.deemaso.grotto.events;

import com.deemaso.core.collisions.Collision;

public class CollisionEvent {

    private final Collision collision;

    public CollisionEvent(Collision collision) {
        this.collision = collision;
    }

    public Collision getCollision() {
        return collision;
    }

}
