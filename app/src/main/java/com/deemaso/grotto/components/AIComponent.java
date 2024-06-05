package com.deemaso.grotto.components;

import com.deemaso.core.components.Component;
import com.deemaso.grotto.ai.AIContext;
import com.deemaso.grotto.ai.TreeNode;

/**
 * Represents an AI component. <br>
 * Contains the decision tree and the context of the AI.
 */
public class AIComponent extends Component {
    private TreeNode decisionTree;
    private final AIContext context;

    /**
     * Creates a new AIComponent.
     * @param decisionTree The decision tree of the AI
     */
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