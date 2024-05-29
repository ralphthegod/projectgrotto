package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;

import java.util.Queue;
import java.util.LinkedList;

public class MovementComponent extends Component {

    private float power;
    private float speed;
    private long lastMovementStartTime;

    public long getLastMovementStartTime() {
        return lastMovementStartTime;
    }

    public void setLastMovementStartTime(long lastMovementStartTime) {
        this.lastMovementStartTime = lastMovementStartTime;
    }

    static public class Movement {
        public float directionX, directionY;

        public Movement(float directionX, float directionY) {
            this.directionX = directionX;
            this.directionY = directionY;
        }
    }

    private final Queue<Movement> movementQueue;

    public MovementComponent(float power, float speed) {
        this.power = power;
        this.speed = speed;
        movementQueue = new LinkedList<>();
    }

    public void addMovement(float directionX, float directionY) {
        movementQueue.add(new Movement(directionX, directionY));
    }

    public Movement getNextMovement() {
        return movementQueue.poll();
    }

    public boolean hasMovements() {
        return !movementQueue.isEmpty();
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }


}