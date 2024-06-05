package com.deemaso.grotto.components;

import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.components.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a perception component.
 * Allows an entity to perceive other entities within a certain radius.
 */
public class PerceptionComponent extends Component {
    private float perceptionRadius;
    private final Set<Entity> perceivedEntities = new HashSet<>();

    /**
     * Creates a new PerceptionComponent.
     * @param perceptionRadius The perception radius
     */
    public PerceptionComponent(float perceptionRadius) {
        this.perceptionRadius = perceptionRadius;
    }

    public float getPerceptionRadius() {
        return perceptionRadius;
    }

    public void setPerceptionRadius(float perceptionRadius) {
        this.perceptionRadius = perceptionRadius;
    }

    /**
     * Get the entities perceived by the entity.
     * @return The perceived entities
     */
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
