package com.deemaso.grotto.components;

import com.badlogic.androidgames.framework.Sound;
import com.deemaso.core.components.Component;

import java.util.HashMap;
import java.util.Map;

public class SoundComponent extends Component {
    private Map<String, Sound> sounds = new HashMap<>();
    private final Map<String, String> paths;

    public SoundComponent(Map<String, String> paths) {
        this.paths = paths;
    }

    public Map<String, Sound> getSounds() {
        return sounds;
    }

    public void setSounds(Map<String, Sound> sounds) {
        this.sounds = sounds;
    }

    public Map<String, String> getPaths() {
        return paths;
    }

    public void addSound(String name, Sound sound) {
        sounds.put(name, sound);
    }
}
