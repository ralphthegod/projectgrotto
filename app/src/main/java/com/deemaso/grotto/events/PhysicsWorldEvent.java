package com.deemaso.grotto.events;

import org.jbox2d.dynamics.World;

public class PhysicsWorldEvent {
    private final World world;

    public PhysicsWorldEvent(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
