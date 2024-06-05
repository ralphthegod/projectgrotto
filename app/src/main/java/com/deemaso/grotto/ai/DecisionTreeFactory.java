package com.deemaso.grotto.ai;

import com.deemaso.grotto.ai.actions.IdleAction;
import com.deemaso.grotto.ai.actions.MoveToHostileTargetAction;
import com.deemaso.grotto.ai.decisions.AnyHostileNearDecision;
import org.w3c.dom.Element;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory for creating decision trees.
 */
public class DecisionTreeFactory {

    private static final Map<String, Supplier<Decision>> decisionSuppliers = new HashMap<>();

    static {
        decisionSuppliers.put("AnyHostileNear", AnyHostileNearDecision::new);
    }

    private static final Map<String, Supplier<Action>> actionSuppliers = new HashMap<>();

    static {
        actionSuppliers.put("Idle", IdleAction::new);
        actionSuppliers.put("MoveToHostileTarget", MoveToHostileTargetAction::new);
    }

    public static TreeNode createDecisionTree(Element rootElement) {
        return NodeFactory.createNode(rootElement);
    }

    public static Decision createDecision(String type) {
        Supplier<Decision> supplier = decisionSuppliers.get(type);
        if (supplier != null) {
            return supplier.get();
        }
        else throw new IllegalArgumentException("Unknown decision type: " + type);
    }

    public static Action createAction(String type) {
        Supplier<Action> supplier = actionSuppliers.get(type);
        if (supplier != null) {
            return supplier.get();
        }
        else throw new IllegalArgumentException("Unknown action type: " + type);
    }
}
