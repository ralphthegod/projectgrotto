package com.deemaso.grotto.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.deemaso.core.Box;
import com.deemaso.core.components.TransformComponent;
import com.deemaso.core.events.EventListener;
import com.deemaso.grotto.components.PhysicsComponent;
import com.deemaso.grotto.events.CurrentViewEvent;
import com.deemaso.grotto.utils.RenderUtils;

import java.util.ArrayList;
import java.util.List;

public class UIManager implements EventListener<CurrentViewEvent> {
    private final List<UIElement> uiElements = new ArrayList<>();
    private Box currentView;
    private final Bitmap buffer;

    public UIManager(Bitmap buffer) {
        this.buffer = buffer;
    }

    public void draw() {
        for (UIElement element : uiElements) {
            draw(element);
        }
    }

    public void draw(UIElement element){
        if (element.isVisible()) {
            Canvas canvas = element.getCanvas();
            if(element instanceof GameSpaceUIElement) {
                drawGameSpaceUIElement((GameSpaceUIElement) element);
            }
            else{
                drawUIElement(element);
            }
        }
    }

    private void drawGameSpaceUIElement(GameSpaceUIElement element) {
        float x = 0;
        float y = 0;
        if(element.getEntity().hasComponent(TransformComponent.class)){
            TransformComponent transformComponent = element.getEntity().getComponent(TransformComponent.class);
            // Consider the offset of the element (element.x, element.y)
            x = transformComponent.getX() + element.getX();
            y = transformComponent.getY() + element.getY();
        }
        else if(element.getEntity().hasComponent(PhysicsComponent.class)){
            PhysicsComponent physicsComponent = element.getEntity().getComponent(PhysicsComponent.class);
            // Consider the offset of the element (element.x, element.y)
            x = physicsComponent.getBody().getPositionX() + element.getX();
            y = physicsComponent.getBody().getPositionY() + element.getY();
        }else{
            throw new RuntimeException("Cannot draw GameSpaceTextUIElement without a TransformComponent.");
        }

        if(x > currentView.xmin && x < currentView.xmax &&
                y > currentView.ymin && y < currentView.ymax) {
            float screen_x = RenderUtils.toPixelsX(x, currentView.xmin, currentView.width, buffer.getWidth());
            float screen_y = RenderUtils.toPixelsY(y, currentView.ymin, currentView.height, buffer.getHeight());
            element.draw(screen_x, screen_y);
        }
    }

    private void drawUIElement(UIElement element) {
        if(element.x > currentView.xmin && element.x < currentView.xmax &&
                element.y > currentView.ymin && element.y < currentView.ymax) {
            float screen_x = RenderUtils.toPixelsX(element.x, currentView.xmin, currentView.width, buffer.getWidth());
            float screen_y = RenderUtils.toPixelsY(element.y, currentView.ymin, currentView.height, buffer.getHeight());
            element.draw(screen_x, screen_y);
        }
    }

    @Override
    public void onEventReceived(CurrentViewEvent event) {
        currentView = event.getCurrentView();
    }

    public void addUIElement(UIElement element) {
        element.setCanvas(new Canvas(buffer));
        uiElements.add(element);
    }

    public void removeUIElement(UIElement element) {
        uiElements.remove(element);
    }
}