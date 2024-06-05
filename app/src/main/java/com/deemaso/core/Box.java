package com.deemaso.core;

import androidx.annotation.NonNull;

/**
 * Represents a bounding box.
 */
public class Box {
    public float xmin, ymin, xmax, ymax, width, height;

    /**
     * Creates a new Box.
     * @param xmin The minimum x value
     * @param ymin The minimum y value
     * @param xmax The maximum x value
     * @param ymax The maximum y value
     */
    public Box(float xmin, float ymin, float xmax, float ymax)
    {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
        this.width = xmax - xmin;
        this.height = ymax - ymin;
    }

    @NonNull
    @Override
    public String toString() {
        return "Box{" +
                "xmin=" + xmin +
                ", ymin=" + ymin +
                ", xmax=" + xmax +
                ", ymax=" + ymax +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
