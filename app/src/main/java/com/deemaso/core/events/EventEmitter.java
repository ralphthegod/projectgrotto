package com.deemaso.core.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface EventEmitter {

    Map<Class<? extends Event>, List<EventListener>> getListeners();

    default <T extends Event> void emitEvent(T event){
        final Map<Class<? extends Event>, List<EventListener>> listeners = getListeners();
        if (listeners.containsKey(event.getClass())) {
            final List<EventListener> eventListeners = listeners.get(event.getClass());
            if(eventListeners != null){
                for (EventListener listener : eventListeners) {
                    listener.onEventReceived(event);
                }
            }
        }
    }

    default <T extends Event> void registerListener(Class<T> eventType, EventListener<T> listener){
        Map<Class<? extends Event>, List<EventListener>> listeners = getListeners();
        if (!listeners.containsKey(eventType)) {
            listeners.put(eventType, new ArrayList<>());
        }
        final List<EventListener> eventListeners = listeners.get(eventType);
        if(eventListeners != null){
            eventListeners.add(listener);
        }
    }
}
