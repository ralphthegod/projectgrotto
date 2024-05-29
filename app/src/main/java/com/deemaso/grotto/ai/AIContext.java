package com.deemaso.grotto.ai;

import java.util.HashMap;
import java.util.Map;

public class AIContext {
    private final Map<String, Object> context = new HashMap<>();

    public void put(String key, Object value){
        context.put(key, value);
    }

    public Object get(String key){
        return context.get(key);
    }

    public void remove(String key){
        context.remove(key);
    }

}
