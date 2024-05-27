package com.deemaso.grotto.ui;

import android.graphics.Canvas;
import android.util.Log;

import com.deemaso.core.Box;

import java.util.ArrayList;
import java.util.List;

public abstract class UIElement {

    protected Canvas canvas;
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected boolean isVisible = true;
    protected List<UIElement> children = new ArrayList<>();

    public UIElement(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(float screenX, float screenY){
        for (UIElement child : children) {
            child.draw(screenX + child.x, screenY + child.y);
        }
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        for(UIElement child : children){
            child.setCanvas(canvas);
        }
        this.canvas = canvas;
    }

    public void addChild(UIElement element) {
        children.add(element);
    }

    public void removeChild(UIElement element) {
        children.remove(element);
    }
}
