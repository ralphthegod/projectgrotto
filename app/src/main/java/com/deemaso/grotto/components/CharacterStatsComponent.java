package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;

public class CharacterStatsComponent extends Component {
    private int level = 1;
    private int experience = 0;
    private boolean isAlive = true;
    private String faction;

    public CharacterStatsComponent(int experience, String faction) {
        this.experience = experience;
        this.faction = faction;
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
}
