package com.deemaso.grotto.events;

import com.deemaso.core.events.Event;
import org.jbox2d.dynamics.World;

public class PhysicsWorldEvent implements Event {
    private final World world;

    public PhysicsWorldEvent(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
