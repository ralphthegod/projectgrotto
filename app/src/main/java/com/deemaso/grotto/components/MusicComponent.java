package com.deemaso.grotto.components;

import com.badlogic.androidgames.framework.Music;
import com.deemaso.core.components.Component;

public class MusicComponent extends Component {
    private Music music;

    public MusicComponent(Music music) {
        this.music = music;
    }

    public MusicComponent() {
    }


    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }
}
