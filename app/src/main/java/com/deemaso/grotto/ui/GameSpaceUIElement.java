package com.deemaso.grotto.ui;

import android.graphics.Canvas;

import com.deemaso.core.Box;
import com.deemaso.core.Entity;
import com.deemaso.core.components.TransformComponent;
import com.deemaso.grotto.components.PhysicsComponent;
import com.deemaso.grotto.utils.RenderUtils;

/**
 * A UI element for Project Grotto.
 * This class represents a UI element in the game space.
 */
public abstract class GameSpaceUIElement extends UIElement{

    protected final Entity entity;

    /**
     * Creates a new game space UI element.
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     * @param entity The entity
     */
    public GameSpaceUIElement(float x, float y, float width, float height, Entity entity) {
        super(x, y, width, height);
        this.entity = entity;
    }

    @Override
    public void draw(float screenX, float screenY) {
        super.draw(screenX, screenY);
    }

    /**
     * Get the associated entity.
     * */
    public Entity getEntity() {
        return entity;
    }
}
