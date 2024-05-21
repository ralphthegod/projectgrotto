package com.deemaso.core;

import com.deemaso.core.systems.System;

import java.util.ArrayList;
import java.util.List;

/*
* The GameWorld class is the main class that manages the game world.
* It contains the EntityManager and a list of Systems that are used to update the game world.
* The update method is called every frame and updates all the systems.
 */
public class GameWorld {

    final private EntityManager entityManager;
    final private List<System> systems = new ArrayList<>();

    public GameWorld(
            EntityManager entityManager
    )
    {
        this.entityManager = entityManager;
    }

    public void addEntity(Entity entity) {
        entityManager.addEntity(entity);
        for(System s : systems) {
            s.registerEntity(entity);
        }
    }

    public Entity createEntityById(String id) {
        Entity e = entityManager.createEntityById(id);
        if(e != null) {
            addEntity(e);
        }
        return e;
    }

    public void addSystem(System system) {
        systems.add(system);
    }

    public System getSystem(Class<? extends System> systemClass) {
        for(System s : systems) {
            if(s.getClass() == systemClass) {
                return s;
            }
        }
        return null;
    }

    public void markEntityForDeletion(Entity e) {
        for(System s : systems) {
            s.unregisterEntity(e);
        }
        entityManager.markEntityForDeletion(e);
    }

    public void deleteMarkedEntities() {
        List<Entity> entities = entityManager.getEntitiesMarkedForDeletion();
        for(System s : systems) {
            s.unregisterEntities(entities);
        }
        entityManager.removeMarkedEntities();
    }

    public void update(float dt) {

        // Update all systems
        for (System s : systems) {
            s.update(dt);
        }

        // Remove entities that are marked for deletion
        deleteMarkedEntities();

    }

    public void pause() {
        for(System s : systems) {
            s.pause();
        }
    }

    public void resume() {
        for(System s : systems) {
            s.resume();
        }
    }
}
