package com.deemaso.core;

import com.deemaso.core.components.Component;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;

public class ComponentFactory {
    private final Map<String, ComponentCreator> creators = new HashMap<>();

    public interface ComponentCreator {
        Component create(Element element);
    }

    public void registerComponent(String name, ComponentCreator creator) {
        creators.put(name, creator);
    }

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