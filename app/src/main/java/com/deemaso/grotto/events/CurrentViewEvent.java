package com.deemaso.grotto.events;

import com.deemaso.core.Box;
import com.deemaso.core.events.Event;

public class CurrentViewEvent implements Event {
    private final Box currentView;

    public CurrentViewEvent(Box currentView) {
        this.currentView = currentView;
    }

    public Box getCurrentView() {
        return currentView;
    }
}
