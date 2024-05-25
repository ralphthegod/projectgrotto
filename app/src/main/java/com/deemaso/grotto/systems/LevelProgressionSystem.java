package com.deemaso.grotto.systems;

import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.events.EventListener;
import com.deemaso.core.systems.System;
import com.deemaso.grotto.components.CharLevelComponent;
import com.deemaso.grotto.components.LootComponent;
import com.deemaso.grotto.components.PlayerComponent;
import com.deemaso.grotto.events.CollisionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LevelProgressionSystem extends System implements EventListener<CollisionEvent>{ // Need another event listener for killing enemies
    public LevelProgressionSystem(GameWorld gameWorld) {
        super(gameWorld, Arrays.asList(CharLevelComponent.class), true);
    }

    private final Queue<Progression> progressions = new LinkedList<>();

    private static class Progression {
        private final Entity entity;
        private final int experience;

        public Progression(Entity entity, int experience) {
            this.entity = entity;
            this.experience = experience;
        }

        public Entity getEntity() {
            return entity;
        }

        public int getExperience() {
            return experience;
        }
    }

    @Override
    public void update(float dt) {
        Progression progression = progressions.poll();
        // Consume a progression per update (frame)
        if(progression != null){
            CharLevelComponent charLevelComponent = progression.getEntity().getComponent(CharLevelComponent.class);
            charLevelComponent.addExperience(progression.getExperience());
            Log.d("LevelProgressionSystem", "Entity " + progression.getEntity().getId() + " gained " + progression.getExperience() + " experience.");
        }
        // Check for level up
        for(Entity entity : entities){
            CharLevelComponent charLevelComponent = entity.getComponent(CharLevelComponent.class);
            if(charLevelComponent.getExperience() >= 10){
                charLevelComponent.setLevel(charLevelComponent.getLevel() + 1);
                charLevelComponent.setExperience(charLevelComponent.getExperience() - 10);
                Log.d("LevelProgressionSystem", "Entity " + entity.getId() + " leveled up to " + charLevelComponent.getLevel() + ".");
                // Emit level up event (e.g. for UI ...)
            }
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
    public void onEventReceived(CollisionEvent event) {
        // Check if player collided with loot
        if(
                event.getCollision().getA().hasComponent(PlayerComponent.class) &&
                        event.getCollision().getB().hasComponent(LootComponent.class)
                ||
                event.getCollision().getB().hasComponent(PlayerComponent.class) &&
                        event.getCollision().getA().hasComponent(LootComponent.class)
        ){
            Entity lootEntity = event.getCollision().getA().hasComponent(LootComponent.class) ?
                    event.getCollision().getA() : event.getCollision().getB();
            Entity playerEntity = event.getCollision().getA().hasComponent(PlayerComponent.class) ?
                    event.getCollision().getA() : event.getCollision().getB();
            Progression prog = new Progression(playerEntity, lootEntity.getComponent(LootComponent.class).getValue());
            gameWorld.markEntityForDeletion(lootEntity);
            progressions.add(prog);
        }
    }
}
