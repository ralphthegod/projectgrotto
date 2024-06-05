package com.deemaso.grotto.ui;

import com.deemaso.core.Entity;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory for creating UI elements.
 */
public class UIFactory {
    private final Map<String, UIElementCreator> creators = new HashMap<>();

    /**
     * Creates a new UI Element.
     */
    public interface UIElementCreator {
        UIElement create(Element element, Entity entity);
    }

    /**
     * Registers a UI element creator.
     * @param name The name of the UI element
     * @param creator The creator
     */
    public void registerUIElement(String name, UIElementCreator creator) {
        creators.put(name, creator);
    }

    /**
     * Creates a UI element.
     * @param name The name of the UI element
     * @param element The XML element
     * @param entity The entity
     * @return The UI element
     */
    public UIElement createUIElement(String name, Element element, Entity entity) {
        if (creators.containsKey(name)) {
            UIElementCreator cc = creators.get(name);
            if(cc != null) {
                return cc.create(element, entity);
            } else {
                throw new IllegalArgumentException("No creator registered for UI element: " + name);
            }
        } else {
            throw new IllegalArgumentException("No creator registered for UI element: " + name);
        }
    }
}
