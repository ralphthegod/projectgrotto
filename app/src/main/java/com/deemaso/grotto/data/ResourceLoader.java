package com.deemaso.grotto.data;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;

import com.deemaso.grotto.utils.RenderUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceLoader {
    private final Resources resources;
    private final BitmapFactory.Options options;

    // Cache bitmaps to avoid decoding them multiple times
    private final Map<String, Bitmap> bitmaps = new HashMap<>();
    private final Map<String, Typeface> fonts = new HashMap<>();

    public ResourceLoader(Resources resources) {
        this.resources = resources;
        this.options = new BitmapFactory.Options();
        this.options.inScaled = false;
    }

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

    public List<Bitmap> loadBitmapAssets(List<String> assets) {
        List<Bitmap> bitmaps = new ArrayList<>();
        for (String asset : assets) {
            bitmaps.add(loadBitmapAsset(asset));
        }
        return bitmaps;
    }

    public List<Bitmap> loadBitmaps(List<Integer> resourceIds) {
        List<Bitmap> bitmaps = new ArrayList<>();
        for (int resourceId : resourceIds) {
            bitmaps.add(loadBitmap(resourceId));
        }
        return bitmaps;
    }

    public Typeface loadFont(String resource) {
        if (fonts.containsKey(resource)) {
            return fonts.get(resource);
        }
        Typeface font = Typeface.createFromAsset(resources.getAssets(), resource);
        fonts.put(resource, font);
        return font;
    }
}
