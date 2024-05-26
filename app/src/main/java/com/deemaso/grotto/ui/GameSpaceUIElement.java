package com.deemaso.grotto.ui;

import android.graphics.Canvas;

import com.deemaso.core.Box;
import com.deemaso.core.Entity;
import com.deemaso.core.components.TransformComponent;
import com.deemaso.grotto.components.PhysicsComponent;
import com.deemaso.grotto.utils.RenderUtils;

public abstract class GameSpaceUIElement extends UIElement{

    protected final Entity entity;

    public GameSpaceUIElement(float x, float y, float width, float height, Entity entity) {
        super(x, y, width, height);
        this.entity = entity;
    }

    @Override
    public void draw(float screenX, float screenY) {
        super.draw(screenX, screenY);
    }

    public Entity getEntity() {
        return entity;
    }
}
