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

/**
 * A level progression system for Project Grotto.
 * This system handles the progression of characters in the game (Entities with CharacterStatsComponent).
 */
public class CharacterStatsSystem extends System{

    /**
     * Creates a new level progression system.
     * @param gameWorld The game world
     */
    public CharacterStatsSystem(GameWorld gameWorld) {
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
                    Progression prog = new Progression(playerEntity, lootEntity.getComponent(LootComponent.class).getValue(), lootEntity.getComponent(LootComponent.class).getStat());
                    if(lootEntity.getComponent(LootComponent.class).isRemoveAfterCollecting()){
                        gameWorld.markEntityForDeletion(lootEntity);
                    }
                    progressions.add(prog);
                }
                break;

            case "COMBAT":
                Entity winner = (Entity) event.get("winner");
                Entity loser = (Entity) event.get("loser");
                if(winner != null && winner.hasComponent(CharacterStatsComponent.class)){
                    Progression prog = new Progression(winner, (int) loser.getComponent(CharacterStatsComponent.class).getStat("experience"), "experience");
                    progressions.add(prog);
                }
                loser.getComponent(CharacterStatsComponent.class).setAlive(false);
                break;
        }
    }

    /**
     * A progression object. <br>
     * This object includes the entity to progress, the value of the progression and the stat to progress.
     */
    private static class Progression {
        private final Entity entity;
        private final int value;
        private final String stat;

        /**
         * Creates a new progression object.
         * @param entity The entity to progress
         * @param value The value of the progression
         * @param stat The stat to progress
         */
        public Progression(Entity entity, int value, String stat) {
            this.entity = entity;
            this.value = value;
            this.stat = stat;
        }

        public Entity getEntity() {
            return entity;
        }

        public int getValue() {
            return value;
        }

        public String getStat() { return stat; }
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
        if (progression != null) {
            CharacterStatsComponent characterStatsComponent = progression.getEntity().getComponent(CharacterStatsComponent.class);
            String stat = progression.getStat();
            int newValue = (int)characterStatsComponent.getStat(stat) + progression.getValue();

            String maxStat = "max" + Character.toUpperCase(stat.charAt(0)) + stat.substring(1);
            if (characterStatsComponent.hasStat(maxStat)) {
                int maxStatValue = (int) characterStatsComponent.getStat(maxStat);
                newValue = Math.min(newValue, maxStatValue);
            }

            characterStatsComponent.setStat(stat, newValue);
            Log.d("LevelProgressionSystem", "Entity " + progression.getEntity().getId() + " got " + progression.getValue() + " " + progression.getStat() + ".");

            SystemEvent event = new SystemEvent("STAT_UPDATED");
            event.put("entity", progression.getEntity());
            event.put("stat", progression.getStat());
            event.put("value", newValue);
            gameWorld.broadcastEvent(event);
        }

        // Check for level up
        for(Entity entity : entities){
            CharacterStatsComponent characterStatsComponent = entity.getComponent(CharacterStatsComponent.class);
            if((int) characterStatsComponent.getStat("experience") >= 10){
                characterStatsComponent.setStat("level", (int)characterStatsComponent.getStat("level") + 1);
                characterStatsComponent.setStat("experience", (int) characterStatsComponent.getStat("experience") - 10);
                Log.d("LevelProgressionSystem", "Entity " + entity.getId() + " leveled up to " + characterStatsComponent.getStat("level") + ".");

                // Emit level up event (e.g. for UI ...)
                SystemEvent event = new SystemEvent("LEVEL_UP");
                event.put("entity", entity);
                event.put("level", characterStatsComponent.getStat("level"));
                gameWorld.broadcastEvent(event);

                SystemEvent event2 = new SystemEvent("STAT_UPDATED");
                event2.put("entity", entity);
                event2.put("stat", "experience");
                event2.put("value", characterStatsComponent.getStat("experience"));
                gameWorld.broadcastEvent(event2);
            }

        }
    }

    @Override
    public boolean registerEntity(Entity entity) {
        if(super.registerEntity(entity)){
            CharacterStatsComponent characterStatsComponent = entity.getComponent(CharacterStatsComponent.class);
            characterStatsComponent.setStat("health", characterStatsComponent.getStat("maxHealth"));
            SystemEvent event = new SystemEvent("STAT_UPDATED");
            event.put("entity", entity);
            event.put("stat", "health");
            event.put("value", characterStatsComponent.getStat("maxHealth"));
            gameWorld.broadcastEvent(event);
            return true;
        }
        return false;
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
