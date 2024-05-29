package com.deemaso.grotto.ai;

import com.deemaso.core.Entity;

public class ActionNode extends TreeNode{
    private final Action action;

    public ActionNode(Action action) {
        this.action = action;
    }

    @Override
    public void execute(AIContext context) {
        action.execute(context);
    }
}
