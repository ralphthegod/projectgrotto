package com.deemaso.grotto.ai.decisions;

import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.grotto.ai.AIContext;
import com.deemaso.grotto.ai.Decision;
import com.deemaso.grotto.components.WeaponComponent;

public class IsInMeleeRangeDecision implements Decision {
    @Override
    public boolean evaluate(AIContext context) {
        Entity entity = (Entity) context.get("self");
        Entity targetEntity = (Entity) context.get("target");
        float distance = (float) context.get("targetDistance");

        if (targetEntity == null) {
            return false;
        }

        WeaponComponent weaponComponent = entity.getComponent(WeaponComponent.class);

        if (weaponComponent == null) {
            return false;
        }

        float shootingRange = 2.5f;

        return distance <= shootingRange;

    }
}
