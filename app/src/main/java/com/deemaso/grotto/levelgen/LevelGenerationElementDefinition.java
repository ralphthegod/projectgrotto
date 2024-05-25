package com.deemaso.grotto.levelgen;

import android.util.Log;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LevelGenerationElementDefinition{
    private char symbol;
    private String name;
    private int globalMinQuantity;
    private int globalMaxQuantity;
    private boolean canBeInRoom;
    private boolean canBeInCorridor;

    private int minDistanceBetweenElements;
    private DistributionType distribution;
    // Map key is probability of this tile being chosen
    private final Map<TileArchetype,Float> tileArchetypes;

    public static class TileArchetype{
        private final String archetypeId;
        private final String topArchetypeId;
        private final String rightArchetypeId;
        private final String bottomArchetypeId;
        private final String leftArchetypeId;
        private final String baseArchetypeId;

        public TileArchetype(String topArchetypeId, String rightArchetypeId, String bottomArchetypeId, String leftArchetypeId, String archetypeId, String baseArchetypeId) {
            this.topArchetypeId = topArchetypeId;
            this.rightArchetypeId = rightArchetypeId;
            this.bottomArchetypeId = bottomArchetypeId;
            this.leftArchetypeId = leftArchetypeId;
            this.archetypeId = archetypeId;
            this.baseArchetypeId = baseArchetypeId;
        }

        public String getTopArchetypeId() {
            return topArchetypeId;
        }

        public String getRightArchetypeId() {
            return rightArchetypeId;
        }

        public String getBottomArchetypeId() {
            return bottomArchetypeId;
        }

        public String getLeftArchetypeId() {
            return leftArchetypeId;
        }

        public String getArchetypeId() { return archetypeId; }

        public String getBaseArchetypeId() {
            return baseArchetypeId;
        }
    }

    public LevelGenerationElementDefinition(char symbol, String name, int globalMinQuantity, int globalMaxQuantity, boolean canBeInRoom, boolean canBeInCorridor, int minDistanceBetweenElements, DistributionType distribution, Map<TileArchetype, Float> tileArchetypes) {
        this.symbol = symbol;
        this.name = name;
        this.globalMinQuantity = globalMinQuantity;
        this.globalMaxQuantity = globalMaxQuantity;
        this.canBeInRoom = canBeInRoom;
        this.canBeInCorridor = canBeInCorridor;
        this.minDistanceBetweenElements = minDistanceBetweenElements;
        this.distribution = distribution;
        this.tileArchetypes = tileArchetypes;
    }

    // Getters e Setters
    public char getSymbol() {
        return symbol;
    }

    public Map<TileArchetype, Float> getTileArchetypes() {
        return tileArchetypes;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGlobalMinQuantity() {
        return globalMinQuantity;
    }

    public void setGlobalMinQuantity(int globalMinQuantity) {
        this.globalMinQuantity = globalMinQuantity;
    }

    public int getGlobalMaxQuantity() {
        return globalMaxQuantity;
    }

    public void setGlobalMaxQuantity(int globalMaxQuantity) {
        this.globalMaxQuantity = globalMaxQuantity;
    }

    public boolean isCanBeInRoom() {
        return canBeInRoom;
    }

    public void setCanBeInRoom(boolean canBeInRoom) {
        this.canBeInRoom = canBeInRoom;
    }

    public boolean isCanBeInCorridor() {
        return canBeInCorridor;
    }

    public void setCanBeInCorridor(boolean canBeInCorridor) {
        this.canBeInCorridor = canBeInCorridor;
    }

    public int getMinDistanceBetweenElements() {
        return minDistanceBetweenElements;
    }

    public void setMinDistanceBetweenElements(int minDistanceBetweenElements) {
        this.minDistanceBetweenElements = minDistanceBetweenElements;
    }

    public DistributionType getDistribution() {
        return distribution;
    }

    public void setDistribution(DistributionType distribution) {
        this.distribution = distribution;
    }

    public TileArchetype getWeightedRandomTileArchetype() {
        if (tileArchetypes.size() == 1) {
            return tileArchetypes.keySet().iterator().next();
        } else {
            float totalWeight = 0.0f;
            for (float weight : tileArchetypes.values()) {
                totalWeight += weight;
            }

            float random = (float) Math.random() * totalWeight;

            for (Map.Entry<TileArchetype, Float> entry : tileArchetypes.entrySet()) {
                random -= entry.getValue();
                if (random <= 0) {
                    return entry.getKey();
                }
            }
            Log.e("LevelGenerationElementDefinition", "Error getting random tile archetype.");
            return null;
        }
    }

    public static LevelGenerationElementDefinition loadFromXML(Element element) {
        char symbol = element.getAttribute("symbol").charAt(0);
        String name = element.getAttribute("name");
        int globalMinQuantity = Integer.parseInt(element.getAttribute("globalMinQuantity"));
        int globalMaxQuantity = Integer.parseInt(element.getAttribute("globalMaxQuantity"));
        boolean canBeInRoom = Boolean.parseBoolean(element.getAttribute("canBeInRoom"));
        boolean canBeInCorridor = Boolean.parseBoolean(element.getAttribute("canBeInCorridor"));
        int minDistanceBetweenElements = Integer.parseInt(element.getAttribute("minDistanceBetweenElements"));
        DistributionType distribution = DistributionType.values()[Integer.parseInt(element.getAttribute("distribution"))];

        HashMap<TileArchetype, Float> tileArchetypes = new HashMap<>();
        NodeList tileArchetypeNodes = element.getElementsByTagName("TileArchetype");
        for (int i = 0; i < tileArchetypeNodes.getLength(); i++) {
            Element tileArchetypeElement = (Element) tileArchetypeNodes.item(i);
            float probability = Float.parseFloat(tileArchetypeElement.getAttribute("probability"));
            String topArchetypeId = tileArchetypeElement.getAttribute("topArchetypeId");
            String baseArchetypeId = tileArchetypeElement.getAttribute("baseArchetypeId");
            String rightArchetypeId = tileArchetypeElement.getAttribute("rightArchetypeId");
            String bottomArchetypeId = tileArchetypeElement.getAttribute("bottomArchetypeId");
            String leftArchetypeId = tileArchetypeElement.getAttribute("leftArchetypeId");
            String archetypeId = tileArchetypeElement.getAttribute("archetypeId");
            TileArchetype tileArchetype = new TileArchetype(topArchetypeId, rightArchetypeId, bottomArchetypeId, leftArchetypeId, archetypeId, baseArchetypeId);
            tileArchetypes.put(tileArchetype, probability);
        }
        return new LevelGenerationElementDefinition(symbol, name, globalMinQuantity, globalMaxQuantity, canBeInRoom, canBeInCorridor, minDistanceBetweenElements, distribution, tileArchetypes);
    }


}