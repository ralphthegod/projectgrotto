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

public abstract class InputSystem extends System{

    protected final Queue<InputEvent> inputEvents = new LinkedList<>();

    protected InputSystem(GameWorld gameWorld, List<Class<? extends Component>> requiredComponents) {
        super(gameWorld, requiredComponents, true);
    }

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

    protected abstract void getInputEvents();

    protected abstract void handleInput(Entity e, InputEvent inputEvent);

}
