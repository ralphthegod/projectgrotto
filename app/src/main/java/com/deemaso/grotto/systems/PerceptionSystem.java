package com.deemaso.grotto.systems;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.core.systems.System;
import com.deemaso.grotto.components.PerceptionComponent;
import com.deemaso.grotto.components.PhysicsComponent;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PerceptionSystem extends System {

    private final World physicsWorld;

    public PerceptionSystem(GameWorld gameWorld, World physicsWorld) {
        super(gameWorld, Arrays.asList(PerceptionComponent.class, PhysicsComponent.class), true);
        this.physicsWorld = physicsWorld;
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        for (Entity entity : entities) {
            PerceptionComponent perception = entity.getComponent(PerceptionComponent.class);
            PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);

            if (perception != null && physics != null) {
                perception.clearPerceivedEntities();

                // Create an AABB for the perception radius
                Vec2 center = physics.getBody().getPosition();
                float radius = perception.getPerceptionRadius();
                AABB aabb = new AABB(
                        new Vec2(center.x - radius, center.y - radius),
                        new Vec2(center.x + radius, center.y + radius)
                );

                Set<Fixture> foundFixtures = new HashSet<>();

                physicsWorld.queryAABB(new QueryCallback() {
                    @Override
                    public boolean reportFixture(Fixture fixture) {
                        foundFixtures.add(fixture);
                        return true;
                    }
                }, aabb);

                for (Fixture fixture : foundFixtures) {
                    Body body = fixture.getBody();
                    if (body != physics.getBody()) {
                        Entity otherEntity = (Entity) body.getUserData();
                        if (otherEntity != null) {
                            perception.addPerceivedEntity(otherEntity);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void finalize() {

    }

    private float distanceBetween(Body body1, Body body2) {
        Vec2 pos1 = body1.getPosition();
        Vec2 pos2 = body2.getPosition();
        return pos1.sub(pos2).length();
    }

    @Override
    public boolean registerEntity(Entity entity) {
        return super.registerEntity(entity);
    }

    @Override
    public void unregisterEntity(Entity entity) {
        super.unregisterEntity(entity);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void onEvent(SystemEvent event) {

    }
}
