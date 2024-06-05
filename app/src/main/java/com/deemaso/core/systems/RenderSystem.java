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

/**
 * Represents a system that renders entities.
 */
public abstract class RenderSystem extends System {

    protected final Box physicalSize, screenSize, currentView;

    /**
     * Creates a new render system with the given physical and screen sizes.
     * @param gameWorld The game world to use
     * @param physicalSize The physical size of the game world
     * @param screenSize The screen size of the game world
     */
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

    /**
     * Creates a new render system with the given physical and screen sizes.
     * @param gameWorld The game world to use
     * @param requiredComponents The required components
     * @param physicalSize The physical size of the game world
     * @param screenSize The screen size of the game world
     */
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

    /**
     * Updates the render system.
     * @param deltaTime The time since the last update
     */
    public void update(float deltaTime) {
        for(Entity e : entities) {
            draw(e, deltaTime);
        }
        super.update(deltaTime);
    }

    /**
     * Sorts the entities by z-index.
     */
    protected abstract void sortEntitiesByZIndex();

    /**
     * Draws an entity.
     * @param entity The entity to draw
     * @param deltaTime The time since the last draw
     * @return True if the entity was drawn, false otherwise
     */
    public abstract boolean draw(
            Entity entity,
            float deltaTime
    );

}
