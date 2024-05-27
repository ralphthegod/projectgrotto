package com.deemaso.grotto.systems;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.collisions.Collision;
import com.deemaso.core.components.Component;
import com.deemaso.core.events.EventListener;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.core.systems.System;
import com.deemaso.grotto.components.CharacterStatsComponent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CombatSystem extends System implements EventListener {

    private static class Combat {
        private final Entity entityA;
        private final Entity entityB;

        public Combat(Entity entityA, Entity entityB) {
            this.entityA = entityA;
            this.entityB = entityB;
        }

        public Entity getEntityA() {
            return entityA;
        }

        public Entity getEntityB() {
            return entityB;
        }
    }

    private final Queue<Combat> combats = new LinkedList<>();

    protected CombatSystem(GameWorld gameWorld, List<Class<? extends Component>> requiredComponents, boolean requireAllComponents) {
        super(gameWorld, requiredComponents, requireAllComponents);
    }

    public CombatSystem(GameWorld gameWorld){
        super(gameWorld, Arrays.asList(CharacterStatsComponent.class), true);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        Combat combat = combats.poll();
        if(combat != null){
            Entity entityA = combat.getEntityA();
            Entity entityB = combat.getEntityB();
            CharacterStatsComponent statsA = entityA.getComponent(CharacterStatsComponent.class);
            CharacterStatsComponent statsB = entityB.getComponent(CharacterStatsComponent.class);
            if(statsA.getLevel() != statsB.getLevel()){
                Entity winner = statsA.getLevel() > statsB.getLevel() ? entityA : entityB;
                Entity loser = statsA.getLevel() > statsB.getLevel() ? entityB : entityA;
                SystemEvent event = new SystemEvent("COMBAT");
                event.put("winner", winner);
                event.put("loser", loser);
                gameWorld.broadcastEvent(event);
            }

        }
    }

    @Override
    public void onEvent(SystemEvent event) {
        if(event.getCode().equals("COLLISION")){
            Collision collision = (Collision) event.get("collision");
            Entity entity1 = collision.getA();
            Entity entity2 = collision.getB();
            if(entity1.hasComponent(CharacterStatsComponent.class) && entity2.hasComponent(CharacterStatsComponent.class)) {
                combats.add(new Combat(entity1, entity2));
            }
        }
    }

    @Override
    protected void finalize() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
}
