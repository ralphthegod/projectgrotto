package com.deemaso.core.systems;

import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.components.Component;
import com.deemaso.core.events.EventListener;
import com.deemaso.core.events.SystemEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class System implements EventListener {

    final protected GameWorld gameWorld;
    final protected List<Entity> entities = new ArrayList<>();
    final protected List<Class<? extends Component>> requiredComponents = new ArrayList<>();
    final protected boolean requireAllComponents;

    protected System(
            GameWorld gameWorld,
            List<Class<? extends Component>> requiredComponents,
            boolean requireAllComponents
    ) {
        this.gameWorld = gameWorld;
        this.requireAllComponents = requireAllComponents;
        this.requiredComponents.addAll(requiredComponents);
    }

    public boolean registerEntity(Entity entity) {
        if (requireAllComponents) {
            if(entity.hasComponents(requiredComponents)) {
                entities.add(entity);
                return true;
            }
        } else {
            for (Class<? extends Component> componentClass : requiredComponents) {
                if (entity.hasComponent(componentClass)) {
                    entities.add(entity);
                    return true;
                }
            }
        }
        return false;
    }

    public void unregisterEntity(Entity entity) {
        entities.remove(entity);
    }

    public void unregisterEntities(List<Entity> entities) {
        for (Entity entity : entities) {
            unregisterEntity(entity);
        }
    }

    public abstract void update(float dt);
    protected abstract void finalize();
    public abstract void pause();
    public abstract void resume();
}
