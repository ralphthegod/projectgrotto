package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;

public class LimitedLifeComponent extends Component {
    private final float lifeTime;
    private float timeAlive;

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
