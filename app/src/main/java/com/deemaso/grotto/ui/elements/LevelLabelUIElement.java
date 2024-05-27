package com.deemaso.grotto.ui.elements;

import android.graphics.Typeface;
import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.events.EventListener;
import com.deemaso.core.events.SystemEvent;

public class LevelLabelUIElement extends GameSpaceTextUIElement implements EventListener {

    public LevelLabelUIElement(float x, float y, float width, float height, Entity entity, String text, float textSize, Typeface typeface, int color) {
        super(x, y, width, height, entity, text, textSize, typeface, color);
    }

    @Override
    public void draw(float screenX, float screenY) {
        getCanvas().drawText(getText(), screenX, screenY, getPaint());
    }

    @Override
    public void onEvent(SystemEvent event) {
        if((event.getCode().equals("LEVEL_UP") || event.getCode().equals("LEVEL_DOWN") || event.getCode().equals("LEVEL_SET"))
                && event.get("entity").equals(getEntity()))
        {
            setText("Lvl " + event.get("level"));
        }

    }
}
