package com.deemaso.core;

import com.deemaso.core.components.Component;

import java.util.HashMap;
import java.util.List;

/**
 * Represents an entity in the game world.
 */
public class Entity {

    final private String id;

    final private HashMap<Class<? extends Component>, Component> componentMap = new HashMap<>();

    /**
     * Creates a new entity with the given id.
     * @param id The id of the entity
     */
    public Entity(String id) {
        this.id = id;
    }

    /**
     * Adds a component to the entity.
     * @param c The component to add
     */
    public void addComponent(Component c) {
        componentMap.put(c.getClass(), c);
    }

    /**
     * Gets a component by class.
     * @param componentClass The class of the component to get
     * @return The component, or null if not found
     */
    public <T extends Component> T getComponent(Class<T> componentClass) {
        return componentClass.cast(componentMap.get(componentClass));
    }

    /**
     * Gets a list of components by class.
     * @param componentClass The classes of the components to get
     * @return The components, or null if not found
     */
    public boolean hasComponent(Class<? extends Component> componentClass) {
        return componentMap.containsKey(componentClass);
    }

    /**
     * Checks if the entity has all of the given components.
     * @param componentClasses The classes of the components to check for
     * @return True if the entity has all of the components, false otherwise
     */
    public boolean hasComponents(List<Class<? extends Component>> componentClasses) {
        for(Class<? extends Component> c : componentClasses) {
            if(!hasComponent(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return The id of the entity
     */
    public String getId() {
        return id;
    }
}
