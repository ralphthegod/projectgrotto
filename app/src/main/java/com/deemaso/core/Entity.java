package com.deemaso.core;

import com.deemaso.core.components.Component;

import java.util.HashMap;
import java.util.List;

public class Entity {

    final private String id;

    /** A map of components that the entity has. */
    final private HashMap<Class<? extends Component>, Component> componentMap = new HashMap<>();

    public Entity(String id) {
        this.id = id;
    }

    /** Adds a component to the entity. Components order is important when initializing them.*/
    public void addComponent(Component c) {
        componentMap.put(c.getClass(), c);
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        return componentClass.cast(componentMap.get(componentClass));
    }

    public boolean hasComponent(Class<? extends Component> componentClass) {
        return componentMap.containsKey(componentClass);
    }

    public boolean hasComponents(List<Class<? extends Component>> componentClasses) {
        for(Class<? extends Component> c : componentClasses) {
            if(!hasComponent(c)) {
                return false;
            }
        }
        return true;
    }

    public String getId() {
        return id;
    }
}
