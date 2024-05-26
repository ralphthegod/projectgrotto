package com.deemaso.grotto;

import android.app.Activity;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.EntityManager;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.grotto.data.ResourceLoader;
import com.deemaso.grotto.ui.UIManager;

public class GrottoGameWorld extends GameWorld{

    final private Activity activity;
    final private ResourceLoader resourceLoader;
    private UIManager uiManager;

    public GrottoGameWorld(EntityManager entityManager, Activity activity, ResourceLoader resourceLoader) {
        super(entityManager);
        this.activity = activity;
        this.resourceLoader = resourceLoader;
    }

    public void setUIManager(UIManager uiManager) {
        this.uiManager = uiManager;
    }

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

    public Activity getActivity() {
        return activity;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
