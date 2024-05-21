package com.deemaso.core.events;

public interface EventListener<T extends Event>{
    void onEventReceived(T event);
}
