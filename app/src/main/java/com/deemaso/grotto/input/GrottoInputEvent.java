package com.deemaso.grotto.input;

import com.deemaso.core.input.InputEvent;

public class GrottoInputEvent extends InputEvent {

    private InputEventType type;

    public GrottoInputEvent(InputEventType type) {
        this.type = type;
    }

    public InputEventType getType() {
        return type;
    }

    public void setType(InputEventType type) {
        this.type = type;
    }

}
