package com.deemaso.grotto.listeners;

import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.collisions.Collision;
import com.deemaso.core.collisions.CollisionPool;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.Collection;
import java.util.HashSet;

/**
 * A listener for collisions.
 */
public class CollisionListener implements ContactListener {

    private final CollisionPool collisionPool;
    private final Collection<Collision> cache = new HashSet<>();

    /**
     * Creates a new collision listener.
     * @param collisionPool The collision pool
     */
    public CollisionListener(CollisionPool collisionPool) {
        this.collisionPool = collisionPool;
    }

    /**
     * Gets the collisions.
     * @return The collisions
     */
    public Collection<Collision> getCollisions() {
        Collection<Collision> result = new HashSet<>(cache);
        cache.clear();
        return result;
    }

    /* Warning: this method runs inside world.step
     *  Hence, it cannot change the physical world.
     */
    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA(),
                fb = contact.getFixtureB();
        Body ba = fa.getBody(), bb = fb.getBody();
        Object userdataA = ba.getUserData(), userdataB = bb.getUserData();
        Entity a = (Entity) userdataA,
                b = (Entity) userdataB;

        // TO DO: use an object pool instead
        Collision collision = collisionPool.get(a, b);
        cache.add(collision);

        // Sound sound = CollisionSounds.getSound(a.getClass(), b.getClass());
        //if (sound!=null)
        //    sound.play(0.7f);
        // Log.d("MyContactListener", "contact bwt " + a.name + " and " + b.name);
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA(),
                fb = contact.getFixtureB();
        Body ba = fa.getBody(), bb = fb.getBody();
        Object userdataA = ba.getUserData(), userdataB = bb.getUserData();
        Entity a = (Entity) userdataA,
                b = (Entity) userdataB;

        Collision collision = collisionPool.get(a, b);
        cache.remove(collision);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
