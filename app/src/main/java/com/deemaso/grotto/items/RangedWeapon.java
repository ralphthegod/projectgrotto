package com.deemaso.grotto.items;

import java.util.List;

/**
 * A ranged weapon.
 */
public class RangedWeapon extends Weapon{

    private final float bulletSpeed;
    private final float reloadTime;

    /**
     * Creates a new ranged weapon.
     * @param name The name
     * @param archetype The archetype
     * @param damage The damage
     * @param bulletSpeed The bullet speed
     * @param knockback The knockback
     */
    public RangedWeapon(String name, String archetype, int damage, float bulletSpeed, float knockback, float reloadTime) {
        super(name, archetype, damage, knockback);
        this.bulletSpeed = bulletSpeed;
        this.reloadTime = reloadTime;
    }

    public float getBulletSpeed() {
        return bulletSpeed;
    }

    public float getReloadTime() {
        return reloadTime;
    }
}
