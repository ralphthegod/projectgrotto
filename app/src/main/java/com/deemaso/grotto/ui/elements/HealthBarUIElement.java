package com.deemaso.grotto.ui.elements;


import android.graphics.Bitmap;

import com.deemaso.core.Entity;
import com.deemaso.core.events.EventListener;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.grotto.components.CharacterStatsComponent;
import com.deemaso.grotto.components.PlayerComponent;
import com.deemaso.grotto.ui.UIElement;

public class HealthBarUIElement extends UIElement implements EventListener {

    final Bitmap fullHeart;
    final Bitmap emptyHeart;
    final Bitmap halfHeart;

    public HealthBarUIElement(float x, float y, float width, float height, Bitmap fullHeart, Bitmap emptyHeart, Bitmap halfHeart) {
        super(x, y, width, height);
        this.fullHeart = fullHeart;
        this.emptyHeart = emptyHeart;
        this.halfHeart = halfHeart;
    }

    @Override
    public void onEvent(SystemEvent event) {
        if(event.getCode().equals("STAT_UPDATED")){
            Entity entity = (Entity) event.get("entity");
            if(!entity.hasComponent(PlayerComponent.class) || !event.get("stat").equals("health")){
                return;
            }
            int maxHealth = (int) entity.getComponent(CharacterStatsComponent.class).getStat("maxHealth");
            int currentHealth = (int) event.get("value");
            int heartCount = children.size();
            int requiredHeartCount = maxHealth / 2;

            if(heartCount < requiredHeartCount){
                for (int i = heartCount; i < requiredHeartCount; i++) {
                    HeartUIElement heartUIElement = new HeartUIElement(i * 20, 0, 25, 25, fullHeart, emptyHeart, halfHeart);
                    addChild(heartUIElement);
                }
            }
            for (int i = 0; i < children.size(); i++) {
                HeartUIElement heartUIElement = (HeartUIElement) children.get(i);
                if(currentHealth >= 2){
                    heartUIElement.setFullHeart();
                    currentHealth -= 2;
                }else if(currentHealth == 1){
                    heartUIElement.setHalfHeart();
                    currentHealth -= 1;
                }else{
                    heartUIElement.setEmptyHeart();
                }
            }

        }
    }

    @Override
    public void draw(float screenX, float screenY) {
        super.draw(screenX, screenY);
    }
}
