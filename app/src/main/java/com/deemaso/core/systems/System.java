package com.deemaso.core.systems;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.components.Component;
import com.deemaso.core.events.EventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a system in the game world.
 */
public abstract class System implements EventListener {

    final protected GameWorld gameWorld;
    final protected List<Entity> entities = new ArrayList<>();
    final protected List<Entity> entitiesToDelete = new ArrayList<>();
    final protected List<Class<? extends Component>> requiredComponents = new ArrayList<>();
    final protected boolean requireAllComponents;

    /**
     * Creates a new system with the given required components.
     * @param gameWorld The game world to use
     * @param requiredComponents The required components
     * @param requireAllComponents True if all components are required, false if any component is required
     */
    protected System(
            GameWorld gameWorld,
            List<Class<? extends Component>> requiredComponents,
            boolean requireAllComponents
    ) {
        this.gameWorld = gameWorld;
        this.requireAllComponents = requireAllComponents;
        this.requiredComponents.addAll(requiredComponents);
    }

    /**
     * Registers an entity with the system.
     * @param entity The entity to register
     * @return True if the entity was registered, false otherwise
     */
    public boolean registerEntity(Entity entity) {
        if(hasRequiredComponents(entity)){
            entities.add(entity);
            return true;
        }
        return false;
    }

    /**
     * It checks if the entity has the required components.
     * @param entity The entity to check
     */
    public boolean hasRequiredComponents(Entity entity){
        if (requireAllComponents) {
            return entity.hasComponents(requiredComponents);
        } else {
            for (Class<? extends Component> componentClass : requiredComponents) {
                if (entity.hasComponent(componentClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Unregisters an entity with the system.
     * @param entity The entity to unregister
     */
    public void unregisterEntity(Entity entity) {
        if(hasRequiredComponents(entity))
            entitiesToDelete.add(entity);
    }

    /**
     * Unregisters a list of entities with the system.
     * @param entities The entities to unregister
     */
    public void unregisterEntities(List<Entity> entities) {
        for (Entity entity : entities) {
            unregisterEntity(entity);
        }
    }

    /**
     * Deletes entities marked for deletion.
     * */
    public void deleteMarkedEntities() {
        for (Entity entity : entitiesToDelete) {
            entities.remove(entity);
        }
        entitiesToDelete.clear();
    }

    /**
     * Updates the system.
     * @param dt The time since the last update
     * */
    public void update(float dt){
        //deleteEntities();
    }

    protected abstract void finalize();
    public abstract void pause();
    public abstract void resume();
}
