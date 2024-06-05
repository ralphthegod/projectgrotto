package com.deemaso.core;

import com.deemaso.core.components.Component;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;

/**
 * Manages the creation of components.
 */
public class ComponentFactory {
    private final Map<String, ComponentCreator> creators = new HashMap<>();

    /**
     * Creates a new Component.
     */
    public interface ComponentCreator {
        Component create(Element element);
    }

    /**
     * Registers a component creator.
     * @param name The name of the component
     * @param creator The creator of the component
     */
    public void registerComponent(String name, ComponentCreator creator) {
        creators.put(name, creator);
    }

    /**
     * Creates a component by name.
     * @param name The name of the component
     * @param element The element to create the component from
     * @return The created component
     */
    public Component createComponent(String name, Element element) {
        if (creators.containsKey(name)) {
            ComponentCreator cc = creators.get(name);
            if(cc != null) {
                return cc.create(element);
            } else {
                throw new IllegalArgumentException("No creator registered for component: " + name);
            }
        } else {
            throw new IllegalArgumentException("No creator registered for component: " + name);
        }
    }
}