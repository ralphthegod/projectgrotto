package com.deemaso.core.systems;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.components.Component;
import com.deemaso.core.components.InputComponent;
import com.deemaso.core.input.InputEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Represents a system that handles input events.
 */
public abstract class InputSystem extends System{

    protected final Queue<InputEvent> inputEvents = new LinkedList<>();

    /**
     * Creates a new input system with the given required components.
     * @param gameWorld The game world to use
     * @param requiredComponents The required components
     */
    protected InputSystem(GameWorld gameWorld, List<Class<? extends Component>> requiredComponents) {
        super(gameWorld, requiredComponents, true);
    }

    /**
     * Creates a new input system with the given game world.
     * @param gameWorld The game world to use
     */
    protected InputSystem(GameWorld gameWorld) {
        super(gameWorld, Arrays.asList(InputComponent.class), true);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        getInputEvents();
        for(Entity e : entities) {
            InputEvent inputEvent = inputEvents.poll();
            if(inputEvent != null) {
                handleInput(e, inputEvent);
            }
        }
        if(entities.isEmpty()){
            InputEvent inputEvent = inputEvents.poll();
            if(inputEvent != null) {
                handleInput(null, inputEvent);
            }
        }
        inputEvents.clear();
    }

    /**
     * Gets the input events for the system.
     */
    protected abstract void getInputEvents();

    /**
     * Handles an input event for an entity.
     * @param e The entity to handle the input event for
     * @param inputEvent The input event to handle
     */
    protected abstract void handleInput(Entity e, InputEvent inputEvent);

}
