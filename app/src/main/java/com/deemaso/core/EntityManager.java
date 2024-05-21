package com.deemaso.core;

import com.deemaso.core.components.Component;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Manages entities and their components.
 */
public class EntityManager {
    private List<Entity> entities = new ArrayList<Entity>();
    private List<Entity> entitiesToDelete = new ArrayList<Entity>();

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void markEntityForDeletion(Entity e) {
        entitiesToDelete.add(e);
    }

    public List<Entity> getEntitiesMarkedForDeletion() {
        return entitiesToDelete;
    }

    public Entity createEntityById(String id) {
        try {
            File file = new File("path/to/your/xml/" + id + ".xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            Entity entity = new Entity();

            NodeList components = doc.getElementsByTagName("Component");
            for (int i = 0; i < components.getLength(); i++) {
                Element componentElement = (Element) components.item(i);
                String componentName = componentElement.getAttribute("name");
                Component component = ComponentFactory.createComponent(componentName, componentElement);
                entity.addComponent(component);
            }

            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void removeMarkedEntities() {
        for (Entity e : entitiesToDelete) {
            entities.remove(e);
        }
        entitiesToDelete.clear();
    }
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