package com.deemaso.grotto.events;

import com.badlogic.androidgames.framework.Sound;
import com.deemaso.core.events.Event;

public class SoundEvent implements Event {
    private final Sound sound;

    public SoundEvent(Sound sound) {
        this.sound = sound;
    }

    public Sound getSound() {
        return sound;
    }
}
