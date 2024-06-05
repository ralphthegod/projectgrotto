package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;

/**
 * Represents a limited life component. <br>
 * Contains the life time of the entity and the time it has been alive.
 */
public class LimitedLifeComponent extends Component {
    private final float lifeTime;
    private float timeAlive;

    /**
     * Creates a new LimitedLifeComponent.
     * @param lifeTime The life time of the entity
     */
    public LimitedLifeComponent(float lifeTime) {
        this.lifeTime = lifeTime;
        this.timeAlive = 0;
    }

    public float getLifeTime() {
        return lifeTime;
    }

    public float getTimeAlive() {
        return timeAlive;
    }

    public void setTimeAlive(float timeAlive) {
        this.timeAlive = timeAlive;
    }
}
