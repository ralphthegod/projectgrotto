package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;

public class CharacterStatsComponent extends Component {
    private int level = 1;
    private int experience = 0;
    private boolean isAlive = true;

    public CharacterStatsComponent(int experience) {
        this.experience = experience;
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
}
