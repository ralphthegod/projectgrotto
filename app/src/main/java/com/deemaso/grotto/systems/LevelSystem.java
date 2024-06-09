package com.deemaso.grotto.systems;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.collisions.Collision;
import com.deemaso.core.components.Component;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.core.systems.System;
import com.deemaso.grotto.GrottoGameWorld;
import com.deemaso.grotto.components.CharacterStatsComponent;
import com.deemaso.grotto.components.EnemySpawnerComponent;
import com.deemaso.grotto.components.GrottoRenderComponent;
import com.deemaso.grotto.components.LevelDefinitionComponent;
import com.deemaso.grotto.components.PhysicsComponent;
import com.deemaso.grotto.components.PlayerComponent;
import com.deemaso.grotto.components.TileComponent;
import com.deemaso.grotto.levelgen.EnemyElementDefinition;
import com.deemaso.grotto.levelgen.LevelGenerationElementDefinition;
import com.deemaso.grotto.levelgen.LevelGenerationEngine;

import org.jbox2d.common.Vec2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *  The LevelSystem is responsible for generating the level based on the LevelDefinitionComponent
 *  of the level entity. The LevelDefinitionComponent contains the level generation element definitions
 *  that are used to generate the level. It relies on the LevelGenerationEngine to generate the
 *  level based on those definitions. Finally, the generated level is then converted to tiles and entities that
 *  are added to the game world.
 */
public class LevelSystem extends System{

    private final Map<String, Entity> tileMap;
    private final Map<String, Entity> entityMap;
    private boolean isGenerating = false;
    private boolean nextLevel = false;
    private final LevelGenerationEngine levelGenerationEngine;
    private int level = 0;
    private PlayerData playerData = null;
    private final Entity levelEntity;
    private final float tileSizeInPixels;

    public LevelSystem(
            GameWorld gameWorld,
            boolean requireAllComponents,
            String levelEntityArchetypeId,
            float tileSizeInPixels,
            PlayerData playerData
    ) {
        super(gameWorld, Arrays.asList(PlayerComponent.class), requireAllComponents);
        this.tileSizeInPixels = tileSizeInPixels;
        this.levelEntity = gameWorld.createEntityById(levelEntityArchetypeId);
        this.playerData = playerData;
        tileMap = new HashMap<>();
        entityMap = new HashMap<>();
        LevelDefinitionComponent levelDefinitionComponent = levelEntity.getComponent(LevelDefinitionComponent.class);
        this.levelGenerationEngine = new LevelGenerationEngine(
                levelDefinitionComponent.getGridWidth(),
                levelDefinitionComponent.getGridHeight(),
                levelDefinitionComponent.getMinRoomSize(),
                levelDefinitionComponent.getMaxRoomSize(),
                levelDefinitionComponent.getMaxRooms(),
                levelDefinitionComponent.getLevelGenerationElementDefinitions(),
                (int) (Math.random() * 1000000)
        );
    }

    /*
    * Used to transport player data during level transition
    * */
    public static class PlayerData implements Serializable {
        final private Map<String, Object> stats;
        final private int level;

        private PlayerData(
                Map<String, Object> stats,
                int level
        ) {
            this.stats = stats;
            this.level = level;
        }

        public Map<String, Object> getStats() {
            return stats;
        }

        public int getLevel() {
            return level;
        }

    }

    public void generateNextLevel(){
        CharacterStatsComponent pc = null;
        for(Entity e : entities){
            if(e.hasComponent(PlayerComponent.class)){
                Log.d("LevelSystem", "PlayerData found: loading stats...");
                pc = e.getComponent(CharacterStatsComponent.class);
            }
        }
        if(pc != null) playerData = new PlayerData(pc.getStats(), level++);
        ((GrottoGameWorld) gameWorld).restart(playerData);
    }

