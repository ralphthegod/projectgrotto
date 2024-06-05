package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;

/**
 * Represents a loot component. <br>
 * Contains the value of the loot and whether it should be removed after collecting.
 */
public class LootComponent extends Component {
    private final int value;
    private final boolean removeAfterCollecting;

    /**
     * Creates a new LootComponent.
     * @param value The value of the loot
     * @param removeAfterCollecting Whether the loot should be removed after collecting
     */
    public LootComponent(int value, boolean removeAfterCollecting) {
        this.value = value;
        this.removeAfterCollecting = removeAfterCollecting;
    }

    public int getValue() {
        return value;
    }

    public boolean isRemoveAfterCollecting() {
        return removeAfterCollecting;
    }
}
