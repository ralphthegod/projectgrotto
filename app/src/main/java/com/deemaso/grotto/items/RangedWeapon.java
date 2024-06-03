package com.deemaso.grotto.items;

import java.util.List;

public class RangedWeapon extends Weapon{

    private final float bulletSpeed;

    public RangedWeapon(String name, String archetype, int damage, float bulletSpeed, float knockback) {
        super(name, archetype, damage, knockback);
        this.bulletSpeed = bulletSpeed;
    }

    public float getBulletSpeed() {
        return bulletSpeed;
    }
}
