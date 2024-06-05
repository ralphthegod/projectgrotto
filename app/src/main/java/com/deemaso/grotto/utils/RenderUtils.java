package com.deemaso.grotto.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * A utility class for rendering.
 */
public class RenderUtils {

    static public Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }

    static public float toMetersX(float x, float minX, float width, float screenWidth) {
        return minX + x * (width/screenWidth);
    }

    static public float toMetersY(float y, float minY, float height, float screenHeight) {
        return minY + y * (height/screenHeight);
    }

    static public float toPixelsX(float x, float minX, float width, int bufferWidth) {
        return (x-minX)/width*bufferWidth;
    }

    static public float toPixelsY(float y, float minY, float height, int bufferHeight) {
        return (y-minY)/height*bufferHeight;
    }

    static public float toPixelsXLength(float x, float width, int bufferWidth) {
        return x/width*bufferWidth;
    }

    static public float toPixelsYLength(float y, float height, int bufferHeight) {
        return y/height*bufferHeight;
    }

    static public Bitmap flipBitmap(Bitmap bitmap, boolean horizontal) {
        Matrix matrix = new Matrix();
        if(horizontal) {
            matrix.preScale(-1.0f, 1.0f);
            matrix.setScale(-1, 1);
            matrix.postTranslate(bitmap.getWidth(), 0);
        } else {
            matrix.preScale(1.0f, -1.0f);
            matrix.setScale(1, -1);
            matrix.postTranslate(0, bitmap.getHeight());
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}
