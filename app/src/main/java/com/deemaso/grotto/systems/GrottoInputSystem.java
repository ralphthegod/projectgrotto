package com.deemaso.grotto.systems;

import android.util.Log;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.components.InputComponent;
import com.deemaso.core.input.InputEvent;
import com.deemaso.core.systems.InputSystem;
import com.deemaso.grotto.input.GrottoInputEvent;
import com.deemaso.grotto.input.InputEventType;

import java.util.Arrays;
import java.util.List;

public class GrottoInputSystem extends InputSystem {

    private final TouchHandler touchHandler;

    public GrottoInputSystem(GameWorld gameWorld, TouchHandler touchHandler) {
        super(gameWorld, Arrays.asList(InputComponent.class));
        this.touchHandler = touchHandler;
    }

    @Override
    protected void getInputEvents() {
        List<Input.TouchEvent> touchEvents = touchHandler.getTouchEvents();
        for(Input.TouchEvent touchEvent : touchEvents) {
            switch (touchEvent.type) {
                case Input.TouchEvent.TOUCH_DOWN:
                    inputEvents.add(new GrottoInputEvent(InputEventType.TOUCH_DOWN));
                    break;
                case Input.TouchEvent.TOUCH_UP:
                    inputEvents.add(new GrottoInputEvent(InputEventType.TOUCH_UP));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void handleInput(Entity e, InputEvent inputEvent) {
        switch (((GrottoInputEvent) inputEvent).getType()){
            case TOUCH_DOWN:
                Log.d("GrottoInputSystem", "Touch Down");
                break;
            case TOUCH_UP:
                Log.d("GrottoInputSystem", "Touch Up");
                break;
            default:
                break;
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
}
