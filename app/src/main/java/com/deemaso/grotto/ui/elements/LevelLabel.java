package com.deemaso.grotto.ui.elements;

import android.graphics.Typeface;

import com.deemaso.core.Entity;
import com.deemaso.grotto.components.CharLevelComponent;

public class LevelLabel extends GameSpaceTextUIElement{

    public LevelLabel(float x, float y, float width, float height, Entity entity, String text, float textSize, Typeface typeface, int color) {
        super(x, y, width, height, entity, text, textSize, typeface, color);
    }

    @Override
    public void draw(float screenX, float screenY) {
        if(entity.hasComponent(CharLevelComponent.class)){
            setText("Lvl " + entity.getComponent(CharLevelComponent.class).getLevel());
        }
        getCanvas().drawText(getText(), screenX, screenY, getPaint());
    }
}
