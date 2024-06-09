package com.deemaso.grotto.ui.elements;

import android.graphics.Typeface;
import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.events.EventListener;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.grotto.components.CharacterStatsComponent;

public class LevelLabelUIElement extends GameSpaceTextUIElement implements EventListener {

    public LevelLabelUIElement(float x, float y, float width, float height, Entity entity, String text, float textSize, Typeface typeface, int color) {
        super(x, y, width, height, entity, text, textSize, typeface, color);
    }

    @Override
    public void draw(float screenX, float screenY) {
        String text = "Lvl 1";
        final CharacterStatsComponent characterStatsComponent = entity.getComponent(CharacterStatsComponent.class);
        if(characterStatsComponent != null) text = "Lvl " + characterStatsComponent.getStat("level");
        getCanvas().drawText(text, screenX, screenY, getPaint());
    }

    @Override
    public void onEvent(SystemEvent event) {
        final String code = event.getCode();
        if ((code.equals("PLAYER_LOADED") || (code.equals("STAT_UPDATED") && "level".equals(event.get("stat")) && event.get("entity").equals(getEntity())))) {
            CharacterStatsComponent characterStatsComponent = getEntity().getComponent(CharacterStatsComponent.class);
            setText("Lvl " + characterStatsComponent.getStat("level"));
        }
    }
}
