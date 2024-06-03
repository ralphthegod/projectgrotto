package com.deemaso.grotto.items;

import android.content.Context;

import com.deemaso.grotto.utils.Helpers;

import org.w3c.dom.Document;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MeleeWeapon extends Weapon{
    private final float slashSpeed;

    public MeleeWeapon(String name, String archetype, int damage, float slashSpeed, float knockback) {
        super(name, archetype, damage, knockback);
        this.slashSpeed = slashSpeed;
    }

    public float getSlashSpeed() {
        return slashSpeed;
    }

}
