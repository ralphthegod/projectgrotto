package com.deemaso.grotto.ai;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NodeFactory {
    private static final Map<String, Function<Element, TreeNode>> nodeCreators = new HashMap<>();

    static {
        nodeCreators.put("DecisionNode", NodeFactory::createDecisionNode);
        nodeCreators.put("ActionNode", NodeFactory::createActionNode);
        // More????? (Selector, Sequence, etc.)
    }

    public static TreeNode createNode(Element element) {
        String nodeName = element.getTagName();
        Function<Element, TreeNode> creator = nodeCreators.get(nodeName);
        if (creator != null) {
            return creator.apply(element);
        }
        throw new IllegalArgumentException("Unknown node type: " + nodeName);
    }

    private static DecisionNode createDecisionNode(Element element) {
        Decision decision = DecisionTreeFactory.createDecision(element.getAttribute("type"));

        Node trueNodeElement = element.getElementsByTagName("TrueNode").item(0).getFirstChild();
        while (trueNodeElement != null && trueNodeElement.getNodeType() != Node.ELEMENT_NODE) {
            trueNodeElement = trueNodeElement.getNextSibling();
        }
        if (trueNodeElement == null) {
            throw new IllegalArgumentException("TrueNode does not contain an element node");
        }
        TreeNode trueNode = createNode((Element) trueNodeElement);

        Node falseNodeElement = element.getElementsByTagName("FalseNode").item(0).getFirstChild();
        while (falseNodeElement != null && falseNodeElement.getNodeType() != Node.ELEMENT_NODE) {
            falseNodeElement = falseNodeElement.getNextSibling();
        }
        if (falseNodeElement == null) {
            throw new IllegalArgumentException("FalseNode does not contain an element node");
        }
        TreeNode falseNode = createNode((Element) falseNodeElement);

        return new DecisionNode(decision, trueNode, falseNode);
    }

    private static ActionNode createActionNode(Element element) {
        Action action = DecisionTreeFactory.createAction(element.getAttribute("type"));
        return new ActionNode(action);
    }

}
