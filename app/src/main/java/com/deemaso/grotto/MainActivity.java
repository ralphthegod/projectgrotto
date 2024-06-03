package com.deemaso.grotto;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.deemaso.core.Box;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.impl.AndroidAudio;
import com.badlogic.androidgames.framework.impl.MultiTouchHandler;
import com.deemaso.core.systems.InputSystem;
import com.deemaso.grotto.data.ResourceLoader;
import com.deemaso.grotto.listeners.AccelerometerListener;
import com.deemaso.grotto.systems.AISystem;
import com.deemaso.grotto.systems.CombatSystem;
import com.deemaso.grotto.systems.GrottoCollisionSystem;
import com.deemaso.grotto.systems.GrottoInputSystem;
import com.deemaso.grotto.systems.GrottoRenderSystem;
import com.deemaso.grotto.systems.LevelProgressionSystem;
import com.deemaso.grotto.systems.LevelSystem;
import com.deemaso.grotto.systems.MovementSystem;
import com.deemaso.grotto.systems.PerceptionSystem;
import com.deemaso.grotto.systems.PhysicsSystem;
import com.deemaso.grotto.systems.SoundSystem;
import com.deemaso.grotto.systems.TimeSystem;
import com.deemaso.grotto.ui.UIManager;
import com.deemaso.grotto.ui.elements.ExperienceCounterUIElement;
import com.deemaso.grotto.ui.elements.HealthBarUIElement;
import com.deemaso.grotto.ui.elements.PlayerDeathTextUIElement;
import com.deemaso.grotto.ui.elements.TextUIElement;
import com.example.mfaella.physicsapp.R;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.nio.ByteOrder;

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

        //CollisionSounds.init(audio);
        //Music backgroundMusic = audio.newMusic("soundtrack.mp3");
        //backgroundMusic.play();

        // Game world
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Box physicalSize = new Box(XMIN, YMIN, XMAX, YMAX),
            screenSize   = new Box(0, 0, metrics.widthPixels, metrics.heightPixels);

        ResourceLoader resourceLoader = new ResourceLoader(this.getResources());

        GrottoGameWorld gw = new GrottoGameWorld(new GrottoEntityManager(getApplicationContext()), this, resourceLoader);

        renderView = new AndroidFastRenderView(this, gw, metrics.widthPixels, metrics.heightPixels);
        setContentView(renderView);

        UIManager uiManager = new UIManager(renderView.getFrameBuffer(), screenSize, getApplicationContext(), resourceLoader);
        gw.setUIManager(uiManager);

        GrottoRenderSystem renderSystem = new GrottoRenderSystem(gw, physicalSize, screenSize, renderView.getFrameBuffer());

        Audio audio = new AndroidAudio(this);
        SoundSystem soundSystem = new SoundSystem(gw, audio);

        World physicsWorld = new World(new Vec2(0,0));
        PhysicsSystem physicsSystem = new PhysicsSystem(gw, physicsWorld, 8,3,3);
        PerceptionSystem perceptionSystem = new PerceptionSystem(gw, physicsWorld);
        GrottoCollisionSystem collisionSystem = new GrottoCollisionSystem(gw);
        physicsWorld.setContactListener(collisionSystem.getContactListener());

        MovementSystem movementSystem = new MovementSystem(gw);
        InputSystem inputSystem = new GrottoInputSystem(gw, new MultiTouchHandler(renderView, 1, 1));
        LevelSystem levelSystem = new LevelSystem(gw, true, "dungeon_1", 1f);
        CombatSystem combatSystem = new CombatSystem(gw);
        LevelProgressionSystem levelProgressionSystem = new LevelProgressionSystem(gw);
        AISystem aiSystem = new AISystem(gw);
        TimeSystem timeSystem = new TimeSystem(gw);

        ExperienceCounterUIElement experienceCounterUIElement = new ExperienceCounterUIElement(
                 10,
                30,
                25f,
                30f,
                resourceLoader.loadBitmapAsset("sprites/coin_icon.png"),
                32,
                resourceLoader.loadFont("fonts/mini4.ttf")
        );

        HealthBarUIElement healthBarUIElement = new HealthBarUIElement(
                20,
                80,
                50f,
                50f,
                resourceLoader.loadBitmapAsset("sprites/ui_heart_full.png"),
                resourceLoader.loadBitmapAsset("sprites/ui_heart_empty.png"),
                resourceLoader.loadBitmapAsset("sprites/ui_heart_half.png")
        );

        PlayerDeathTextUIElement playerDeathTextUIElement = new PlayerDeathTextUIElement(
                200,
                600,
                0,
                0,
                "You died!",
                72,
                resourceLoader.loadFont("fonts/mini4.ttf"),
                Color.RED
        );

        gw.getUIManager().addUIElement(experienceCounterUIElement);
        gw.getUIManager().addUIElement(playerDeathTextUIElement);
        gw.getUIManager().addUIElement(healthBarUIElement);

        // Order matters (input, physics, render...)
        gw.addSystem(inputSystem);
        gw.addSystem(physicsSystem);
        gw.addSystem(collisionSystem);
        //game logic systems
        gw.addSystem(perceptionSystem);
        gw.addSystem(aiSystem);
        gw.addSystem(levelSystem);
        gw.addSystem(combatSystem);
        gw.addSystem(movementSystem);
        gw.addSystem(levelProgressionSystem);
        gw.addSystem(timeSystem);
        gw.addSystem(soundSystem);
        //rendering
        gw.addSystem(renderSystem);

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
