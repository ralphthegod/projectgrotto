package com.deemaso.ecs;

public abstract class System {

    final private EntityManager entityManager;

    public System(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public abstract void update(float dt);
}
