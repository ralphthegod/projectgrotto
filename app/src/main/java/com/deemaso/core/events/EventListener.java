package com.deemaso.core.events;

/**
 * Represents an event listener.
 */
public interface EventListener {
    /**
     * Called when an event is received.
     * @param event The event
     */
    void onEvent(SystemEvent event);
}
