package com.deemaso.grotto.systems;

import android.util.Log;

import com.deemaso.core.GameWorld;
import com.deemaso.core.collisions.Collision;
import com.deemaso.core.events.Event;
import com.deemaso.core.events.EventEmitter;
import com.deemaso.core.events.EventListener;
import com.deemaso.core.systems.CollisionSystem;
import com.deemaso.grotto.components.PhysicsComponent;
import com.deemaso.grotto.events.CollisionEvent;
import com.deemaso.grotto.listeners.CollisionListener;

import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GrottoCollisionSystem extends CollisionSystem implements EventEmitter {

    private final ContactListener contactListener;
    private final Map<Class<? extends Event>, List<EventListener>> listeners = new java.util.HashMap<>();

    public GrottoCollisionSystem(GameWorld gameWorld) {
        super(gameWorld, Arrays.asList(PhysicsComponent.class));
        contactListener = new CollisionListener(collisionPool);
    }

    public GrottoCollisionSystem(GameWorld gameWorld, ContactListener contactListener) {
        super(gameWorld, Arrays.asList(PhysicsComponent.class));
        this.contactListener = contactListener;
    }

    @Override
    protected Collection<Collision> getCollisions() {
        return ((CollisionListener) contactListener).getCollisions();
    }

    @Override
    protected void handleCollision(Collision collision) {
        Log.d("CollisionSystem", "Collision detected");
        emitEvent(new CollisionEvent(collision));

    }

    public ContactListener getContactListener() {
        return contactListener;
    }

    @Override
    protected void finalize() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

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
