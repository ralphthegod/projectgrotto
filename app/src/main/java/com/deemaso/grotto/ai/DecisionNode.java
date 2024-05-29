package com.deemaso.grotto.ai;

import com.deemaso.core.Entity;

public class DecisionNode extends TreeNode{
    private final TreeNode trueNode;
    private final TreeNode falseNode;
    private final Decision decision;

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
