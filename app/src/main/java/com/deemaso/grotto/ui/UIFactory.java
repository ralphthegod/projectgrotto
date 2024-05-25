package com.deemaso.grotto.ui;

import com.deemaso.core.Entity;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

public class UIFactory {
    private final Map<String, UIElementCreator> creators = new HashMap<>();

    public interface UIElementCreator {
        UIElement create(Element element, Entity entity);
    }

    public void registerUIElement(String name, UIElementCreator creator) {
        creators.put(name, creator);
    }

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
