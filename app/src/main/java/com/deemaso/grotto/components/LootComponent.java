package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;

public class LootComponent extends Component {
    private final int value;
    private final boolean removeAfterCollecting;

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
