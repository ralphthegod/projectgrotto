package com.deemaso.grotto;

import android.app.Activity;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.EntityManager;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.grotto.data.ResourceLoader;
import com.deemaso.grotto.ui.UIManager;

/**
 * The game world for Project Grotto.
 * This class represents the game world in Project Grotto.
 */
public class GrottoGameWorld extends GameWorld{

    final private Activity activity;
    final private ResourceLoader resourceLoader;
    private UIManager uiManager;

    /**
     * Creates a new game world.
     * @param entityManager The entity manager
     * @param activity The activity
     * @param resourceLoader The resource loader
     */
    public GrottoGameWorld(EntityManager entityManager, Activity activity, ResourceLoader resourceLoader) {
        super(entityManager);
        this.activity = activity;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Sets the UI manager.
     * @param uiManager The UI manager
     */
    public void setUIManager(UIManager uiManager) {
        this.uiManager = uiManager;
    }

    /**
     * Gets the UI manager.
     * @return The UI manager
     */
    public UIManager getUIManager() {
        return uiManager;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if(uiManager != null) {
            uiManager.draw();
        }
    }

    @Override
    public void broadcastEvent(SystemEvent event) {
        super.broadcastEvent(event);
        if(uiManager != null) {
            uiManager.onEvent(event);
        }
    }

    @Override
    public void addEntity(Entity entity) {
        super.addEntity(entity);
        if(uiManager != null) {
            uiManager.createEntityGameSpaceUIElements(entity);
        }
    }

    @Override
    public void markEntityForDeletion(Entity e) {
        super.markEntityForDeletion(e);
        if(uiManager != null) {
            uiManager.removeEntityGameSpaceUIElements(e);
        }
    }

    /**
     * Gets the activity.
     * @return The activity
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Gets the resource loader.
     * @return The resource loader
     */
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
