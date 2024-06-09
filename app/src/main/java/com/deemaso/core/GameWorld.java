package com.deemaso.core;

import android.util.Log;

import com.deemaso.core.components.Component;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.core.systems.System;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
* The GameWorld class is the main class that manages the game world.
* It contains the EntityManager and a list of Systems that are used to update the game world.
* The update method is called every frame and updates all the systems.
 */
public class GameWorld {

    final private EntityManager entityManager;
    final private List<System> systems = new ArrayList<>();

    /**
     * Creates a new GameWorld.
     * @param entityManager The EntityManager to use
     */
    public GameWorld(
            EntityManager entityManager
    )
    {
        this.entityManager = entityManager;
    }

    /**
     * Adds an entity to the game world.
     * The entity is added to the EntityManager and registered with all systems.
     * @param entity The entity to add
     */
    public void addEntity(Entity entity) {
        entityManager.addEntity(entity);
        for(System s : systems) {
            s.registerEntity(entity);
        }
    }

    /**
     * Broadcasts an event to all systems.
     * @param event The event to broadcast
     */
    public void broadcastEvent(SystemEvent event) {
        for(System s : systems) {
            s.onEvent(event);
        }
    }

    /**
     * Creates an entity by id and adds it to the game world.
     * @param id The archetype id of the entity to create
     * @return The created entity
     */
    public Entity createEntityById(String id) {
        Entity e = entityManager.createEntityById(id);
        if(e != null) {
            addEntity(e);
        }
        return e;
    }

    /**
     * Creates an entity by id and adds it to the game world.
     * @param id The id of the entity to create
     * @param extraComponents Extra components to add to the entity
     * @return The created entity
     */
    public Entity createEntityById(String id, Collection<Component> extraComponents){
        Entity e = entityManager.createEntityById(id);
        if(e != null) {
            for(Component c : extraComponents){
                e.addComponent(c);
            }
            addEntity(e);
        }
        return e;
    }

    /**
     * Adds a system to the game world.
     * @param system The system to add
     */
    public void addSystem(System system) {
        systems.add(system);
    }

    /**
     * Gets a system by class.
     * @param systemClass The class of the system to get
     * @return The system, or null if not found
     */
    public System getSystem(Class<? extends System> systemClass) {
        for(System s : systems) {
            if(s.getClass() == systemClass) {
                return s;
            }
        }
        return null;
    }

    /**
     * Marks an entity for deletion.
     * The entity will be removed from all systems at the end of the frame.
     * @param e The entity to mark for deletion.
     */
    public void markEntityForDeletion(Entity e) {
        for(System s : systems) {
            s.unregisterEntity(e);
        }
        entityManager.markEntityForDeletion(e);
        Log.d("GameWorld", e.getId() + " marked for deletion.");
    }

    /**
     * Deletes all entities that are marked for deletion.
     */
    public void deleteMarkedEntities() {
        //List<Entity> entities = entityManager.getEntitiesMarkedForDeletion();
        for(System s : systems) {
            s.deleteMarkedEntities();
        }
        if(!entityManager.getEntitiesMarkedForDeletion().isEmpty()) Log.d("GameWorld", "Deleting marked entities...");
        entityManager.removeMarkedEntities();
    }

    /**
     * Updates the game world.
     * @param dt The time since the last frame
     */
    public void update(float dt) {
        for (System s : systems) {
            s.update(dt);
        }
        deleteMarkedEntities();
    }

    /**
     * Pauses all systems.
     */
    public void pause() {
        for(System s : systems) {
            s.pause();
        }
    }

    /**
     * Resumes all systems.
     */
    public void resume() {
        for(System s : systems) {
            s.resume();
        }
    }
}
