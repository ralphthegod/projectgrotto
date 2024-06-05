package com.deemaso.grotto.ai;

/**
 * Represents an action in the decision tree.
 */
public interface Action {
    /**
     * Executes the action.
     * @param context The AI context
     */
    void execute(AIContext context);
}
