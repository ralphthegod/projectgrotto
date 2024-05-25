package com.deemaso.core;

import android.content.Context;
import android.util.Log;

import com.deemaso.core.components.Component;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Manages entities and their components.
 */
public abstract class EntityManager {
    private List<Entity> entities = new ArrayList<Entity>();
    private List<Entity> entitiesToDelete = new ArrayList<Entity>();

    protected final ComponentFactory componentFactory = new ComponentFactory();

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void markEntityForDeletion(Entity e) {
        entitiesToDelete.add(e);
    }

    public List<Entity> getEntitiesMarkedForDeletion() {
        return entitiesToDelete;
    }

    public abstract Entity createEntityById(String id);

    protected EntityManager(){
        loadComponentFactoryCreators();
    }

    public void removeMarkedEntities() {
        for (Entity e : entitiesToDelete) {
            entities.remove(e);
        }
        entitiesToDelete.clear();
    }

    abstract protected void loadComponentFactoryCreators();
}

/*
    *
* */

/** EXAMPLE XML FILE
 * <Entity id="1">
 *     <Component name="PhysicsComponent">
 *         <x>10.0</x>
 *         <y>20.0</y>
 *         <mass>1.5</mass>
 *     </Component>
 *
 * </Entity>
 */