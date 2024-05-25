package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;

public class NavigationComponent extends Component {
    private final boolean isWalkable;
    private final boolean isFlyable;

    public NavigationComponent(boolean isWalkable, boolean isFlyable) {
        this.isWalkable = isWalkable;
        this.isFlyable = isFlyable;
    }

    public NavigationComponent(boolean isWalkable) {
        this.isWalkable = isWalkable;
        this.isFlyable = false;
    }

    public boolean isWalkable() {
        return isWalkable;
    }

    public boolean isFlyable() {
        return isFlyable;
    }

}
