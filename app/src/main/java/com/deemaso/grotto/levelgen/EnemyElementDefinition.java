package com.deemaso.grotto.levelgen;

import org.w3c.dom.Element;

/**
 * Represents an enemy element definition. <br>
 * It replaces an enemy spawner when generating the level.
 * */
public class EnemyElementDefinition {

    private final String enemyArchetypeId;
    private final int maxQuantity;
    private final int minPlayerLevel;
    private final float probability;

    public EnemyElementDefinition(String enemyArchetypeId, int maxQuantity, int minPlayerLevel, float probability) {
        this.enemyArchetypeId = enemyArchetypeId;
        this.maxQuantity = maxQuantity;
        this.minPlayerLevel = minPlayerLevel;
        this.probability = probability;
    }

    public String getEnemyArchetypeId() {
        return enemyArchetypeId;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public int getMinPlayerLevel() {
        return minPlayerLevel;
    }

    public float getProbability() { return probability; }

    public static EnemyElementDefinition loadFromXml(Element element){
        String enemyArchetypeId = element.getAttribute("enemyArchetypeId");
        int maxQuantity = Integer.parseInt(element.getAttribute("maxQuantity"));
        int minPlayerLevel = Integer.parseInt(element.getAttribute("minPlayerLevel"));
        float probability = Float.parseFloat(element.getAttribute("probability"));
        return new EnemyElementDefinition(enemyArchetypeId, maxQuantity, minPlayerLevel, probability);
    }

}
