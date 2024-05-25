package com.deemaso.grotto.events;

import com.deemaso.core.collisions.Collision;
import com.deemaso.core.events.Event;

public class CollisionEvent implements Event {

    private final Collision collision;

    public CollisionEvent(Collision collision) {
        this.collision = collision;
    }

    public Collision getCollision() {
        return collision;
    }

}
