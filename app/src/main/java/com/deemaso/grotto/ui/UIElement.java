package com.deemaso.grotto.ui;

import android.graphics.Canvas;
import android.util.Log;

import com.deemaso.core.Box;

import java.util.ArrayList;
import java.util.List;

/**
 * For rendering UI elements on the screen.
 */
public abstract class UIElement {

    protected Canvas canvas;
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected boolean isVisible = true;
    protected List<UIElement> children = new ArrayList<>();

    /**
     * Creates a new UI element.
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     */
    public UIElement(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Draws the UI element.
     * @param screenX The x position on the screen
     * @param screenY The y position on the screen
     */
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

    /**
     * Adds a child to the UI element.
     * */
    public void addChild(UIElement element) {
        element.setCanvas(canvas);
        children.add(element);
    }

    public void removeChild(UIElement element) {
        children.remove(element);
    }
}
