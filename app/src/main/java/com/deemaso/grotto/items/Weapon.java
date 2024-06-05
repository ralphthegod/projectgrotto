package com.deemaso.grotto.items;

import android.content.Context;
import android.graphics.Bitmap;

import com.deemaso.grotto.utils.Helpers;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * A weapon.
 */
public class Weapon {
    private final String name;
    private final String archetype;
    private final int damage;
    private final float knockback;

    /**
     * Creates a new weapon.
     * @param name The name
     * @param archetype The archetype
     * @param damage The damage
     * @param knockback The knockback
     */
    public Weapon(String name, String archetype, int damage, float knockback) {
        this.name = name;
        this.archetype = archetype;
        this.damage = damage;
        this.knockback = knockback;
    }

    /**
     * Gets the name.
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the damage.
     * @return The damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Gets the archetype.
     * @return The archetype
     */
    public String getArchetype() {
        return archetype;
    }

    /**
     * Gets the knockback.
     * @return The knockback
     */
    public float getKnockback() {
        return knockback;
    }
}
