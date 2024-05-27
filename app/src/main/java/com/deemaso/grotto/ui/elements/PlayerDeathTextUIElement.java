package com.deemaso.grotto.ui.elements;

import android.graphics.Typeface;
import android.util.Log;

import com.deemaso.core.events.EventListener;
import com.deemaso.core.events.SystemEvent;

public class PlayerDeathTextUIElement extends TextUIElement implements EventListener {

    public PlayerDeathTextUIElement(float x, float y, float width, float height, String text, float textSize, Typeface typeface, int color) {
        super(x, y, width, height, text, textSize, typeface, color);
        setVisible(false);
    }

    @Override
    public void onEvent(SystemEvent event) {
        if(event.getCode().equals("PLAYER_DIED"))
        {
            setVisible(true);
            setText("You died!");
        }
    }
}
