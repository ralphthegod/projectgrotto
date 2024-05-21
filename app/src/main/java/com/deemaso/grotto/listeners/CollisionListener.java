package com.deemaso.grotto.listeners;

import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.collisions.Collision;
import com.deemaso.core.collisions.CollisionPool;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Contact;
import com.google.fpl.liquidfun.ContactListener;
import com.google.fpl.liquidfun.Fixture;

import java.util.Collection;
import java.util.HashSet;

public class CollisionListener extends ContactListener {

    private final CollisionPool collisionPool;
    private final Collection<Collision> cache = new HashSet<>();

    public CollisionListener(CollisionPool collisionPool) {
        this.collisionPool = collisionPool;
    }

    public Collection<Collision> getCollisions() {
        Collection<Collision> result = new HashSet<>(cache);
        cache.clear();
        return result;
    }

    /** Warning: this method runs inside world.step
     *  Hence, it cannot change the physical world.
     */
    @Override
    public void beginContact(Contact contact) {
        Log.d("GrottoContactListener", "Begin contact");
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
}
