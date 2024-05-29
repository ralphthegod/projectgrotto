package com.deemaso.grotto.ai.actions;

import com.deemaso.core.Entity;
import com.deemaso.grotto.ai.AIContext;
import com.deemaso.grotto.ai.Action;
import com.deemaso.grotto.components.MovementComponent;
import com.deemaso.grotto.components.PhysicsComponent;

import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

public class MoveToHostileTargetAction implements Action {

    private static class ObstacleDetector implements RayCastCallback {
        private boolean obstacleDetected = false;
        private final Vec2 avoidanceNormal = new Vec2();

        @Override
        public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
            if (fraction < 1.0f) {
                obstacleDetected = true;
                avoidanceNormal.set(normal);
                return fraction;
            }
            return 1.0f;
        }

        public boolean isObstacleDetected() {
            return obstacleDetected;
        }

        public Vec2 getAvoidanceNormal() {
            return avoidanceNormal;
        }
    }

    @Override
    public void execute(AIContext context) {
        Entity entity = (Entity) context.get("self");
        Entity targetEntity = (Entity) context.get("target");

        if (targetEntity == null) {
            return;
        }

        PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
        MovementComponent movement = entity.getComponent(MovementComponent.class);
        if (physics == null || movement == null) {
            return;
        }

        long currentTime = java.lang.System.currentTimeMillis();
        long timeSinceLastMovement = currentTime - movement.getLastMovementStartTime();

        if (timeSinceLastMovement < (1000 / movement.getSpeed())) {
            return;
        }

        World physicsWorld = physics.getBody().getWorld();
        Vec2 targetPosition = targetEntity.getComponent(PhysicsComponent.class).getBody().getPosition();
        Vec2 currentPosition = physics.getBody().getPosition();
        Vec2 direction = targetPosition.sub(currentPosition);
        float maxDistance = direction.length();
        direction.normalize();

        ObstacleDetector obstacleDetector = new ObstacleDetector();

        Vec2 rayEnd = currentPosition.add(direction.mul(maxDistance));
        physicsWorld.raycast(obstacleDetector, currentPosition, rayEnd);

        if (!obstacleDetector.isObstacleDetected()) {
            movement.addMovement(direction.x, direction.y);
        } else {
            avoidObstacle(movement, direction, obstacleDetector.getAvoidanceNormal(), maxDistance);
        }
    }

    private void avoidObstacle(MovementComponent movement, Vec2 direction, Vec2 normal, float distance) {
        Vec2 avoidanceDirection = direction.add(normal.mul(1.0f / distance));
        avoidanceDirection.normalize();
        movement.addMovement(avoidanceDirection.x, avoidanceDirection.y);
    }
}
