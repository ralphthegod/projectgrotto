package com.deemaso.grotto.ai;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the AI context.
 */
public class AIContext {
    private final Map<String, Object> context = new HashMap<>();

    /**
     * Puts a value into the context.
     * @param key The key
     * @param value The value
     */
    public void put(String key, Object value){
        context.put(key, value);
    }

    /**
     * Gets a value from the context.
     * @param key The key
     * @return The value
     */
    public Object get(String key){
        return context.get(key);
    }

    /**
     * Removes a value from the context.
     * @param key The key
     */
    public void remove(String key){
        context.remove(key);
    }

}
