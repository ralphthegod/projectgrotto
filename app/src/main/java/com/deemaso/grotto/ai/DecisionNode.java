package com.deemaso.grotto.ai;

import com.deemaso.core.Entity;

/**
 * Represents a decision tree node.
 */
public class DecisionNode extends TreeNode{
    private final TreeNode trueNode;
    private final TreeNode falseNode;
    private final Decision decision;

    /**
     * Creates a new DecisionNode.
     * @param decision The decision
     * @param trueNode The true node
     * @param falseNode The false node
     */
    public DecisionNode(Decision decision, TreeNode trueNode, TreeNode falseNode) {
        this.decision = decision;
        this.trueNode = trueNode;
        this.falseNode = falseNode;
    }

    @Override
    public void execute(AIContext context) {
        if (decision.evaluate(context)) {
            trueNode.execute(context);
        } else {
            falseNode.execute(context);
        }
    }
}
