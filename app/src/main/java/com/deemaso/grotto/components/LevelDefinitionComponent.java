package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;
import com.deemaso.grotto.levelgen.LevelGenerationElementDefinition;

import java.util.List;

/**
 * Represents a level definition component. <br>
 * Contains the definitions for the level generation elements.
 */
public class LevelDefinitionComponent extends Component {

    private int minRoomSize;
    private int maxRoomSize;
    private int maxRooms;
    private int gridWidth;
    private int gridHeight;
    private final List<LevelGenerationElementDefinition> levelGenerationElementDefinitions;
    private final float levelProgressionMultiplierIncrease;
    private float levelProgressionMultiplier;

    /**
     * Creates a new LevelDefinitionComponent.
     * @param minRoomSize The minimum room size
     * @param maxRoomSize The maximum room size
     * @param maxRooms The maximum number of rooms
     * @param gridWidth The grid width
     * @param gridHeight The grid height
     * @param levelGenerationElementDefinitions The level generation element definitions
     * @param levelProgressionMultiplierIncrease The level progression multiplier increase
     * @param levelProgressionMultiplier The level progression multiplier
     */
    public LevelDefinitionComponent(
            int minRoomSize,
            int maxRoomSize,
            int maxRooms,
            int gridWidth,
            int gridHeight,
            List<LevelGenerationElementDefinition> levelGenerationElementDefinitions,
            float levelProgressionMultiplierIncrease,
            float levelProgressionMultiplier
    ) {
        this.minRoomSize = minRoomSize;
        this.maxRoomSize = maxRoomSize;
        this.maxRooms = maxRooms;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.levelGenerationElementDefinitions = levelGenerationElementDefinitions;
        this.levelProgressionMultiplierIncrease = levelProgressionMultiplierIncrease;
        this.levelProgressionMultiplier = levelProgressionMultiplier;
    }

    public List<LevelGenerationElementDefinition> getLevelGenerationElementDefinitions() {
        return levelGenerationElementDefinitions;
    }

    public int getMinRoomSize() {
        return minRoomSize;
    }

    public void setMinRoomSize(int minRoomSize) {
        this.minRoomSize = minRoomSize;
    }

    public int getMaxRoomSize() {
        return maxRoomSize;
    }

    public void setMaxRoomSize(int maxRoomSize) {
        this.maxRoomSize = maxRoomSize;
    }

    public int getMaxRooms() {
        return maxRooms;
    }

    public void setMaxRooms(int maxRooms) {
        this.maxRooms = maxRooms;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }

    public float getLevelProgressionMultiplierIncrease() {
        return levelProgressionMultiplierIncrease;
    }

    public float getLevelProgressionMultiplier() {
        return levelProgressionMultiplier;
    }

    public void setLevelProgressionMultiplier(float levelProgressionMultiplier) {
        this.levelProgressionMultiplier = levelProgressionMultiplier;
    }
}
