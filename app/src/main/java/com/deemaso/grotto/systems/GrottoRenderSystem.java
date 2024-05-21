package com.deemaso.grotto.systems;

import com.deemaso.core.Box;
import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.components.RenderComponent;
import com.deemaso.core.events.Event;
import com.deemaso.core.events.EventEmitter;
import com.deemaso.core.events.EventListener;
import com.deemaso.core.systems.RenderSystem;
import com.deemaso.grotto.GrottoGameWorld;
import com.deemaso.grotto.components.CameraComponent;
import com.deemaso.grotto.components.GrottoRenderComponent;
import com.deemaso.grotto.components.PhysicsComponent;
import com.deemaso.grotto.data.ResourceLoader;
import com.deemaso.grotto.events.CurrentViewEvent;
import com.deemaso.grotto.utils.RenderUtils;
import com.google.fpl.liquidfun.Body;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    * Manages rendering of entities in the Grotto game.
    * It uses Canvas and Bitmap from Android SDK to draw entities.
    * It also uses the PhysicsComponent to determine the position of entities.
 */
public class GrottoRenderSystem extends RenderSystem implements EventEmitter {

    private final Canvas canvas;
    private final Bitmap buffer;

    public GrottoRenderSystem(
            GameWorld gameWorld,
            Box physicalSize,
            Box screenSize,
            Bitmap buffer
    ) {
        super(
                gameWorld,
                Arrays.asList(GrottoRenderComponent.class, PhysicsComponent.class),
                physicalSize,
                screenSize
        );
        this.buffer = buffer;
        this.canvas = new Canvas(buffer);
    }

    @Override
    public boolean registerEntity(Entity entity) {
        if(!super.registerEntity(entity)){
            return false;
        }
        GrottoRenderComponent render = entity.getComponent(GrottoRenderComponent.class);
        render.setScreenSemiHeight(RenderUtils.toPixelsYLength(render.getHeight(), currentView.height, buffer.getHeight())/2);
        render.setScreenSemiWidth(RenderUtils.toPixelsXLength(render.getWidth(), currentView.width, buffer.getWidth())/2);
        render.setCanvas(new Canvas(buffer));
        render.setPaint(new Paint());
        render.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        render.getPaint().setAntiAlias(false);
        render.getPaint().setFilterBitmap(false);
        //int g = (int)(255*Math.random());
        //render.getPaint().setColor(Color.BLACK);

        if (gameWorld instanceof GrottoGameWorld) {
            ResourceLoader resourceLoader = ((GrottoGameWorld) gameWorld).getResourceLoader();
            render.setBitmaps(resourceLoader.loadBitmaps(render.getResourceIds()));
            render.getSrc().set(0, 0, render.getBitmap(0).getWidth(), render.getBitmap(0).getHeight());
        }

        return true;
    }

    @Override
    protected void finalize() {}

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void update(float deltaTime) {
        canvas.drawColor(Color.BLACK);
        super.update(deltaTime);
    }

    @Override
    public boolean draw(Entity entity, float deltaTime) {
        RenderComponent render = entity.getComponent(GrottoRenderComponent.class);
        PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
        // Only last created camera component is considered
        CameraComponent camera = entity.getComponent(CameraComponent.class);

        final Body body = physics.getBody();
        if(body != null) {
            float x = body.getPositionX();
            float y = body.getPositionY();
            float angle = body.getAngle();
            float velocityX = body.getLinearVelocity().getX();
            float velocityY = body.getLinearVelocity().getY();

            if(camera != null){
                final float zoom = camera.getZoom();
                currentView.xmin = x - currentView.width / 2 / zoom;
                currentView.xmax = x + currentView.width / 2 / zoom;
                currentView.ymin = y - currentView.height / 2 / zoom;
                currentView.ymax = y + currentView.height / 2 / zoom;
            }

            // Emit the current view event, so that other systems can use it if needed
            emitEvent(new CurrentViewEvent(currentView));

            if(x > currentView.xmin && x < currentView.xmax &&
                    y > currentView.ymin && y < currentView.ymax) {
                float screen_x = RenderUtils.toPixelsX(x, currentView.xmin, currentView.width, buffer.getWidth());
                float screen_y = RenderUtils.toPixelsY(y, currentView.ymin, currentView.height, buffer.getHeight());
                draw(render, screen_x, screen_y, angle, velocityX, velocityY, deltaTime);
                return true;
            }
        }
        else{
            draw(render, 0, 0, 0, 0, 0, deltaTime);
            return true;
        }
        return false;
    }

    private void draw(RenderComponent render, float x, float y, float angle, float velocityX, float velocityY, float deltaTime) {
        GrottoRenderComponent grottoRender = (GrottoRenderComponent) render;
        grottoRender.getCanvas().save();
        grottoRender.getCanvas().rotate((float) Math.toDegrees(angle), x, y);
        Bitmap nextFrame = grottoRender.getNextFrame(deltaTime);

        // Flip the bitmap if the entity is moving to the left (do it only if the entity has a related component)
        if (velocityX < 0) {
            Matrix flipHorizontalMatrix = new Matrix();
            flipHorizontalMatrix.setScale(-1, 1);
            flipHorizontalMatrix.postTranslate(nextFrame.getWidth(), 0);
            nextFrame = Bitmap.createBitmap(nextFrame, 0, 0, nextFrame.getWidth(), nextFrame.getHeight(), flipHorizontalMatrix, true);
        }

        grottoRender.getDst().set(
                x - grottoRender.getScreenSemiWidth(),
                y - grottoRender.getScreenSemiHeight(),
                x + grottoRender.getScreenSemiWidth(),
                y + grottoRender.getScreenSemiHeight()
        );
        canvas.drawBitmap(nextFrame, grottoRender.getSrc(), grottoRender.getDst(), grottoRender.getPaint());
        grottoRender.getCanvas().restore();
    }

    public Bitmap getBuffer() {
        return buffer;
    }

    final private static Map<Class<? extends Event>, List<EventListener>> listeners = new HashMap<>();

    @Override
    public Map<Class<? extends Event>, List<EventListener>> getListeners() {
        return listeners;
    }

    @Override
    public <T extends Event> void emitEvent(T event) {
        EventEmitter.super.emitEvent(event);
    }

    @Override
    public <T extends Event> void registerListener(Class<T> eventType, EventListener<T> listener) {
        EventEmitter.super.registerListener(eventType, listener);
    }
}
