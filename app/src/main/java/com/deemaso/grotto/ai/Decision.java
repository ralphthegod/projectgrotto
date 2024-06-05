package com.deemaso.grotto.ai;

import com.deemaso.core.Entity;

/**
 * Represents a decision in the decision tree.
 */
public interface Decision {
    /**
     * Evaluates the decision.
     * @param context The AI context
     * @return True if the decision is true, false otherwise
     */
    boolean evaluate(AIContext context);
}
