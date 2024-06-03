package com.deemaso.grotto.systems;

import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.core.systems.System;
import com.deemaso.grotto.components.LimitedLifeComponent;

import java.util.Arrays;

public class TimeSystem extends System {

    public TimeSystem(GameWorld gw) {
        super(gw, Arrays.asList(LimitedLifeComponent.class), true);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        for(Entity entity : entities){
            LimitedLifeComponent life = entity.getComponent(LimitedLifeComponent.class);
            life.setTimeAlive(life.getTimeAlive() + dt);
            if(life.getTimeAlive() >= life.getLifeTime()){
                gameWorld.markEntityForDeletion(entity);
            }
        }
    }

    @Override
    public void onEvent(SystemEvent event) {

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
}
