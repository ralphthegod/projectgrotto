package com.deemaso.grotto;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.androidgames.framework.Sound;
import com.deemaso.core.Box;
import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.impl.AndroidAudio;
import com.badlogic.androidgames.framework.impl.MultiTouchHandler;
import com.deemaso.core.EntityManager;
import com.deemaso.core.components.RenderComponent;
import com.deemaso.core.systems.CollisionSystem;
import com.deemaso.core.systems.InputSystem;
import com.deemaso.core.systems.RenderSystem;
import com.deemaso.grotto.components.CameraComponent;
import com.deemaso.grotto.components.GrottoRenderComponent;
import com.deemaso.grotto.components.MusicComponent;
import com.deemaso.grotto.components.PhysicsComponent;
import com.deemaso.grotto.events.CurrentViewEvent;
import com.deemaso.grotto.listeners.AccelerometerListener;
import com.deemaso.grotto.systems.GrottoCollisionSystem;
import com.deemaso.grotto.systems.GrottoInputSystem;
import com.deemaso.grotto.systems.GrottoRenderSystem;
import com.deemaso.grotto.systems.PhysicsSystem;
import com.deemaso.grotto.systems.SoundSystem;
import com.deemaso.grotto.ui.UIManager;
import com.deemaso.grotto.ui.elements.GameSpaceTextUIElement;
import com.deemaso.grotto.ui.elements.TextUIElement;
import com.example.mfaella.physicsapp.CollisionSounds;
import com.example.mfaella.physicsapp.R;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.Vec2;

import java.nio.ByteOrder;
import java.util.Arrays;

public class MainActivity extends Activity {

    private AndroidFastRenderView renderView;
    // boundaries of the physical simulation
    private static final float XMIN = -10, XMAX = 10, YMIN = -15, YMAX = 15;
    // the tag used for logging
    public static String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");

        TAG = getString(R.string.app_name);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Sound
        Audio audio = new AndroidAudio(this);
        //CollisionSounds.init(audio);
        Music backgroundMusic = audio.newMusic("soundtrack.mp3");
        //backgroundMusic.play();

        // Game world
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Box physicalSize = new Box(XMIN, YMIN, XMAX, YMAX),
            screenSize   = new Box(0, 0, metrics.widthPixels, metrics.heightPixels);

        GrottoGameWorld gw = new GrottoGameWorld(new EntityManager(), this);

        renderView = new AndroidFastRenderView(this, gw, metrics.widthPixels, metrics.heightPixels);
        setContentView(renderView);


        GrottoRenderSystem renderSystem = new GrottoRenderSystem(gw, physicalSize, screenSize, renderView.getFrameBuffer());
        SoundSystem soundSystem = new SoundSystem(gw);
        PhysicsSystem physicsSystem = new PhysicsSystem(gw, 8,3,3);
        InputSystem inputSystem = new GrottoInputSystem(gw, new MultiTouchHandler(renderView, 1, 1));
        CollisionSystem collisionSystem = new GrottoCollisionSystem(gw);

        UIManager uiManager = new UIManager(renderView.getFrameBuffer(), screenSize);

        // Register the UI manager as a listener for the CurrentViewEvent
        // This event is emitted by the render system when the camera moves
        // so that the UI manager can update the position of the UI elements
        renderSystem.registerListener(CurrentViewEvent.class, uiManager);

        // Order matters (input, physics, render...)
        gw.addSystem(inputSystem);
        gw.addSystem(physicsSystem);
        gw.addSystem(collisionSystem);
        gw.addSystem(soundSystem);
        //game logic systems
        gw.addSystem(renderSystem);

        // Set the UI manager
        gw.setUIManager(uiManager);

        // Entities
        Entity backgroundMusicEntity = new Entity();
        backgroundMusicEntity.addComponent(new MusicComponent(backgroundMusic));
        gw.addEntity(backgroundMusicEntity);

