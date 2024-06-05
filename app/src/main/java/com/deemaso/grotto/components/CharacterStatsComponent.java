package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;

import java.util.HashMap;
import java.util.Map;

public class CharacterStatsComponent extends Component {
    private boolean isAlive = true;
    private final Map<String, Object> stats = new HashMap<>();

    public CharacterStatsComponent(int experience, String faction, int maxHealth) {
        stats.put("experience", experience);
        stats.put("level", 1);
        stats.put("faction", faction);
        stats.put("maxHealth", maxHealth);
        stats.put("health", maxHealth);
    }

    public Object getStat(String key) {
        return stats.get(key);
    }

    public void setStat(String key, Object value) {
        stats.put(key, value);
    }

    public CharacterStatsComponent() {
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

}
