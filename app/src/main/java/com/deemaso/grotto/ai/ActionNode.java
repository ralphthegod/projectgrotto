package com.deemaso.grotto.ai;

/**
 * Represents an action node in the decision tree.
 */
public class ActionNode extends TreeNode{
    private final Action action;

    /**
     * Creates a new ActionNode.
     * @param action The action
     */
    public ActionNode(Action action) {
        this.action = action;
    }

    @Override
    public void execute(AIContext context) {
        action.execute(context);
    }
}
