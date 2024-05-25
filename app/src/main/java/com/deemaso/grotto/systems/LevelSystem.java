package com.deemaso.grotto.systems;

import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.components.Component;
import com.deemaso.core.systems.System;
import com.deemaso.grotto.components.GrottoRenderComponent;
import com.deemaso.grotto.components.LevelDefinitionComponent;
import com.deemaso.grotto.components.PhysicsComponent;
import com.deemaso.grotto.components.TileComponent;
import com.deemaso.grotto.levelgen.LevelGenerationElementDefinition;
import com.deemaso.grotto.levelgen.LevelGenerationEngine;

import org.jbox2d.common.Vec2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelSystem extends System{

    private final Map<String, Entity> tileMap;
    private final Map<String, Entity> entityMap;
    private boolean isGenerating = false;
    private final LevelGenerationEngine levelGenerationEngine;
    private final Entity levelEntity;
    private final float tileSizeInPixels;

    public LevelSystem(
            GameWorld gameWorld,
            boolean requireAllComponents,
            String levelEntityArchetypeId,
            float tileSizeInPixels
    ) {
        super(gameWorld, Arrays.asList(), requireAllComponents);
        this.tileSizeInPixels = tileSizeInPixels;
        Entity levelEntity = gameWorld.createEntityById(levelEntityArchetypeId);
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
        this.levelEntity = levelEntity;
        tileMap = new HashMap<>();
        entityMap = new HashMap<>();
    }

    @Override
    public void update(float dt) {
        if(!isGenerating && tileMap.isEmpty()) {
            boolean success = false;
            int attempts = 0;
            while (!success && attempts < 5) { // Limit to 5 attempts
                try {
                    generate();
                    success = true; // If generate() is successful, set success to true
                } catch(Exception e) {
                    Log.d("LevelSystem", "Error generating level. Retrying...");
                    e.printStackTrace();
                    attempts++; // Increment the number of attempts
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

    public void addTile(int x, int y, Entity tile) {
        String key = encodeCoordinates(x, y);
        tileMap.put(key, tile);
    }

    public Entity getTile(int x, int y) {
        String key = encodeCoordinates(x, y);
        return tileMap.get(key);
    }

    public void addEntity(int x, int y, Entity entity) {
        String key = encodeCoordinates(x, y);
        entityMap.put(key, entity);
    }

    public Entity getEntity(int x, int y) {
        String key = encodeCoordinates(x, y);
        return entityMap.get(key);
    }

    private String encodeCoordinates(int x, int y) {
        return x + "," + y;
    }

    private void generatedDungeonToTiles(char[][] level, Map<Character, LevelGenerationElementDefinition> levelGenerationElementDefinitions){
        /*
        * An alternative would be to create a pattern detection system that would detect patterns
        * and replace them with the appropriate tiles. This would allow for more complex level
        * (e.g. side walls, corners, etc.)
        *  */
        for(int y = 0; y < level.length; y++) {
            for(int x = 0; x < level[y].length; x++) {
                char symbol = level[y][x];
                LevelGenerationElementDefinition def = levelGenerationElementDefinitions.get(symbol);
                if(def != null) {
                    LevelGenerationElementDefinition.TileArchetype tile = def.getWeightedRandomTileArchetype();
                    Entity tileEntity = gameWorld.createEntityById(tile.getArchetypeId());
                    if(tileEntity.hasComponent(TileComponent.class)){
                        addTile(x, y, tileEntity);
                        if(!tile.getBaseArchetypeId().equals("-1"))
                            addTile(x, y, gameWorld.createEntityById(tile.getBaseArchetypeId()));
                        if(!tile.getBottomArchetypeId().equals("-1"))
                            addTile(x, y + 1, gameWorld.createEntityById(tile.getBottomArchetypeId()));
                        if(!tile.getTopArchetypeId().equals("-1"))
                            addTile(x, y - 1, gameWorld.createEntityById(tile.getTopArchetypeId()));
                        if(!tile.getLeftArchetypeId().equals("-1"))
                            addTile(x - 1, y, gameWorld.createEntityById(tile.getLeftArchetypeId()));
                        if(!tile.getRightArchetypeId().equals("-1"))
                            addTile(x + 1, y, gameWorld.createEntityById(tile.getRightArchetypeId()));
                    }
                    else{
                        addEntity(x, y, tileEntity);
                        if(!tile.getBaseArchetypeId().equals("-1"))
                            addTile(x, y, gameWorld.createEntityById(tile.getBaseArchetypeId()));
                    }
                }
            }
        }
    }

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

    private void generate() {
        isGenerating = true;
        LevelDefinitionComponent levDefComp = levelEntity.getComponent(LevelDefinitionComponent.class);
        Map<Character, LevelGenerationElementDefinition> levelGenerationElementDefinitions = new HashMap<>();
        for(LevelGenerationElementDefinition def : levDefComp.getLevelGenerationElementDefinitions()) {
            levelGenerationElementDefinitions.put(def.getSymbol(), def);
        }

        char[][] level = levelGenerationEngine.generateDungeon();
        generatedDungeonToTiles(level, levelGenerationElementDefinitions);

        // Set the position of the tiles
        for(Map.Entry<String, Entity> entry : tileMap.entrySet()) {
            String[] coordinates = entry.getKey().split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            setTilesPosition(entry.getValue(), x, y);
        }

        // Set the position of the entities
        for(Map.Entry<String, Entity> entry : entityMap.entrySet()) {
            String[] coordinates = entry.getKey().split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            setTilesPosition(entry.getValue(), x, y);
        }
        isGenerating = false;
    }
}
