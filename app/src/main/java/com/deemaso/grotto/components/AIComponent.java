package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;
import com.deemaso.grotto.ai.AIContext;
import com.deemaso.grotto.ai.TreeNode;

public class AIComponent extends Component {
    private TreeNode decisionTree;
    private final AIContext context;

    public AIComponent(TreeNode decisionTree) {
        this.decisionTree = decisionTree;
        this.context = new AIContext();
    }

    public TreeNode getDecisionTree() {
        return decisionTree;
    }

    public void setDecisionTree(TreeNode decisionTree) {
        this.decisionTree = decisionTree;
    }

    public AIContext getContext() {
        return context;
    }
}