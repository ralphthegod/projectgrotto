package com.deemaso.core.events;

import java.util.HashMap;
import java.util.Map;

public class SystemEvent {

    final String code;
    final Map<String,Object> data = new HashMap<>();

    public SystemEvent(String code) {
        this.code = code;
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public String getCode() {
        return code;
    }
}
