package com.deemaso.grotto.components;

import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.components.Component;

import java.util.HashSet;
import java.util.Set;

public class PerceptionComponent extends Component {
    private float perceptionRadius;
    private final Set<Entity> perceivedEntities = new HashSet<>();

    public PerceptionComponent(float perceptionRadius) {
        this.perceptionRadius = perceptionRadius;
    }

    public float getPerceptionRadius() {
        return perceptionRadius;
    }

    public void setPerceptionRadius(float perceptionRadius) {
        this.perceptionRadius = perceptionRadius;
    }

    public Set<Entity> getPerceivedEntities() {
        return perceivedEntities;
    }

    public void clearPerceivedEntities() {
        perceivedEntities.clear();
    }

    public void addPerceivedEntity(Entity entity) {
        perceivedEntities.add(entity);
    }

    public void removePerceivedEntity(Entity entity) {
        perceivedEntities.remove(entity);
    }

    public boolean hasPerceivedEntity(Entity entity) {
        return perceivedEntities.contains(entity);
    }

}
