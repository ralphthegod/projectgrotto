package com.deemaso.grotto.systems;

import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.core.systems.System;
import com.deemaso.grotto.components.MovementComponent;
import com.deemaso.grotto.components.PhysicsComponent;

import org.jbox2d.common.Vec2;

import java.util.Arrays;

/**
 * A movement system for Project Grotto.
 * This system handles the movement of entities in the game.
 */
public class MovementSystem extends System {

    /**
     * Creates a new movement system.
     * @param gameWorld The game world
     */
    public MovementSystem(GameWorld gameWorld) {
        super(gameWorld, Arrays.asList(MovementComponent.class, PhysicsComponent.class), true);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        for (Entity entity : entities) {
            MovementComponent movement = entity.getComponent(MovementComponent.class);
            PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);

            if (movement != null && physics != null) {
                long currentTime = java.lang.System.currentTimeMillis();
                long timeSinceLastMovement = currentTime - movement.getLastMovementStartTime();

                if (movement.hasMovements() && timeSinceLastMovement >= (1000 / movement.getSpeed())) {
                    MovementComponent.Movement nextMovement = movement.getNextMovement();
                    if (nextMovement != null) {
                        stopMovement(physics);
                        applyImpulse(physics, nextMovement, movement.getPower());
                        movement.setLastMovementStartTime(currentTime);
                    }
                } else if (!movement.hasMovements() && timeSinceLastMovement >= (1000 / movement.getSpeed())) {
                    //stopMovement(physics);
                    movement.setLastMovementStartTime(currentTime);
                }
            }
        }
    }

    @Override
    protected void finalize() {

    }

    private void applyImpulse(PhysicsComponent physics, MovementComponent.Movement movement, float power) {
        Vec2 direction = new Vec2(movement.directionX, movement.directionY);
        direction.normalize();
        Vec2 impulse = direction.mul(power);

        physics.getBody().applyLinearImpulse(impulse, physics.getBody().getWorldCenter());
    }

    private void stopMovement(PhysicsComponent physics) {
        physics.getBody().setLinearVelocity(new Vec2(0, 0));
        physics.getBody().setAngularVelocity(0);
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
