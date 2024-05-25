package com.deemaso.grotto.components;

import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.impl.AndroidAudio;
import com.deemaso.core.components.Component;

public class MusicComponent extends Component {
    private Music music;
    private String path;

    public MusicComponent(Music music) {
        this.music = music;
    }

    public MusicComponent(String path) {
        this.path = path;
    }


    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
