package com.deemaso.grotto.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import com.deemaso.core.Box;
import com.deemaso.core.Entity;
import com.deemaso.core.components.TransformComponent;
import com.deemaso.core.events.EventListener;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.grotto.components.CharacterStatsComponent;
import com.deemaso.grotto.components.PhysicsComponent;
import com.deemaso.grotto.data.ResourceLoader;
import com.deemaso.grotto.ui.elements.LevelLabelUIElement;
import com.deemaso.grotto.utils.Helpers;
import com.deemaso.grotto.utils.RenderUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class UIManager implements EventListener {
    private final List<UIElement> uiElements = new ArrayList<>();
    private Box currentView;
    private final Bitmap buffer;

    private final UIFactory uiFactory;

    private final Context context;

    private final ResourceLoader resourceLoader;

    public UIManager(Bitmap buffer, Box screenSize, Context context, ResourceLoader resourceLoader) {
        this.buffer = buffer;
        this.context = context;
        this.resourceLoader = resourceLoader;
        this.uiFactory = new UIFactory();
        loadUIFactoryCreators();
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
            x = physicsComponent.getBody().getPosition().x + element.getX();
            y = physicsComponent.getBody().getPosition().y + element.getY();
        }else{
            throw new RuntimeException("Cannot draw GameSpaceUIElement without a TransformComponent.");
        }

        if(x > currentView.xmin && x < currentView.xmax &&
                y > currentView.ymin && y < currentView.ymax) {
            float screen_x = RenderUtils.toPixelsX(x, currentView.xmin, currentView.width, buffer.getWidth());
            float screen_y = RenderUtils.toPixelsY(y, currentView.ymin, currentView.height, buffer.getHeight());
            element.draw(screen_x, screen_y);
        }
    }

    private void drawUIElement(UIElement element) {
        float screen_x = element.x;
        float screen_y = element.y;
        element.draw(screen_x, screen_y);
    }

    public void addUIElement(UIElement element) {
        element.setCanvas(new Canvas(buffer));
        uiElements.add(element);
    }

    public void createEntityGameSpaceUIElements(Entity entity) {
        try {
            InputStream inputStream = context.getAssets().open("archetypes/" + entity.getId() + ".xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();

            NodeList uiElements = doc.getElementsByTagName("UI");
            for (int i = 0; i < uiElements.getLength(); i++) {
                Element uiElement = (Element) uiElements.item(i);
                String elementType = uiElement.getAttribute("name");
                UIElement element = uiFactory.createUIElement(elementType, uiElement, entity);
                addUIElement(element);
            }

        } catch (Exception e) {
            Log.d("UIManager", "Error loading entity UI elements: " + entity.getId() + ".xml");
            e.printStackTrace();
        }
    }

    public void removeEntityGameSpaceUIElements(Entity entity) {
        List<GameSpaceUIElement> elementsToRemove = new ArrayList<>();
        for (UIElement element : uiElements) {
            if (element instanceof GameSpaceUIElement) {
                GameSpaceUIElement gameSpaceUIElement = (GameSpaceUIElement) element;
                if (gameSpaceUIElement.getEntity().equals(entity)) {
                    elementsToRemove.add(gameSpaceUIElement);
                }
            }
        }
        uiElements.removeAll(elementsToRemove);
    }

    public void removeUIElement(UIElement element) {
        uiElements.remove(element);
    }

    private void loadUIFactoryCreators() {
        uiFactory.registerUIElement("LevelLabel", (element, entity) -> {
            float x = Helpers.getAttributeAsFloat(element, "x", 0);
            float y = Helpers.getAttributeAsFloat(element, "y", 0);
            float textSize = Helpers.getAttributeAsFloat(element, "textSize", 20);
            int color = Color.parseColor(Helpers.getAttributeAsString(element, "color", "0xFFFFFFFF"));
            Typeface typeface = resourceLoader.loadFont(Helpers.getAttributeAsString(element, "typeface", "default"));
            return new LevelLabelUIElement(x, y, 0, 0, entity, "Lvl " + entity.getComponent(CharacterStatsComponent.class).getLevel(), textSize, typeface, color);
        });
    }

    @Override
    public void onEvent(SystemEvent event) {
        // Update the current view
        if(event.getCode().equals("CURRENT_VIEW")){
            currentView = (Box) event.get("currentView");
        }

        // Broadcast the event to all UIElements that are EventListeners
        for(UIElement element : uiElements){
            if(element instanceof EventListener){
                ((EventListener) element).onEvent(event);
            }
        }
    }
}