package com.deemaso.grotto.data;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;

public class ResourceLoader {
    private final Resources resources;
    private final BitmapFactory.Options options;

    public ResourceLoader(Resources resources) {
        this.resources = resources;
        this.options = new BitmapFactory.Options();
        this.options.inScaled = false;
    }

    public Bitmap loadBitmap(int resourceId) {
        return BitmapFactory.decodeResource(resources, resourceId, options);
    }

    public List<Bitmap> loadBitmaps(List<Integer> resourceIds) {
        List<Bitmap> bitmaps = new ArrayList<>();
        for (int resourceId : resourceIds) {
            bitmaps.add(loadBitmap(resourceId));
        }
        return bitmaps;
    }

    public Typeface loadFont(String resource) {
        return Typeface.createFromAsset(resources.getAssets(), resource);
    }
}
