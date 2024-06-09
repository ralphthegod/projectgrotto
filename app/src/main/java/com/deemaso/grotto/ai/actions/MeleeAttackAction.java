package com.deemaso.grotto.ai.actions;

import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.grotto.ai.AIContext;
import com.deemaso.grotto.ai.Action;
import com.deemaso.grotto.components.PhysicsComponent;

import org.jbox2d.common.Vec2;

public class MeleeAttackAction implements Action {
    @Override
    public void execute(AIContext context) {
        GameWorld gameWorld = (GameWorld) context.get("gameWorld");
        Entity attacker = (Entity) context.get("self");

        if (attacker == null) {
            return;
        }

        SystemEvent event = new SystemEvent("ATTACK");
        event.put("attacker", attacker);
        gameWorld.broadcastEvent(event);
    }
}
