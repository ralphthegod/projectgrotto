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

public class Weapon {
    private final String name;
    private final String archetype;
    private final int damage;
    private final float knockback;

    public Weapon(String name, String archetype, int damage, float knockback) {
        this.name = name;
        this.archetype = archetype;
        this.damage = damage;
        this.knockback = knockback;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public String getArchetype() {
        return archetype;
    }

    public float getKnockback() {
        return knockback;
    }
}
