package com.deemaso.grotto.events;

import com.deemaso.core.events.Event;
import com.google.fpl.liquidfun.World;

public class PhysicsWorldEvent implements Event {
    private final World world;

    public PhysicsWorldEvent(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
