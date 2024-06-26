package com.deemaso.grotto;

import android.content.Context;
import android.util.Log;

import com.badlogic.androidgames.framework.Sound;
import com.deemaso.core.Entity;
import com.deemaso.core.EntityManager;
import com.deemaso.core.components.Component;
import com.deemaso.core.components.InputComponent;
import com.deemaso.core.components.TransformComponent;
import com.deemaso.grotto.ai.DecisionTreeFactory;
import com.deemaso.grotto.ai.TreeNode;
import com.deemaso.grotto.components.AIComponent;
import com.deemaso.grotto.components.CharacterStatsComponent;
import com.deemaso.grotto.components.EnemySpawnerComponent;
import com.deemaso.grotto.components.LevelDefinitionComponent;
import com.deemaso.grotto.components.LootComponent;
import com.deemaso.grotto.components.MovementComponent;
import com.deemaso.grotto.components.NavigationComponent;
import com.deemaso.grotto.components.PerceptionComponent;
import com.deemaso.grotto.components.PlayerComponent;
import com.deemaso.grotto.components.SoundComponent;
import com.deemaso.grotto.components.TileComponent;
import com.deemaso.grotto.components.WeaponComponent;
import com.deemaso.grotto.data.ResourceLoader;
import com.deemaso.grotto.levelgen.EnemyElementDefinition;
import com.deemaso.grotto.levelgen.LevelGenerationElementDefinition;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    float gravityScale = Helpers.getAttributeAsFloat(element, "gravityScale", 1.0f);
                    BodyType bodyType = BodyType.values()[Helpers.getAttributeAsInt(element, "bodyType", 0)];
                    float linearDamping = Helpers.getAttributeAsFloat(element, "linearDamping", 0.0f);
                    PhysicsComponent physics = new PhysicsComponent();
                    physics.setLinearDamping(linearDamping);
                    physics.setDensity(density);
                    physics.setFriction(friction);
                    physics.setShapeHeight(shapeHeight);
                    physics.setShapeWidth(shapeWidth);
                    physics.setX(x);
                    physics.setY(y);
                    physics.setBodyType(bodyType);
                    physics.setSensor(isSensor);
                    physics.setGravityScale(gravityScale);
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
                    render.setConsiderAngle(Helpers.getAttributeAsBoolean(element, "considerAngle", false));
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
            "SoundComponent",
            (element) -> {
                Map<String, String> soundPaths = new HashMap<>();
                NodeList sounds = element.getElementsByTagName("Sound");
                for (int i = 0; i < sounds.getLength(); i++) {
                    Element soundElement = (Element) sounds.item(i);
                    String soundName = soundElement.getAttribute("name");
                    String soundPath = soundElement.getAttribute("path");
                    soundPaths.put(soundName, soundPath);
                }
                return new SoundComponent(soundPaths);
            }
        );

        componentFactory.registerComponent(
            "TileComponent",
            (Element element) -> {
                int x = Helpers.getAttributeAsInt(element, "x", 0);
                int y = Helpers.getAttributeAsInt(element, "y", 0);
                float effectiveX = Helpers.getAttributeAsFloat(element, "effectiveX", 0.0f);
                float effectiveY = Helpers.getAttributeAsFloat(element, "effectiveY", 0.0f);
                String tag = Helpers.getAttributeAsString(element, "tag", "");
                return new TileComponent(x, y, effectiveX, effectiveY, tag);
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

                List<EnemyElementDefinition> enemyElementDefinitions = new ArrayList<>();

                NodeList enemyDefinitions = element.getElementsByTagName("EnemyDefinition");
                for (int i = 0; i < enemyDefinitions.getLength(); i++) {
                    Element enemyDefinitionElement = (Element) enemyDefinitions.item(i);
                    EnemyElementDefinition enemyDefinition = EnemyElementDefinition.loadFromXml(enemyDefinitionElement);
                    enemyElementDefinitions.add(enemyDefinition);
                }

                return new LevelDefinitionComponent(
                    minRoomSize,
                    maxRoomSize,
                    maxRooms,
                    gridWidth,
                    gridHeight,
                    listElementDefinitions,
                    enemyElementDefinitions,
                    levelProgressionMultiplierIncrease,
                    levelProgressionMultiplier
                );
            }
        );

        componentFactory.registerComponent(
            "LootComponent",
            (element) -> {
                int value = Helpers.getAttributeAsInt(element, "lootValue", 0);
                boolean removeAfterCollecting = Helpers.getAttributeAsBoolean(element, "removeAfterCollecting", true);
                String stat = Helpers.getAttributeAsString(element, "stat", "");
                return new LootComponent(value, removeAfterCollecting, stat);
            }
        );

        componentFactory.registerComponent(
            "CharacterStatsComponent",
            (element) -> {
                int experience = Helpers.getAttributeAsInt(element, "experience", 0);
                String faction = Helpers.getAttributeAsString(element, "faction", "");
                int maxHealth = Helpers.getAttributeAsInt(element, "maxHealth", 100);
                return new CharacterStatsComponent(experience, faction, maxHealth);
            }
        );

        componentFactory.registerComponent(
            "PlayerComponent",
            (element) -> {
                return new PlayerComponent();
            }
        );

        componentFactory.registerComponent(
            "MovementComponent",
            (element) -> {
                float speed = Helpers.getAttributeAsFloat(element, "speed", 1.0f);
                float power = Helpers.getAttributeAsFloat(element, "power", 1.0f);
                return new MovementComponent(power, speed);
            }
        );

        componentFactory.registerComponent(
            "PerceptionComponent",
            (element) -> {
                float perceptionRadius = Helpers.getAttributeAsFloat(element, "perceptionRadius", 1.0f);
                return new PerceptionComponent(perceptionRadius);
            }
        );

        componentFactory.registerComponent(
            "AIComponent",
            (element) -> {
                String decisionTreeName = Helpers.getAttributeAsString(element, "decisionTree", "");
                TreeNode decisionTree = ResourceLoader.loadDecisionTree(context, decisionTreeName);
                return new AIComponent(decisionTree);
            }
        );

        componentFactory.registerComponent(
            "WeaponComponent",
            (element) -> {
                String weapon = Helpers.getAttributeAsString(element, "weapon", "");
                List<String> ignoreFactionList = Helpers.getChildElementsAsStringList(element, "faction");
                return new WeaponComponent(ResourceLoader.loadWeapon(context, weapon), ignoreFactionList);
            }
        );

        componentFactory.registerComponent(
            "EnemySpawnerComponent",
            (element) -> {
                return new EnemySpawnerComponent();
            }
        );

    }
}
