package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;

/**
 * Represents a tile component.
 */
public class TileComponent extends Component {
    private int x;
    private int y;
    private float effectiveX;
    private float effectiveY;

    /**
     * Creates a new TileComponent.
     * @param x The x coordinate
     * @param y The y coordinate
     * @param effectiveX The effective x coordinate
     * @param effectiveY The effective y coordinate
     */
    public TileComponent(int x, int y, float effectiveX, float effectiveY) {
        this.x = x;
        this.y = y;
        this.effectiveX = effectiveX;
        this.effectiveY = effectiveY;
    }

    public TileComponent() {
        this.x = 0;
        this.y = 0;
        this.effectiveX = 0;
        this.effectiveY = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Get the effective x coordinate.
     * @return The effective x coordinate
     */
    public float getEffectiveX() {
        return effectiveX;
    }

    /**
     * Get the effective y coordinate.
     * @return The effective y coordinate
     */
    public float getEffectiveY() {
        return effectiveY;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setEffectiveX(float effectiveX) {
        this.effectiveX = effectiveX;
    }

    public void setEffectiveY(float effectiveY) {
        this.effectiveY = effectiveY;
    }
}
