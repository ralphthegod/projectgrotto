package com.deemaso.core.components;

/**
 * Represents a transform component.
 */
public class TransformComponent extends Component {
    private float x, y;
    private float rotation;
    private float scale;

    /**
     * Creates a new TransformComponent.
     * @param x The x position
     * @param y The y position
     * @param rotation The rotation
     * @param scale The scale
     */
    public TransformComponent(float x, float y, float rotation, float scale) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.scale = scale;
    }

    /**
     * Gets the x position.
     * @return The x position
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the x position.
     * @param x The x position
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Gets the y position.
     * @return The y position
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the y position.
     * @param y The y position
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Gets the rotation.
     * @return The rotation
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation.
     * @param rotation The rotation
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    /**
     * Gets the scale.
     * @return The scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * Sets the scale.
     * @param scale The scale
     */
    public void setScale(float scale) {
        this.scale = scale;
    }
}
