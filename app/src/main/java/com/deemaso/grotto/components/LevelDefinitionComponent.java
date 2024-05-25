package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;
import com.deemaso.grotto.levelgen.LevelGenerationElementDefinition;

import java.util.List;

public class LevelDefinitionComponent extends Component {

    private int minRoomSize;
    private int maxRoomSize;
    private int maxRooms;
    private int gridWidth;
    private int gridHeight;
    private final List<LevelGenerationElementDefinition> levelGenerationElementDefinitions;
    private final float levelProgressionMultiplierIncrease;
    private float levelProgressionMultiplier;

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