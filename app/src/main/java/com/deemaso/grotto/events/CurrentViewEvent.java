package com.deemaso.grotto.events;

import com.deemaso.core.Box;

public class CurrentViewEvent {
    private final Box currentView;

    public CurrentViewEvent(Box currentView) {
        this.currentView = currentView;
    }

    public Box getCurrentView() {
        return currentView;
    }
}
