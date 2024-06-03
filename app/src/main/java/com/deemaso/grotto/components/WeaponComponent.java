package com.deemaso.grotto.components;

import com.deemaso.core.Entity;
import com.deemaso.core.components.Component;
import com.deemaso.grotto.items.Weapon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WeaponComponent extends Component {
    private Weapon weapon;
    private Entity owner;
    private Collection<String> ignoreFactions;
    private long lastFired = 0;

    public WeaponComponent(Weapon weapon, Collection<String> ignoreFactions) {
        this.weapon = weapon;
        this.ignoreFactions = ignoreFactions;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public Entity getOwner() {
        return owner;
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    public Collection<String> getIgnoreFactions() {
        return ignoreFactions;
    }

    public void setIgnoreFactions(Collection<String> ignoreFactions) {
        this.ignoreFactions = ignoreFactions;
    }

    public void addIgnoreFaction(String faction) {
        ignoreFactions.add(faction);
    }

    public void removeIgnoreFaction(String faction) {
        ignoreFactions.remove(faction);
    }

    public long getLastFired() {
        return lastFired;
    }

    public void setLastFired(long lastFired) {
        this.lastFired = lastFired;
    }
}
