package com.deemaso.grotto.ai;

import android.content.Context;

import com.deemaso.grotto.ai.actions.IdleAction;
import com.deemaso.grotto.ai.actions.MoveToHostileTargetAction;
import com.deemaso.grotto.ai.decisions.AnyHostileNearDecision;

public class DecisionTreeFactory {
    public static TreeNode createDecisionTree(Context context, String path){
        Decision any = new AnyHostileNearDecision();
        ActionNode a1 = new ActionNode(new MoveToHostileTargetAction());
        ActionNode a2 = new ActionNode(new IdleAction());
        return new DecisionNode(any, a1, a2);
    }

}
