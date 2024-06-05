package com.deemaso.grotto.systems;

import com.deemaso.core.Box;
import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.components.RenderComponent;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.core.systems.RenderSystem;
import com.deemaso.grotto.GrottoGameWorld;
import com.deemaso.grotto.components.CameraComponent;
import com.deemaso.grotto.components.GrottoRenderComponent;
import com.deemaso.grotto.components.PhysicsComponent;
import com.deemaso.grotto.components.PlayerComponent;
import com.deemaso.grotto.components.WeaponComponent;
import com.deemaso.grotto.data.ResourceLoader;
import com.deemaso.grotto.utils.RenderUtils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import org.jbox2d.dynamics.Body;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * A render system for Project Grotto.
 * This system renders entities with a GrottoRenderComponent.
 * It uses a buffer to draw the entities (Android Bitmap).
 */
public class GrottoRenderSystem extends RenderSystem {

    private final Canvas canvas;
    private final Bitmap buffer;

    /**
     * Creates a new render system.
     * @param gameWorld The game world
     * @param physicalSize The physical size
     * @param screenSize The screen size
     * @param buffer The buffer
     */
    public GrottoRenderSystem(
            GameWorld gameWorld,
            Box physicalSize,
            Box screenSize,
            Bitmap buffer
    ) {
        super(
                gameWorld,
                Arrays.asList(GrottoRenderComponent.class),
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
            render.setBitmaps(resourceLoader.loadBitmapAssets(render.getResourceIds()));
            render.getSrc().set(0, 0, render.getBitmap(0).getWidth(), render.getBitmap(0).getHeight());
        }

        sortEntitiesByZIndex();

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
    protected void sortEntitiesByZIndex() {
        Collections.sort(entities, new Comparator<Entity>() {
            @Override
            public int compare(Entity e1, Entity e2) {
                GrottoRenderComponent r1 = e1.getComponent(GrottoRenderComponent.class);
                GrottoRenderComponent r2 = e2.getComponent(GrottoRenderComponent.class);
                return r1.getZIndex() - r2.getZIndex();
            }
        });
    }

    @Override
    public boolean draw(Entity entity, float deltaTime) {
        RenderComponent render = entity.getComponent(GrottoRenderComponent.class);
        PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
        // Only the last created camera component is considered
        CameraComponent camera = entity.getComponent(CameraComponent.class);

        Body body = null;
        if(physics != null) {
            body = physics.getBody();
        }
        if(body != null) {
            float x = body.getPosition().x;
            float y = body.getPosition().y;
            float angle = body.getAngle();
            float velocityX = body.getLinearVelocity().x;
            float velocityY = body.getLinearVelocity().y;

            if(camera != null){
                final float zoom = camera.getZoom();
                currentView.xmin = x - currentView.width / 2 / zoom;
                currentView.xmax = x + currentView.width / 2 / zoom;
                currentView.ymin = y - currentView.height / 2 / zoom;
                currentView.ymax = y + currentView.height / 2 / zoom;
            }

            // Emit the current view event, so that other systems can use it if needed
            SystemEvent event = new SystemEvent("CURRENT_VIEW");
            event.put("currentView", currentView);
            gameWorld.broadcastEvent(event);

            if(x > currentView.xmin && x < currentView.xmax &&
                    y > currentView.ymin && y < currentView.ymax) {
                float screen_x = RenderUtils.toPixelsX(x, currentView.xmin, currentView.width, buffer.getWidth());
                float screen_y = RenderUtils.toPixelsY(y, currentView.ymin, currentView.height, buffer.getHeight());
                draw(render, screen_x, screen_y, angle, velocityX, velocityY, deltaTime);
                return true;
            }
        }
        else{
            float x = render.getX();
            float y = render.getY();
            if(x > currentView.xmin && x < currentView.xmax &&
                    y > currentView.ymin && y < currentView.ymax) {
                float screen_x = RenderUtils.toPixelsX(x, currentView.xmin, currentView.width, buffer.getWidth());
                float screen_y = RenderUtils.toPixelsY(y, currentView.ymin, currentView.height, buffer.getHeight());
                draw(render, screen_x, screen_y, 0, 0, 0, deltaTime);
                return true;
            }
        }
        return false;
    }

    private void draw(RenderComponent render, float x, float y, float angle, float velocityX, float velocityY, float deltaTime) {
        GrottoRenderComponent grottoRender = (GrottoRenderComponent) render;
        canvas.save();
        if(grottoRender.isConsiderAngle()){
            canvas.rotate((float) Math.toDegrees(angle), x, y);
        }
        Bitmap nextFrame = grottoRender.getNextFrame(deltaTime, velocityX < 0);

        grottoRender.getDst().set(
                x - grottoRender.getScreenSemiWidth(),
                y - grottoRender.getScreenSemiHeight(),
                x + grottoRender.getScreenSemiWidth(),
                y + grottoRender.getScreenSemiHeight()
        );
        canvas.drawBitmap(nextFrame, grottoRender.getSrc(), grottoRender.getDst(), grottoRender.getPaint());
        canvas.restore();
    }

    public Bitmap getBuffer() {
        return buffer;
    }

    @Override
    public void onEvent(SystemEvent event) {

    }
}
