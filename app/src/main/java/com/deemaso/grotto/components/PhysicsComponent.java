package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;


public class PhysicsComponent extends Component {
    private Body body;
    private BodyType bodyType;
    private float x = 0, y = 0;
    private float density = 0;
    private float friction = 0;
    private boolean isSensor;
    private float shapeHeight = 0;
    private float shapeWidth = 0;
    private float gravityScale = 1;
    private float linearDamping = 0;


    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
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

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getShapeHeight() {
        return shapeHeight;
    }

    public void setShapeHeight(float shapeHeight) {
        this.shapeHeight = shapeHeight;
    }

    public float getShapeWidth() {
        return shapeWidth;
    }

    public void setShapeWidth(float shapeWidth) {
        this.shapeWidth = shapeWidth;
    }

    public boolean isSensor() {
        return isSensor;
    }

    public void setSensor(boolean sensor) {
        isSensor = sensor;
    }

    public float getGravityScale() {
        return gravityScale;
    }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }
}
