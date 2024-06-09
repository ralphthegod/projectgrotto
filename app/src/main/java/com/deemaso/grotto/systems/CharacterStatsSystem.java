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
import com.deemaso.grotto.components.SoundComponent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A level progression system for Project Grotto.
 * This system handles the progression of characters in the game (Entities with CharacterStatsComponent).
 */
public class CharacterStatsSystem extends System{

    private enum StatOperation {
        ADD,
        SET
    }

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
                        (entity1.hasComponent(PlayerComponent.class) &&
                                (entity2.hasComponent(LootComponent.class) && !entity2.hasComponent(CharacterStatsComponent.class)))
                            ||
                            (entity2.hasComponent(PlayerComponent.class) &&
                                (entity1.hasComponent(LootComponent.class) && !entity1.hasComponent(CharacterStatsComponent.class)))
                ){
                    Entity lootEntity = entity1.hasComponent(LootComponent.class) ?
                            entity1 : entity2;
                    Entity playerEntity = entity1.hasComponent(PlayerComponent.class) ?
                            entity1 : entity2;
                    Progression prog = new Progression(
                            playerEntity, lootEntity.getComponent(LootComponent.class).getValue(),
                            lootEntity.getComponent(LootComponent.class).getStat()
                    );
                    if(lootEntity.getComponent(LootComponent.class).isRemoveAfterCollecting()){
                        gameWorld.markEntityForDeletion(lootEntity);
                    }
                    if(lootEntity.hasComponent(SoundComponent.class)){
                        SystemEvent soundEvent = new SystemEvent("SOUND");
                        soundEvent.put("sound", lootEntity.getComponent(SoundComponent.class).getSounds().get("pickup"));
                        gameWorld.broadcastEvent(soundEvent);
                    }
                    progressions.add(prog);
                }
                break;

            case "COMBAT_END":
                Entity winner = (Entity) event.get("winner");
                Entity loser = (Entity) event.get("loser");
                if(winner != null && winner.hasComponent(CharacterStatsComponent.class) && loser.hasComponent(LootComponent.class)){
                    Progression prog = new Progression(winner, (loser.getComponent(LootComponent.class)).getValue(), (loser.getComponent(LootComponent.class)).getStat());
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
                if(entity.hasComponent(SoundComponent.class)){
                    SystemEvent soundEvent = new SystemEvent("SOUND");
                    soundEvent.put("sound", entity.getComponent(SoundComponent.class).getSounds().get("death"));
                    gameWorld.broadcastEvent(soundEvent);
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
                Log.d("CharacterStatsSystem", "Limiting max on stat " + maxStat + "with " +newValue);
            }

            updateStat(progression.getEntity(), stat, newValue, StatOperation.SET);
            Log.d("LevelProgressionSystem", "Entity " + progression.getEntity().getId() + " got " + progression.getValue() + " " + progression.getStat() + ".");
        }

        // Check for level up
        for(Entity entity : entities){
            CharacterStatsComponent characterStatsComponent = entity.getComponent(CharacterStatsComponent.class);
            if((int) characterStatsComponent.getStat("experience") >= 10){
                updateStat(entity, "level", 1, StatOperation.ADD);
                updateStat(entity, "experience", -10, StatOperation.ADD);
                int newLevel = (int) characterStatsComponent.getStat("level");
                Log.d("LevelProgressionSystem", "Entity " + entity.getId() + " leveled up to " + characterStatsComponent.getStat("level") + ".");

                if (characterStatsComponent.hasStat("maxHealth")) {
                    int currentMaxHealth = (int) characterStatsComponent.getStat("maxHealth");
                    int currentLevel = newLevel - 1;
                    double baseMaxHealth = currentMaxHealth / Math.pow(1.05, currentLevel);
                    int newMaxHealth = (int) (baseMaxHealth * Math.pow(1.05, newLevel));
                    updateStat(entity, "maxHealth", newMaxHealth, StatOperation.SET);
                    Log.d("LevelProgressionSystem", "Entity " + entity.getId() + " max health increased to " + newMaxHealth + ".");
                }

                if(entity.hasComponent(PlayerComponent.class)) {
                    SystemEvent event = new SystemEvent("SOUND");
                    SoundComponent soundComponent = entity.getComponent(SoundComponent.class);
                    event.put("sound", soundComponent.getSounds().get("level_up"));
                    gameWorld.broadcastEvent(event);
                }
            }
        }
    }

    private void updateStat(Entity entity, String stat, int value, StatOperation operation){
        CharacterStatsComponent characterStatsComponent = entity.getComponent(CharacterStatsComponent.class);
        int newValue = (int)characterStatsComponent.getStat(stat);
        switch(operation){
            case ADD:
                newValue += value;
                break;
            case SET:
                newValue = value;
                break;
        }
        characterStatsComponent.setStat(stat, newValue);
        SystemEvent event = new SystemEvent("STAT_UPDATED");
        event.put("entity", entity);
        event.put("stat", stat);
        event.put("value", newValue);
        gameWorld.broadcastEvent(event);
    }

    @Override
    public boolean registerEntity(Entity entity) {
        if(super.registerEntity(entity)){
            CharacterStatsComponent characterStatsComponent = entity.getComponent(CharacterStatsComponent.class);
            updateStat(entity, "health", (int) characterStatsComponent.getStat("maxHealth"), StatOperation.SET);
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
