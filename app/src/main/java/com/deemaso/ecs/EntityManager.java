package com.deemaso.ecs;

import java.util.ArrayList;
import java.util.Collection;

public class EntityManager {
    private ArrayList<Entity> entities = new ArrayList<Entity>();

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void removeEntity(Entity e) {
        entities.remove(e);
    }

    public Collection<Entity> queryWithComponent(Class<? extends Component> componentClass) {
        Collection<Entity> result = new ArrayList<Entity>();
        for (Entity e : entities) {
            if (e != null && e.hasComponent(componentClass)) {
                result.add(e);
            }
        }
        return result;
    }
}
