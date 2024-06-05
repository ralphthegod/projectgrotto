package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;

import java.util.Queue;
import java.util.LinkedList;

/**
 * Represents a movement component.
 */
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

    /**
     * Represents a movement. <br>
     * Contains the direction of the movement.
     */
    static public class Movement {
        public float directionX, directionY;

        public Movement(float directionX, float directionY) {
            this.directionX = directionX;
            this.directionY = directionY;
        }
    }

    private final Queue<Movement> movementQueue;

    /**
     * Creates a new MovementComponent.
     * @param power The power
     * @param speed The speed
     */
    public MovementComponent(float power, float speed) {
        this.power = power;
        this.speed = speed;
        movementQueue = new LinkedList<>();
    }

    /**
     * Add a movement to the movement queue.
     * @param directionX The direction on the x axis
     * @param directionY The direction on the y axis
     */
    public void addMovement(float directionX, float directionY) {
        movementQueue.add(new Movement(directionX, directionY));
    }

    /**
     * Get the next movement from the movement queue.
     * @return The next movement
     */
    public Movement getNextMovement() {
        return movementQueue.poll();
    }

    /**
     * Check if the movement queue is empty.
     * @return True if the movement queue is empty, false otherwise
     */
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