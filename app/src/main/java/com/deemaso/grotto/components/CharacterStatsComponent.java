package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;

public class CharacterStatsComponent extends Component {
    private int level = 1;
    private int experience = 0;
    private int maxHealth = 100;
    private int health = 100;
    private boolean isAlive = true;
    private String faction;

    public CharacterStatsComponent(int experience, String faction, int maxHealth) {
        this.experience = experience;
        this.faction = faction;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    public CharacterStatsComponent() {
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void addExperience(int experience) {
        this.experience += experience;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
