package com.deemaso.grotto;

import android.content.Context;
import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.EntityManager;
import com.deemaso.core.components.Component;
import com.deemaso.core.components.InputComponent;
import com.deemaso.core.components.TransformComponent;
import com.deemaso.grotto.components.CharLevelComponent;
import com.deemaso.grotto.components.LevelDefinitionComponent;
import com.deemaso.grotto.components.LootComponent;
import com.deemaso.grotto.components.NavigationComponent;
import com.deemaso.grotto.components.PlayerComponent;
import com.deemaso.grotto.components.TileComponent;
import com.deemaso.grotto.levelgen.LevelGenerationElementDefinition;
import com.deemaso.grotto.ui.UIElement;
import com.deemaso.grotto.ui.UIFactory;
import com.deemaso.grotto.ui.UIManager;
import com.deemaso.grotto.utils.Helpers;
import com.deemaso.grotto.components.CameraComponent;
import com.deemaso.grotto.components.GrottoRenderComponent;
import com.deemaso.grotto.components.MusicComponent;
import com.deemaso.grotto.components.PhysicsComponent;

import org.jbox2d.dynamics.BodyType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class GrottoEntityManager extends EntityManager {

    private Context context;

    public GrottoEntityManager(Context context) {
        super();
        this.context = context;
    }

    @Override
    public Entity createEntityById(String id) {
        try {
            InputStream inputStream = context.getAssets().open("archetypes/" + id + ".xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();

            Entity entity = new Entity(id);

            // Load components
            NodeList components = doc.getElementsByTagName("Component");
            for (int i = 0; i < components.getLength(); i++) {
                Element componentElement = (Element) components.item(i);
                String componentName = componentElement.getAttribute("name");
                Component component = componentFactory.createComponent(componentName, componentElement);
                entity.addComponent(component);
            }

            return entity;
        } catch (Exception e) {
            Log.d("EntityManager", "Error loading entity: " + id + ".xml");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void loadComponentFactoryCreators() {

        componentFactory.registerComponent(
                "PhysicsComponent",
                (element) -> {
                    float density = Helpers.getAttributeAsFloat(element, "density", 1.0f);
                    float friction = Helpers.getAttributeAsFloat(element, "friction", 0.5f);
                    float shapeHeight = Helpers.getAttributeAsFloat(element, "shapeHeight", 1.0f);
                    float shapeWidth = Helpers.getAttributeAsFloat(element, "shapeWidth", 1.0f);
                    float x = Helpers.getAttributeAsFloat(element, "x", 0.0f);
                    float y = Helpers.getAttributeAsFloat(element, "y", 0.0f);
                    boolean isSensor = Helpers.getAttributeAsBoolean(element, "isSensor", false);
                    BodyType bodyType = BodyType.values()[Helpers.getAttributeAsInt(element, "bodyType", 0)];
                    PhysicsComponent physics = new PhysicsComponent();
                    physics.setDensity(density);
                    physics.setFriction(friction);
                    physics.setShapeHeight(shapeHeight);
                    physics.setShapeWidth(shapeWidth);
                    physics.setX(x);
                    physics.setY(y);
                    physics.setBodyType(bodyType);
                    physics.setSensor(isSensor);
                    return physics;
                }
        );

        componentFactory.registerComponent(
                "GrottoRenderComponent",
                (element) -> {
                    GrottoRenderComponent render = new GrottoRenderComponent();
                    render.setScreenSemiWidth(Helpers.getAttributeAsFloat(element, "screenSemiWidth", 1.0f));
                    render.setScreenSemiHeight(Helpers.getAttributeAsFloat(element, "screenSemiHeight", 1.0f));
                    render.setAnimationFrameDuration(Helpers.getAttributeAsFloat(element, "animationFrameDuration", 0.2f));
                    render.setHeight(Helpers.getAttributeAsFloat(element, "height", 1.0f));
                    render.setWidth(Helpers.getAttributeAsFloat(element, "width", 1.0f));
                    List<String> spriteList = Helpers.getChildElementsAsStringList(element, "sprite");
                    render.setX(Helpers.getAttributeAsFloat(element, "x", 0.0f));
                    render.setY(Helpers.getAttributeAsFloat(element, "y", 0.0f));
                    render.setZIndex(Helpers.getAttributeAsInt(element, "zIndex", 0));
                    render.setResourceIds(spriteList);
                    return render;
                }
        );
        componentFactory.registerComponent(
            "CameraComponent",
            (Element element) -> {
                float zoom = Helpers.getAttributeAsFloat(element,"zoom", 1f);
                return new CameraComponent(zoom);
            }
        );

        componentFactory.registerComponent(
            "InputComponent",
            (Element element) -> new InputComponent()
        );

        componentFactory.registerComponent(
            "TransformComponent",
            (Element element) -> {
                float x = Helpers.getAttributeAsFloat(element, "x", 0.0f);
                float y = Helpers.getAttributeAsFloat(element, "y", 0.0f);
                float rotation = Helpers.getAttributeAsFloat(element, "rotation", 0.0f);
                float scale = Helpers.getAttributeAsFloat(element, "scale", 1.0f);
                return new TransformComponent(x,y,rotation,scale);
            }
        );

        componentFactory.registerComponent(
            "MusicComponent",
            (Element element) -> {
                String music = Helpers.getAttributeAsString(element, "music", "");
                return new MusicComponent(music);
            }
        );

        componentFactory.registerComponent(
            "TileComponent",
            (Element element) -> {
                int x = Helpers.getAttributeAsInt(element, "x", 0);
                int y = Helpers.getAttributeAsInt(element, "y", 0);
                float effectiveX = Helpers.getAttributeAsFloat(element, "effectiveX", 0.0f);
                float effectiveY = Helpers.getAttributeAsFloat(element, "effectiveY", 0.0f);
                return new TileComponent(x, y, effectiveX, effectiveY);
            }
        );

        componentFactory.registerComponent(
            "NavigationComponent",
            (Element element) -> {
                boolean isWalkable = Helpers.getAttributeAsBoolean(element, "isWalkable", false);
                boolean isFlyable = Helpers.getAttributeAsBoolean(element, "isFlyable", false);
                return new NavigationComponent(isWalkable, isFlyable);
            }
        );

        componentFactory.registerComponent(
            "LevelDefinitionComponent",
            (element) -> {
                int minRoomSize = Helpers.getAttributeAsInt(element, "minRoomSize", 5);
                int maxRoomSize = Helpers.getAttributeAsInt(element, "maxRoomSize", 10);
                int maxRooms = Helpers.getAttributeAsInt(element, "maxRooms", 10);
                int gridWidth = Helpers.getAttributeAsInt(element, "gridWidth", 10);
                int gridHeight = Helpers.getAttributeAsInt(element, "gridHeight", 10);
                float levelProgressionMultiplierIncrease = Helpers.getAttributeAsFloat(element, "levelProgressionMultiplierIncrease", 0.3f);
                float levelProgressionMultiplier = Helpers.getAttributeAsFloat(element, "levelProgressionMultiplier", 1.1f);
                List<LevelGenerationElementDefinition> listElementDefinitions = new ArrayList<>();

                NodeList elementDefinitions = element.getElementsByTagName("ElementDefinition");
                for (int i = 0; i < elementDefinitions.getLength(); i++) {
                    Element elementDefinitionElement = (Element) elementDefinitions.item(i);
                    LevelGenerationElementDefinition elementDefinition = LevelGenerationElementDefinition.loadFromXML(elementDefinitionElement);
                    listElementDefinitions.add(elementDefinition);
                }

                return new LevelDefinitionComponent(
                    minRoomSize,
                    maxRoomSize,
                    maxRooms,
                    gridWidth,
                    gridHeight,
                    listElementDefinitions,
                    levelProgressionMultiplierIncrease,
                    levelProgressionMultiplier
                );
            }
        );

        componentFactory.registerComponent(
            "LootComponent",
            (element) -> {
                int value = Helpers.getAttributeAsInt(element, "lootValue", 0);
                return new LootComponent(value);
            }
        );

        componentFactory.registerComponent(
            "CharLevelComponent",
            (element) -> {
              return new CharLevelComponent();
            }
        );

        componentFactory.registerComponent(
            "PlayerComponent",
            (element) -> {
                return new PlayerComponent();
            }
        );

    }
}
