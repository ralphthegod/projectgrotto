package com.deemaso.ecs;

import java.util.ArrayList;
public abstract class Entity {
    final private ArrayList<Component> components = new ArrayList<Component>();

    private void addComponent(Component c) {
        components.add(c);
    }

    public boolean hasComponent(Class<? extends Component> componentClass) {
        for (Component c : components) {
            if (componentClass.isInstance(c)) {
                return true;
            }
        }
        return false;
    }
}
