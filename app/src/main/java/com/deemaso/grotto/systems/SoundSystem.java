package com.deemaso.grotto.systems;

import android.util.Log;

import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.Sound;
import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.events.EventListener;
import com.deemaso.core.systems.System;
import com.deemaso.grotto.components.MusicComponent;
import com.deemaso.grotto.components.SoundComponent;
import com.deemaso.grotto.events.SoundEvent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class SoundSystem extends System implements EventListener<SoundEvent> {

    private long timeOfLastSound = 0;
    private Music backgroundMusic;
    private final Queue<Sound> soundQueue = new LinkedList<>();

    public SoundSystem(GameWorld gameWorld) {
        super(gameWorld, Arrays.asList(SoundComponent.class, MusicComponent.class), false);
       // audio = new AndroidAudio(((GrottoGameWorld) gameWorld).getActivity());
    }

    @Override
    public void update(float dt) {
        if(backgroundMusic == null){
            for (Entity entity : entities) {
                if(entity.hasComponent(MusicComponent.class)){
                    MusicComponent musicComponent = entity.getComponent(MusicComponent.class);
                    backgroundMusic = musicComponent.getMusic();
                    backgroundMusic.play();
                    backgroundMusic.setLooping(true);
                    Log.d("SoundSystem", "Playing background music");
                }
            }
        }
        Sound nextSound = soundQueue.poll();
        if(nextSound != null){
            if(java.lang.System.nanoTime() > timeOfLastSound + 500_000_000){
                nextSound.play(0.7f);
                timeOfLastSound = java.lang.System.nanoTime();
            }
            else{
                soundQueue.add(nextSound);
            }
        }
    }

    @Override
    protected void finalize() {

    }

    @Override
    public void pause() {
        if(backgroundMusic != null){
            backgroundMusic.pause();
        }
    }

    @Override
    public void resume() {
        if(backgroundMusic != null){
            backgroundMusic.play();
        }
    }

    @Override
    public void onEventReceived(SoundEvent event) {
        soundQueue.add(event.getSound());
    }
}
