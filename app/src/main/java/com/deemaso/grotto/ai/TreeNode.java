package com.deemaso.grotto.ai;

/**
 * Represents a decision tree node.
 */
public abstract class TreeNode {
    /**
     * Executes the node.
     * @param context The AI context
     */
    public abstract void execute(AIContext context);
}
