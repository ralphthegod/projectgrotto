package com.deemaso.grotto.systems;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.components.Component;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.core.systems.System;
import com.deemaso.grotto.ai.AIContext;
import com.deemaso.grotto.components.AIComponent;

import java.util.Arrays;
import java.util.List;

/**
 * An AI system.
 */
public class AISystem extends System {

    /**
     * Creates a new AI system.
     * @param gameWorld The game world
     * @param requiredComponents The required components
     * @param requireAllComponents Whether all components are required
     */
    protected AISystem(GameWorld gameWorld, List<Class<? extends Component>> requiredComponents, boolean requireAllComponents) {
        super(gameWorld, requiredComponents, requireAllComponents);
    }

    /**
     * Creates a new AI system.
     * @param gameWorld The game world
     */
    public AISystem(GameWorld gameWorld){
        super(gameWorld, Arrays.asList(AIComponent.class), true);
    }

    @Override
    public void update(float dt) {
        for (Entity entity : entities) {
            AIComponent aiComponent = entity.getComponent(AIComponent.class);
            if (aiComponent != null) {
                AIContext context = aiComponent.getContext();
                context.put("gameWorld", gameWorld);
                context.put("self", entity);
                aiComponent.getDecisionTree().execute(context);
            }
        }
    }

    @Override
    protected void finalize() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void onEvent(SystemEvent event) {

    }
}