    @Override
    public void update(float dt) {
        if(nextLevel){
            generateNextLevel();
        }
        else if(checkLevelGeneration()){
            Log.d("LevelSystem", "Level generation finished.");
            Entity pc = null;
            for(Entity e : entities){
                if(e.hasComponent(PlayerComponent.class)){
                    Log.d("LevelSystem", "Saving player entity...");
                    pc = e;
                }
            }
        }
    }

    /**
     *  Checks if the level is being generated and if the tile map is empty. If the level is not being
     *  generated and the tile map is empty, then it generates the level.
     * */
    private boolean checkLevelGeneration(){
        if(!isGenerating && tileMap.isEmpty()) {
            boolean success = false;
            int attempts = 0;
            while (!success && attempts < 5) {
                try {
                    success = generate();
                } catch(Exception e) {
                    Log.e("LevelSystem", "Error generating level. Retrying...");
                    e.printStackTrace();
                    attempts++;
                }
            }
            return success;
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

    /**
     *  Adds a tile to the tile map.
     * */
    private void addTile(int x, int y, Entity tile) {
        String key = encodeCoordinates(x, y);
        tileMap.put(key, tile);
    }

    private Entity getTile(int x, int y) {
        String key = encodeCoordinates(x, y);
        return tileMap.get(key);
    }

    private void addEntity(int x, int y, Entity entity) {
        String key = encodeCoordinates(x, y);
        entityMap.put(key, entity);
    }

    private Entity getEntity(int x, int y) {
        String key = encodeCoordinates(x, y);
        return entityMap.get(key);
    }

    /**
     *  Encodes the coordinates to a string.
     * */
    private String encodeCoordinates(int x, int y) {
        return x + "," + y;
    }

    /**
    *  Converts the generated dungeon to tiles and entities that are added to the game world.
    * */
    private void generatedDungeonToTiles(char[][] level, Map<Character, LevelGenerationElementDefinition> levelGenerationElementDefinitions) {
        /*
         * An alternative would be to create a pattern detection system that would detect patterns
         * and replace them with the appropriate tiles. This would allow for more complex level
         * (e.g. side walls, corners, etc.)
         *  */
        for (int y = 0; y < level.length; y++) {
            for (int x = 0; x < level[y].length; x++) {
                char symbol = level[y][x];
                LevelGenerationElementDefinition def = levelGenerationElementDefinitions.get(symbol);
                if (def != null) {
                    LevelGenerationElementDefinition.TileArchetype tile = def.getWeightedRandomTileArchetype();
                    Entity tileEntity = gameWorld.createEntityById(tile.getArchetypeId());
                    if (tileEntity.hasComponent(TileComponent.class)) {
                        addTile(x, y, tileEntity);
                        addSurroundingTiles(x, y, tile);
                    } else {
                        addEntity(x, y, tileEntity);
                        addBaseTile(x, y, tile);
                    }
                    if(tileEntity.hasComponent(PlayerComponent.class) && playerData != null){
                        tileEntity.getComponent(CharacterStatsComponent.class).setStats(playerData.getStats());
                        SystemEvent event = new SystemEvent("PLAYER_LOADED");
                        event.put("entity", tileEntity);
                        gameWorld.broadcastEvent(event);
                    }
                }
            }
        }
    }

    /**
     * Adds the surrounding tiles of the given tile archetype.
     * */
    private void addSurroundingTiles(int x, int y, LevelGenerationElementDefinition.TileArchetype tile) {
        if (!tile.getBaseArchetypeId().equals("-1"))
            addTile(x, y, gameWorld.createEntityById(tile.getBaseArchetypeId()));
        if (!tile.getBottomArchetypeId().equals("-1"))
            addTile(x, y + 1, gameWorld.createEntityById(tile.getBottomArchetypeId()));
        if (!tile.getTopArchetypeId().equals("-1"))
            addTile(x, y - 1, gameWorld.createEntityById(tile.getTopArchetypeId()));
        if (!tile.getLeftArchetypeId().equals("-1"))
            addTile(x - 1, y, gameWorld.createEntityById(tile.getLeftArchetypeId()));
        if (!tile.getRightArchetypeId().equals("-1"))
            addTile(x + 1, y, gameWorld.createEntityById(tile.getRightArchetypeId()));
    }

    /**
     * Adds the base tile of the given tile archetype.
     * */
    private void addBaseTile(int x, int y, LevelGenerationElementDefinition.TileArchetype tile) {
        if (!tile.getBaseArchetypeId().equals("-1"))
            addTile(x, y, gameWorld.createEntityById(tile.getBaseArchetypeId()));
    }

    /**
     * Sets the position of the given tile entity.
     * */
    private void setTilesPosition(Entity tile, int x, int y) {
        if(tile.hasComponent(TileComponent.class)){
            TileComponent tileComponent = tile.getComponent(TileComponent.class);
            tileComponent.setEffectiveX(x * tileSizeInPixels);
            tileComponent.setEffectiveY(y * tileSizeInPixels);
        }
        if(tile.hasComponent(PhysicsComponent.class)){
            PhysicsComponent physicsComponent = tile.getComponent(PhysicsComponent.class);
            physicsComponent.getBody().setTransform(new Vec2(x*tileSizeInPixels, y*tileSizeInPixels), 0);
        }
        else if(tile.hasComponent(GrottoRenderComponent.class)){
            GrottoRenderComponent renderComponent = tile.getComponent(GrottoRenderComponent.class);
            renderComponent.setX(x * tileSizeInPixels);
            renderComponent.setY(y * tileSizeInPixels);
        }
        else{
            Log.d("LevelSystem", "Tile does not have a render or physics component");
        }
    }

    /**
     * Generates the level based on the level generation element definitions.
     * */
    private boolean generate() {
        isGenerating = true;

        LevelDefinitionComponent levDefComp = levelEntity.getComponent(LevelDefinitionComponent.class);
        Map<Character, LevelGenerationElementDefinition> levelGenerationElementDefinitions = new HashMap<>();
        for(LevelGenerationElementDefinition def : levDefComp.getLevelGenerationElementDefinitions()) {
            levelGenerationElementDefinitions.put(def.getSymbol(), def);
        }

        boolean hasPlayer = false;
        boolean hasEnding = false;

        // Check if the level has a player and ending
        for(LevelGenerationElementDefinition def : levDefComp.getLevelGenerationElementDefinitions()) {
            if(def.getSymbol() == 'S') {
                hasPlayer = true;
            }
            if(def.getSymbol() == 'E') {
                hasEnding = true;
            }
        }

        if(!hasPlayer || !hasEnding){
            throw new RuntimeException("LevelGenerationElementDefinitions must have a player (S) and an ending (E) element.");
        }

        char[][] level = levelGenerationEngine.generateDungeon();
        if(level == null) {
            Log.d("LevelSystem", "Level generation failed.");
            return false;
        }
        else if(level.length == 0) {
            Log.d("LevelSystem", "Level generation failed. Level is empty.");
            return false;
        }

        Log.d("LevelSystem", "PhysicsWorld bodycount before level generation: " + ( (PhysicsSystem) gameWorld.getSystem(PhysicsSystem.class)).physicsWorld.getBodyCount());

        generatedDungeonToTiles(level, levelGenerationElementDefinitions);

        for(Map.Entry<String, Entity> entry : tileMap.entrySet()) {
            String[] coordinates = entry.getKey().split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            setTilesPosition(entry.getValue(), x, y);
        }

        for(Map.Entry<String, Entity> entry : entityMap.entrySet()) {
            String[] coordinates = entry.getKey().split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            setTilesPosition(entry.getValue(), x, y);
        }

        generateEnemies();

        isGenerating = false;
        return true;
    }

    private void generateEnemies() {
        final Map<String, Integer> enemyCountMap = new HashMap<>();
        LevelDefinitionComponent levelDefinitionComponent = levelEntity.getComponent(LevelDefinitionComponent.class);
        int playerLevel = 1;

        if(playerData != null){
            playerLevel = (int) playerData.getStats().get("level");
        }

        for (Entity entity : entityMap.values()){
            if (entity.hasComponent(EnemySpawnerComponent.class)) {
                EnemyElementDefinition selectedEnemyDef = getWeightedRandomEnemyDefinition(
                        levelDefinitionComponent.getEnemyElementDefinitions(),
                        playerLevel,
                        enemyCountMap
                );
                if (selectedEnemyDef != null) {
                    Entity enemy = gameWorld.createEntityById(selectedEnemyDef.getEnemyArchetypeId());

                    PhysicsComponent spawnerPhysics = entity.getComponent(PhysicsComponent.class);
                    if (spawnerPhysics != null) {
                        PhysicsComponent enemyPhysics = enemy.getComponent(PhysicsComponent.class);
                        if (enemyPhysics != null) {
                            enemyPhysics.getBody().setTransform(spawnerPhysics.getBody().getPosition(), 0);
                        }
                    }

                    CharacterStatsComponent enemyStats = enemy.getComponent(CharacterStatsComponent.class);
                    if (enemyStats != null) {
                        int enemyLevel = calculateEnemyLevel(playerLevel, level);
                        enemyStats.setStat("level", enemyLevel);
                    }

                    int currentEnemyCount = enemyCountMap.get(selectedEnemyDef.getEnemyArchetypeId()) == null ? 0 : enemyCountMap.get(selectedEnemyDef.getEnemyArchetypeId());
                    enemyCountMap.put(selectedEnemyDef.getEnemyArchetypeId(), currentEnemyCount + 1);
                    Log.d("LevelSystem", "Generated enemy " + selectedEnemyDef.getEnemyArchetypeId() + " (Lvl " + enemyStats.getStat("level") + ") at spawner position");
                }

                gameWorld.markEntityForDeletion(entity);
            }
        }
    }

    private int calculateEnemyLevel(int playerLevel, int dungeonDepth) {
        double factor = 0.5;
        int baseEnemyLevel = (int) Math.max(1, playerLevel + dungeonDepth * factor);
        Random random = new Random();
        int levelVariation = random.nextInt(3) - 1;
        int enemyLevel = Math.max(1, baseEnemyLevel + levelVariation);
        return enemyLevel;
    }

    private EnemyElementDefinition getWeightedRandomEnemyDefinition(List<EnemyElementDefinition> enemyElementDefinitions, int currentLevel, Map<String, Integer> enemyCountMap) {
        final Random random = new Random();
        float totalWeight = 0.0f;
        for (EnemyElementDefinition def : enemyElementDefinitions) {
            if (currentLevel >= def.getMinPlayerLevel() && enemyCountMap.getOrDefault(def.getEnemyArchetypeId(), 0) < def.getMaxQuantity()) {
                totalWeight += def.getProbability();
            }
        }

        float randomValue = random.nextFloat() * totalWeight;
        for (EnemyElementDefinition def : enemyElementDefinitions) {
            if (currentLevel >= def.getMinPlayerLevel() && enemyCountMap.getOrDefault(def.getEnemyArchetypeId(), 0) < def.getMaxQuantity()) {
                randomValue -= def.getProbability();
                if (randomValue <= 0) {
                    return def;
                }
            }
        }
        return null;
    }

    @Override
    public void onEvent(SystemEvent event) {
        if(event.getCode().equals("COLLISION")){
            Collision collision = (Collision) event.get("collision");
            Entity entity1 = collision.getA();
            Entity entity2 = collision.getB();
            if(entity1.hasComponent(PlayerComponent.class) && entity2.hasComponent(TileComponent.class)){
                TileComponent tile = entity2.getComponent(TileComponent.class);
                if(tile.getTag().equals("ending")){
                    nextLevel = true;
                }
            }
            else if(entity2.hasComponent(PlayerComponent.class) && entity1.hasComponent(TileComponent.class)){
                TileComponent tile = entity1.getComponent(TileComponent.class);
                if(tile.getTag().equals("ending")){
                    nextLevel = true;
                }
            }
        }
    }
}
