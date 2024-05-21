package com.deemaso.core.components;

public class TransformComponent extends Component {
    private float x, y;
    private float rotation;
    private float scale;

    public TransformComponent(float x, float y, float rotation, float scale) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.scale = scale;
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

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
