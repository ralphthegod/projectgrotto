package com.deemaso.grotto.data;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Log;

import com.deemaso.grotto.ai.DecisionTreeFactory;
import com.deemaso.grotto.ai.TreeNode;
import com.deemaso.grotto.items.MeleeWeapon;
import com.deemaso.grotto.items.RangedWeapon;
import com.deemaso.grotto.items.Weapon;
import com.deemaso.grotto.utils.Helpers;
import com.deemaso.grotto.utils.RenderUtils;

import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Loads resources from the assets folder.
 */
public class ResourceLoader {
    private final Resources resources;
    private final BitmapFactory.Options options;

    // Cache bitmaps to avoid decoding them multiple times
    private final Map<String, Bitmap> bitmaps = new HashMap<>();
    private final Map<String, Typeface> fonts = new HashMap<>();

    /**
     * Creates a new ResourceLoader.
     * @param resources The resources
     */
    public ResourceLoader(Resources resources) {
        this.resources = resources;
        this.options = new BitmapFactory.Options();
        this.options.inScaled = false;
    }

    /**
     * Loads a bitmap from the resources.
     * @param resourceId The resource ID
     * @return The bitmap
     */
    public Bitmap loadBitmap(int resourceId) {
        String key = String.valueOf(resourceId);
        if (bitmaps.containsKey(key)) {
            return bitmaps.get(key);
        }
        else{
            Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId, options);
            bitmaps.put(key, bitmap);
            return bitmap;
        }
    }

    /**
     * Loads a bitmap from the assets.
     * @param asset The asset
     * @return The bitmap
     */
    public Bitmap loadBitmapAsset(String asset) {
        if (bitmaps.containsKey(asset)) {
            return bitmaps.get(asset);
        }

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(resources.getAssets().open(asset), null, options);
            bitmaps.put(asset, bitmap);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads a list of bitmaps from the assets.
     * @param assets The assets
     * @return The bitmaps
     */
    public List<Bitmap> loadBitmapAssets(List<String> assets) {
        List<Bitmap> bitmaps = new ArrayList<>();
        for (String asset : assets) {
            bitmaps.add(loadBitmapAsset(asset));
        }
        return bitmaps;
    }

    /**
     * Loads a list of bitmaps from the resources.
     * @param resourceIds The resource IDs
     * @return The bitmaps
     */
    public List<Bitmap> loadBitmaps(List<Integer> resourceIds) {
        List<Bitmap> bitmaps = new ArrayList<>();
        for (int resourceId : resourceIds) {
            bitmaps.add(loadBitmap(resourceId));
        }
        return bitmaps;
    }

    /**
     * Loads a font from the assets.
     * @param resource The resource
     * @return The font
     */
    public Typeface loadFont(String resource) {
        if (fonts.containsKey(resource)) {
            return fonts.get(resource);
        }
        Typeface font = Typeface.createFromAsset(resources.getAssets(), resource);
        fonts.put(resource, font);
        return font;
    }

    /**
     * Loads a decision tree from the assets.
     * @param path The path
     * @return The decision tree
     */
    public static TreeNode loadDecisionTree(Context context, String path) {
        try {
            InputStream inputStream = context.getAssets().open("archetypes/decision_trees/" + path + ".xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();
            return DecisionTreeFactory.createDecisionTree(doc.getDocumentElement());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads a weapon from the assets.
     * @param path The path
     * @return The weapon
     */
    public static Weapon loadWeapon(Context context, String path) {
        try {
            InputStream inputStream = context.getAssets().open("archetypes/weapons/" + path + ".xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();

            String name = Helpers.getAttributeAsString(doc.getDocumentElement(), "weaponName", "");
            String archetype = Helpers.getAttributeAsString(doc.getDocumentElement(), "archetype", "");
            int damage =  Helpers.getAttributeAsInt(doc.getDocumentElement(), "damage", 0);
            float knockback = Helpers.getAttributeAsFloat(doc.getDocumentElement(), "knockback", 0);
            String type = Helpers.getAttributeAsString(doc.getDocumentElement(), "type", "");

            if(type.equals("MELEE")){
                float slashSpeed = Helpers.getAttributeAsFloat(doc.getDocumentElement(), "slashSpeed", 0);
                return new MeleeWeapon(name, archetype, damage, slashSpeed, knockback);
            }
            else if(type.equals("RANGED")){
                float bulletSpeed = Helpers.getAttributeAsFloat(doc.getDocumentElement(), "bulletSpeed", 0);
                float reloadTime = Helpers.getAttributeAsFloat(doc.getDocumentElement(), "reloadTime", 0);
                return new RangedWeapon(name, archetype, damage, bulletSpeed, knockback, reloadTime);
            }
            else{
                return new Weapon(name, archetype, damage, knockback);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
