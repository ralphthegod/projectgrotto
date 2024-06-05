package com.deemaso.grotto.ui.elements;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.events.EventListener;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.grotto.components.PlayerComponent;
import com.deemaso.grotto.ui.UIElement;

public class ExperienceCounterUIElement extends UIElement implements EventListener {

    public ExperienceCounterUIElement(float x, float y, float width, float height, Bitmap image, int textSize, Typeface typeface) {
        super(x, y, width, height);
        TextUIElement textUIElement = new TextUIElement(x + 40, y, width, height, "0/10", textSize, typeface, Color.WHITE);
        ImageUIElement imageUIElement = new ImageUIElement(x, y - 25, width, height, image);
        addChild(imageUIElement);
        addChild(textUIElement);
    }

    @Override
    public void draw(float screenX, float screenY) {
        super.draw(screenX, screenY);
    }

    @Override
    public void onEvent(SystemEvent event) {
        if(event.getCode().equals("STAT_UPDATED"))
        {
            Entity entity = (Entity) event.get("entity");
            if(!entity.hasComponent(PlayerComponent.class) || !event.get("stat").equals("experience")){
                return;
            }
            for (UIElement child : children) {
                if(child instanceof TextUIElement){
                    ((TextUIElement) child).setText(event.get("value") + "/10");
                }
            }
        }
    }
}
