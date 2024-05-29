package com.deemaso.grotto.ai.decisions;

import com.deemaso.core.Entity;
import com.deemaso.grotto.ai.AIContext;
import com.deemaso.grotto.ai.Decision;
import com.deemaso.grotto.components.CharacterStatsComponent;
import com.deemaso.grotto.components.PerceptionComponent;

public class AnyHostileNearDecision implements Decision {
    @Override
    public boolean evaluate(AIContext context) {
        Entity self = (Entity) context.get("self");
        if(self != null){
            PerceptionComponent perceptionComponent = self.getComponent(PerceptionComponent.class);
            for(Entity entity: perceptionComponent.getPerceivedEntities()){
                if(entity.hasComponent(CharacterStatsComponent.class)){
                    CharacterStatsComponent characterStatsComponent = entity.getComponent(CharacterStatsComponent.class);
                    if(!characterStatsComponent.getFaction().equals(self.getComponent(CharacterStatsComponent.class).getFaction())){
                        context.remove("target");
                        context.put("target", entity);
                        return true;
                    }
                }
            }
        }
        context.remove("target");
        return false;
    }
}