package com.deemaso.core.systems;

import android.util.Log;

import com.deemaso.core.Box;
import com.deemaso.core.GameWorld;
import com.deemaso.core.components.Component;
import com.deemaso.core.components.RenderComponent;
import com.deemaso.core.Entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
    * Manages rendering of entities.
    * This class is abstract and must be subclassed to implement the draw method.
 */
public abstract class RenderSystem extends System {

    protected final Box physicalSize, screenSize, currentView;

    public RenderSystem(
            GameWorld gameWorld,
            Box physicalSize,
            Box screenSize
    ) {
        super(gameWorld, Arrays.asList(RenderComponent.class), true);
        this.physicalSize = physicalSize;
        this.screenSize = screenSize;
        this.currentView = physicalSize;
    }

    protected RenderSystem(
        GameWorld gameWorld,
        List<Class<? extends Component>> requiredComponents,
        Box physicalSize,
        Box screenSize
    ) {
        super(gameWorld, requiredComponents, true);
        this.physicalSize = physicalSize;
        this.screenSize = screenSize;
        this.currentView = physicalSize;
    }

    public void update(float deltaTime) {
        for(Entity e : entities) {
            draw(e, deltaTime);
        }
        super.update(deltaTime);
    }

    protected abstract void sortEntitiesByZIndex();

    public abstract boolean draw(
            Entity entity,
            float deltaTime
    );

}
