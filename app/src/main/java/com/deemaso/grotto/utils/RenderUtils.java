package com.deemaso.grotto.utils;

public class RenderUtils {

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

}
