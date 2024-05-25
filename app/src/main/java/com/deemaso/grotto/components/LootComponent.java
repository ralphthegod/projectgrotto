package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;

public class LootComponent extends Component {
    private final int value;

    public LootComponent(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
