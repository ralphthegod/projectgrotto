package com.deemaso.grotto.ai.actions;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.grotto.ai.AIContext;
import com.deemaso.grotto.ai.Action;
import com.deemaso.grotto.components.PhysicsComponent;

import org.jbox2d.common.Vec2;

public class ShootAction implements Action {
    @Override
    public void execute(AIContext context) {
        GameWorld gameWorld = (GameWorld) context.get("gameWorld");
        if (gameWorld == null) {
            return;
        }

        Entity attacker = (Entity) context.get("self");
        Entity target = (Entity) context.get("target");

        if (attacker == null || target == null) {
            return;
        }

        Vec2 attackerPosition = attacker.getComponent(PhysicsComponent.class).getBody().getPosition();
        Vec2 targetPosition = target.getComponent(PhysicsComponent.class).getBody().getPosition();
        Vec2 direction = targetPosition.sub(attackerPosition);
        direction.normalize();

        SystemEvent event = new SystemEvent("ATTACK");
        event.put("attacker", attacker);
        event.put("direction", direction);
        gameWorld.broadcastEvent(event);
    }
}
