package com.deemaso.grotto.ai.decisions;

import com.deemaso.core.Entity;
import com.deemaso.grotto.ai.AIContext;
import com.deemaso.grotto.ai.Decision;
import com.deemaso.grotto.components.WeaponComponent;
import com.deemaso.grotto.items.RangedWeapon;

public class IsShootingDistanceDecision implements Decision {
    @Override
    public boolean evaluate(AIContext context) {
        Entity entity = (Entity) context.get("self");
        Entity targetEntity = (Entity) context.get("target");
        float distance = (float) context.get("targetDistance");

        if (targetEntity == null) {
            return false;
        }

        WeaponComponent weaponComponent = entity.getComponent(WeaponComponent.class);

        if (weaponComponent == null || !(weaponComponent.getWeapon() instanceof RangedWeapon)) {
            return false;
        }

        RangedWeapon rangedWeapon = (RangedWeapon) weaponComponent.getWeapon();

        // Maybe add a check for the weapon's range ... (rangedWeapon.getRange())

        float shootingRange = 4.0f;

        return distance <= shootingRange;

    }
}
