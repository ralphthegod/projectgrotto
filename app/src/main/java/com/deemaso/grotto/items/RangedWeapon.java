package com.deemaso.grotto.items;

import java.util.List;

/**
 * A ranged weapon.
 */
public class RangedWeapon extends Weapon{

    private final float bulletSpeed;

    /**
     * Creates a new ranged weapon.
     * @param name The name
     * @param archetype The archetype
     * @param damage The damage
     * @param bulletSpeed The bullet speed
     * @param knockback The knockback
     */
    public RangedWeapon(String name, String archetype, int damage, float bulletSpeed, float knockback) {
        super(name, archetype, damage, knockback);
        this.bulletSpeed = bulletSpeed;
    }

    public float getBulletSpeed() {
        return bulletSpeed;
    }
}
