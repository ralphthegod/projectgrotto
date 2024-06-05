package com.deemaso.core.events;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a system event.
 */
public class SystemEvent {

    final String code;
    final Map<String,Object> data = new HashMap<>();

    /**
     * Creates a new system event with the given code.
     * @param code The code of the event
     */
    public SystemEvent(String code) {
        this.code = code;
    }

    /**
     * Puts a value into the event data.
     * @param key The key of the value
     * @param value The value
     */
    public void put(String key, Object value) {
        data.put(key, value);
    }

    /**
     * Gets a value from the event data.
     * @param key The key of the value
     * @return The value
     */
    public Object get(String key) {
        return data.get(key);
    }

    /**
     * Gets the code of the event.
     * @return The code
     */
    public String getCode() {
        return code;
    }
}
