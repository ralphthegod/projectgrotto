package com.deemaso.grotto.components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.deemaso.core.components.RenderComponent;
import com.deemaso.grotto.utils.RenderUtils;

import java.util.ArrayList;
import java.util.List;

public class GrottoRenderComponent extends RenderComponent {

    private float screenSemiWidth;
    private float screenSemiHeight;

    private Canvas canvas;
    private Paint paint;

    private List<Bitmap> bitmap;
    private List<Bitmap> horizontalFlipBitmap;

    private List<String> resourceIds;
    private int currentFrame = 0;
    private float lastFrameTime = 0.0f;
    private float animationFrameDuration;

    private final Rect src = new Rect();
    private final RectF dst = new RectF();

    public void setScreenSemiWidth(float screenSemiWidth) {
        this.screenSemiWidth = screenSemiWidth;
    }

    public void setScreenSemiHeight(float screenSemiHeight) {
        this.screenSemiHeight = screenSemiHeight;
    }

    public float getScreenSemiWidth() {
        return screenSemiWidth;
    }

    public float getScreenSemiHeight() {
        return screenSemiHeight;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Paint getPaint() {
        return paint;
    }

    public Rect getSrc() {
        return src;
    }

    public RectF getDst() {
        return dst;
    }

    public Bitmap getBitmap(int index) {
        return bitmap.get(index);
    }

    public List<Bitmap> getBitmaps() {
        return bitmap;
    }

    public void setBitmaps(List<Bitmap> bitmap) {
        this.bitmap = bitmap;
    }

    public List<String> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<String> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public float getAnimationFrameDuration() {
        return animationFrameDuration;
    }
    
    public void setAnimationFrameDuration(float animationFrameDuration) {
        this.animationFrameDuration = animationFrameDuration;
    }

    public Bitmap getNextFrame(float deltaTime, boolean flipHorizontal) {
        if(bitmap.size() == 1) {
            if(flipHorizontal) {
                if(horizontalFlipBitmap == null || horizontalFlipBitmap.isEmpty()) {
                    horizontalFlipBitmap = new ArrayList<>();
                    horizontalFlipBitmap.add(RenderUtils.flipBitmap(bitmap.get(0), true));
                }
                return horizontalFlipBitmap.get(0);
            }
            return bitmap.get(0);
        }
        lastFrameTime += deltaTime;
        if (lastFrameTime >= animationFrameDuration) {
            lastFrameTime = 0.0f;
            currentFrame++;
            if (currentFrame >= bitmap.size()) {
                currentFrame = 0;
            }
        }
        if(flipHorizontal) {
            if(horizontalFlipBitmap == null || horizontalFlipBitmap.isEmpty()) {
                horizontalFlipBitmap = new ArrayList<>();
                for (Bitmap b : bitmap) {
                    horizontalFlipBitmap.add(RenderUtils.flipBitmap(b, true));
                }
            }
            return horizontalFlipBitmap.get(currentFrame);
        }
        return bitmap.get(currentFrame);
    }


}
