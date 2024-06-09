package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a character stats component. <br>
 * Contains the stats of the character.
 */
public class CharacterStatsComponent extends Component {
    private boolean isAlive = true;
    private Map<String, Object> stats = new HashMap<>();

    /**
     * Creates a new CharacterStatsComponent.
     * @param experience The experience
     * @param faction The faction
     * @param maxHealth The maximum health
     */
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

    public void setStats(Map<String,Object> stats){
        this.stats = stats;
    }

    public Map<String, Object> getStats(){
        return stats;
    }

    public boolean hasStat(String key) {
        return stats.containsKey(key);
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
