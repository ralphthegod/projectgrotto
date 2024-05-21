package com.deemaso.grotto;

import android.app.Activity;

import com.deemaso.core.GameWorld;
import com.deemaso.core.EntityManager;
import com.deemaso.core.systems.System;
import com.deemaso.grotto.data.ResourceLoader;
import com.deemaso.grotto.ui.UIManager;

import java.util.List;

public class GrottoGameWorld extends GameWorld{

    final private Activity activity;
    final private ResourceLoader resourceLoader;
    private UIManager uiManager;

    public GrottoGameWorld(EntityManager entityManager, Activity activity) {
        super(entityManager);
        this.activity = activity;
        this.resourceLoader = new ResourceLoader(activity.getResources());
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

    public Activity getActivity() {
        return activity;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