        Entity testingEntity = new Entity();
        GrottoRenderComponent grottoRenderComponent = new GrottoRenderComponent();
        grottoRenderComponent.setResourceIds(Arrays.asList(R.drawable.angel_idle_anim_f0, R.drawable.angel_idle_anim_f1, R.drawable.angel_idle_anim_f2, R.drawable.angel_idle_anim_f3));
        grottoRenderComponent.setAnimationFrameDuration(0.1f);
        grottoRenderComponent.setHeight(2.5f);
        grottoRenderComponent.setWidth(2.5f);
        testingEntity.addComponent(grottoRenderComponent);
        testingEntity.addComponent(new PhysicsComponent());
        testingEntity.getComponent(PhysicsComponent.class).setX(5);
        testingEntity.getComponent(PhysicsComponent.class).setY(-5);
        testingEntity.getComponent(PhysicsComponent.class).setDensity(0.5f);
        testingEntity.getComponent(PhysicsComponent.class).setFriction(0.9f);
        testingEntity.getComponent(PhysicsComponent.class).setBodyType(BodyType.dynamicBody);
        testingEntity.getComponent(PhysicsComponent.class).setShapeHeight(2.5f/2);
        testingEntity.getComponent(PhysicsComponent.class).setShapeWidth(2.5f/2);
        testingEntity.addComponent(new CameraComponent(1));
        gw.getUIManager().addUIElement(new GameSpaceTextUIElement(
                -1,
                -1,
                0,
                0,
                testingEntity,
                "Lvl 1",
                32,
                gw.getResourceLoader().loadFont("fonts/mini4.ttf"),
                Color.WHITE
        ));
        gw.addEntity(testingEntity);

        Entity testingEntity2 = new Entity();
        GrottoRenderComponent grottoRenderComponent2 = new GrottoRenderComponent();
        grottoRenderComponent2.setResourceIds(Arrays.asList(R.drawable.column));
        grottoRenderComponent2.setHeight(4f);
        grottoRenderComponent2.setWidth(2f);
        testingEntity2.addComponent(grottoRenderComponent2);
        testingEntity2.addComponent(new PhysicsComponent());
        testingEntity2.getComponent(PhysicsComponent.class).setX(5);
        testingEntity2.getComponent(PhysicsComponent.class).setY(-10);
        testingEntity2.getComponent(PhysicsComponent.class).setBodyType(BodyType.staticBody);
        testingEntity2.getComponent(PhysicsComponent.class).setShapeHeight(1f);
        testingEntity2.getComponent(PhysicsComponent.class).setShapeWidth(1f);

        gw.addEntity(testingEntity2);

        gw.getUIManager().addUIElement(new TextUIElement(
                20,
                20,
                0,
                0,
                "Grotto",
                32,
                gw.getResourceLoader().loadFont("fonts/mini4.ttf"),
                Color.WHITE
        ));

        /* gw.addGameObject(new EnclosureGO(gw, XMIN, XMAX, YMIN, YMAX));
        gw.addGameObject(new DynamicBoxGO(gw, 0, 0));
        gw.addGameObject(new DynamicBoxGO(gw, 5, 0));
        gw.addGameObject(new DynamicTriangleGO(gw, 7, 3));
        gw.addGameObject(new MarblesGO(gw, 0, 5)); */

        //GameObject a = gw.addGameObject(new DynamicBoxGO(gw, 0, -2));
        //GameObject b = gw.addGameObject(new DynamicBoxGO(gw, 1, -3));
        //new MyRevoluteJoint(gw, a.body, b.body);
        // new MyPrismaticJoint(gw, a.body, b.body);

        // Just for info
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        float refreshRate = display.getRefreshRate();
        Log.i(getString(R.string.app_name), "Refresh rate =" + refreshRate);

        // Accelerometer
        SensorManager smanager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (smanager.getSensorList(Sensor.TYPE_ACCELEROMETER).isEmpty()) {
            Log.i(getString(R.string.app_name), "No accelerometer");
        } else {
            Sensor accelerometer = smanager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            if (!smanager.registerListener(new AccelerometerListener(physicsSystem), accelerometer, SensorManager.SENSOR_DELAY_NORMAL))
                Log.i(getString(R.string.app_name), "Could not register accelerometer listener");
        }

        Log.i(getString(R.string.app_name), "onCreate complete, Endianness = " +
                (ByteOrder.nativeOrder()==ByteOrder.BIG_ENDIAN? "Big Endian" : "Little Endian"));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("Main thread", "pause");

        renderView.pause(); // stops the main loop

        // persistence example
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("Main thread", "stop");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Main thread", "resume");

        renderView.resume(); // starts game loop in a separate thread

        // persistence example
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        int counter = pref.getInt(getString(R.string.important_info), -1); // default value
        Log.i("Main thread", "read counter " + counter);
    }
}
