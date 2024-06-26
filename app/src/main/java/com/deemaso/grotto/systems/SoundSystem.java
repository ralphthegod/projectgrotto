package com.deemaso.grotto.systems;

import android.util.Log;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.Sound;
import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.core.systems.System;
import com.deemaso.grotto.components.MusicComponent;
import com.deemaso.grotto.components.SoundComponent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A sound system for Project Grotto.
 * This system handles the playing of sounds in the game.
 */
public class SoundSystem extends System{

    private long timeOfLastSound = 0;
    private Music backgroundMusic;
    private Audio audio;
    private final Queue<Sound> soundQueue = new LinkedList<>();

    /**
     * Creates a new sound system.
     * @param gameWorld The game world
     * @param audio The audio object
     */
    public SoundSystem(GameWorld gameWorld, Audio audio) {
        super(gameWorld, Arrays.asList(SoundComponent.class, MusicComponent.class), false);
        this.audio = audio;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if(backgroundMusic == null){
            for (Entity entity : entities) {
                if(entity.hasComponent(MusicComponent.class)){
                    MusicComponent musicComponent = entity.getComponent(MusicComponent.class);
                    if(musicComponent.getMusic() != null){
                        backgroundMusic = musicComponent.getMusic();
                    }
                    else{
                        backgroundMusic = audio.newMusic(musicComponent.getPath());
                        Log.d("SoundSystem", "Playing music: " + musicComponent.getPath());
                    }
                    backgroundMusic.setLooping(true);
                    backgroundMusic.play();
                }
            }
        }
        Sound nextSound = soundQueue.poll();
        if(nextSound != null){
            if(java.lang.System.nanoTime() > timeOfLastSound + 1_000_000){
                nextSound.play(0.7f);
                timeOfLastSound = java.lang.System.nanoTime();
            }
            else{
                soundQueue.add(nextSound);
            }
        }
    }

    @Override
    public boolean registerEntity(Entity entity) {
        if(!super.registerEntity(entity)) return false;
        if(entity.hasComponent(MusicComponent.class)){
            MusicComponent musicComponent = entity.getComponent(MusicComponent.class);
            if(musicComponent.getMusic() == null){
                musicComponent.setMusic(audio.newMusic(musicComponent.getPath()));
            }
        }
        else if(entity.hasComponent(SoundComponent.class)){
            SoundComponent soundComponent = entity.getComponent(SoundComponent.class);
            for(String path : soundComponent.getPaths().keySet()){
                try{
                    Sound sound = audio.newSound(soundComponent.getPaths().get(path));
                    soundComponent.addSound(path, sound);
                }
                catch (Exception e){
                    Log.e("SoundSystem", "Failed to load sound: " + soundComponent.getPaths().get(path));
                    return false;
                }
            }
        }
        else{
            return false;
        }
        return true;
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
    public void onEvent(SystemEvent event) {
        if(event.getCode().equals("SOUND")){
            Sound sound = (Sound) event.get("sound");
            soundQueue.add(sound);
        }
    }
}
