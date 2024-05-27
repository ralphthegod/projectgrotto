package com.deemaso.grotto.systems;

import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.collisions.Collision;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.core.systems.System;
import com.deemaso.grotto.components.CharacterStatsComponent;
import com.deemaso.grotto.components.LootComponent;
import com.deemaso.grotto.components.PlayerComponent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class LevelProgressionSystem extends System{
    public LevelProgressionSystem(GameWorld gameWorld) {
        super(gameWorld, Arrays.asList(CharacterStatsComponent.class), true);
    }

    private final Queue<Progression> progressions = new LinkedList<>();

    @Override
    public void onEvent(SystemEvent event) {
        switch(event.getCode()){

            case "COLLISION":
                Collision collision = (Collision) event.get("collision");
                Entity entity1 = collision.getA();
                Entity entity2 = collision.getB();
                if(
                        entity1.hasComponent(PlayerComponent.class) &&
                                entity2.hasComponent(LootComponent.class)
                            ||
                        entity2.hasComponent(PlayerComponent.class) &&
                                entity1.hasComponent(LootComponent.class)
                ){
                    Entity lootEntity = entity1.hasComponent(LootComponent.class) ?
                            entity1 : entity2;
                    Entity playerEntity = entity1.hasComponent(PlayerComponent.class) ?
                            entity1 : entity2;
                    Progression prog = new Progression(playerEntity, lootEntity.getComponent(LootComponent.class).getValue());
                    if(lootEntity.getComponent(LootComponent.class).isRemoveAfterCollecting()){
                        gameWorld.markEntityForDeletion(lootEntity);
                    }
                    progressions.add(prog);
                }
                break;

            case "COMBAT":
                Entity winner = (Entity) event.get("winner");
                Entity loser = (Entity) event.get("loser");
                Progression prog = new Progression(winner, loser.getComponent(CharacterStatsComponent.class).getExperience());
                progressions.add(prog);
                loser.getComponent(CharacterStatsComponent.class).setAlive(false);
                break;
        }
    }


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
        super.update(dt);
        processProgressions();
        processDeaths();
    }

    private void processDeaths(){
        // Check for dead entities
        for(Entity entity : entities){
            CharacterStatsComponent characterStatsComponent = entity.getComponent(CharacterStatsComponent.class);
            if(!characterStatsComponent.isAlive()){
                Log.d("LevelProgressionSystem", "Entity " + entity.getId() + " died.");
                if(entity.hasComponent(PlayerComponent.class))
                    gameWorld.broadcastEvent(new SystemEvent("PLAYER_DIED"));
                else{
                    SystemEvent event = new SystemEvent("ENTITY_DIED");
                    event.put("entity", entity);
                    gameWorld.broadcastEvent(event);
                }
                gameWorld.markEntityForDeletion(entity);
            }
        }
    }

    private void processProgressions(){
        Progression progression = progressions.poll();
        // Consume a progression per update (frame)
        if(progression != null){
            CharacterStatsComponent characterStatsComponent = progression.getEntity().getComponent(CharacterStatsComponent.class);
            characterStatsComponent.addExperience(progression.getExperience());
            SystemEvent event = new SystemEvent("EXPERIENCE_UPDATED");
            event.put("entity", progression.getEntity());
            event.put("experience", characterStatsComponent.getExperience());
            gameWorld.broadcastEvent(event);
            if(progression.entity.hasComponent(PlayerComponent.class)){
                SystemEvent playerEvent = new SystemEvent("PLAYER_EXPERIENCE_UPDATED");
                playerEvent.put("experience", characterStatsComponent.getExperience());
                gameWorld.broadcastEvent(playerEvent);
            }
            Log.d("LevelProgressionSystem", "Entity " + progression.getEntity().getId() + " got" + progression.getExperience() + " experience.");
        }

        // Check for level up
        for(Entity entity : entities){
            CharacterStatsComponent characterStatsComponent = entity.getComponent(CharacterStatsComponent.class);
            if(characterStatsComponent.getExperience() >= 10){
                characterStatsComponent.setLevel(characterStatsComponent.getLevel() + 1);
                characterStatsComponent.setExperience(characterStatsComponent.getExperience() - 10);
                Log.d("LevelProgressionSystem", "Entity " + entity.getId() + " leveled up to " + characterStatsComponent.getLevel() + ".");

                // Emit level up event (e.g. for UI ...)
                SystemEvent event = new SystemEvent("LEVEL_UP");
                event.put("entity", entity);
                event.put("level", characterStatsComponent.getLevel());
                gameWorld.broadcastEvent(event);

                SystemEvent event2 = new SystemEvent("EXPERIENCE_UPDATED");
                event2.put("entity", entity);
                event2.put("experience", characterStatsComponent.getExperience());
                gameWorld.broadcastEvent(event2);
                if(entity.hasComponent(PlayerComponent.class)){
                    SystemEvent playerEvent = new SystemEvent("PLAYER_EXPERIENCE_UPDATED");
                    playerEvent.put("experience", characterStatsComponent.getExperience());
                    gameWorld.broadcastEvent(playerEvent);
                }
            }
            else if(characterStatsComponent.getExperience() < 0){
                characterStatsComponent.setExperience(10 + characterStatsComponent.getExperience());
                characterStatsComponent.setLevel(characterStatsComponent.getLevel() - 1);
                Log.d("LevelProgressionSystem", "Entity " + entity.getId() + " lost a level.");
                // Emit level down event (e.g. for UI ...)
                SystemEvent event = new SystemEvent("LEVEL_DOWN");
                event.put("entity", entity);
                event.put("level", characterStatsComponent.getLevel());
                gameWorld.broadcastEvent(event);
                if(characterStatsComponent.getLevel() < 1){
                    characterStatsComponent.setAlive(false);
                }

                SystemEvent event2 = new SystemEvent("EXPERIENCE_UPDATED");
                event2.put("entity", entity);
                event2.put("experience", characterStatsComponent.getExperience());
                gameWorld.broadcastEvent(event2);
                if(entity.hasComponent(PlayerComponent.class)){
                    SystemEvent playerEvent = new SystemEvent("PLAYER_EXPERIENCE_UPDATED");
                    playerEvent.put("experience", characterStatsComponent.getExperience());
                    gameWorld.broadcastEvent(playerEvent);
                }
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

}
