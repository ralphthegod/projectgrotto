package com.deemaso.grotto.systems;

import android.util.Log;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.components.InputComponent;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.core.input.InputEvent;
import com.deemaso.core.systems.InputSystem;
import com.deemaso.grotto.GrottoGameWorld;
import com.deemaso.grotto.input.GrottoInputEvent;
import com.deemaso.grotto.input.InputEventType;

import java.util.Arrays;
import java.util.List;

/**
 * An input system for Project Grotto.
 */
public class GrottoInputSystem extends InputSystem {

    private final TouchHandler touchHandler;

    /**
     * Creates a new input system.
     * @param gameWorld The game world
     * @param touchHandler The touch handler
     */
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
                SystemEvent event = new SystemEvent("ATTACK");
                if(e != null){
                    event.put("attacker", e);
                    gameWorld.broadcastEvent(event);
                }
                else{
                    ((GrottoGameWorld) gameWorld).restart(null);
                }
                break;
            case TOUCH_UP:
                Log.d("GrottoInputSystem", "Touch Up");
                SystemEvent event2 = new SystemEvent("INPUT_TOUCH_UP");
                gameWorld.broadcastEvent(event2);
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

    @Override
    public void onEvent(SystemEvent event) {
        // Handle events
    }
}
