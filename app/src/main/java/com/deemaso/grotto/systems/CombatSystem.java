package com.deemaso.grotto.systems;

import android.util.Log;

import com.deemaso.core.Entity;
import com.deemaso.core.GameWorld;
import com.deemaso.core.collisions.Collision;
import com.deemaso.core.components.Component;
import com.deemaso.core.events.EventListener;
import com.deemaso.core.events.SystemEvent;
import com.deemaso.core.systems.System;
import com.deemaso.grotto.components.CharacterStatsComponent;
import com.deemaso.grotto.components.LimitedLifeComponent;
import com.deemaso.grotto.components.PhysicsComponent;
import com.deemaso.grotto.components.WeaponComponent;
import com.deemaso.grotto.items.MeleeWeapon;
import com.deemaso.grotto.items.RangedWeapon;
import com.deemaso.grotto.items.Weapon;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * A combat system.
 */
public class CombatSystem extends System implements EventListener {

    private final Collection<MeleeAttack> meleeAttacks = new HashSet<>();
    private final Queue<Hit> hits = new LinkedList<>();

    /**
     * A melee attack. Used to animate melee attacks.
     */
    private static class MeleeAttack {
        private final Entity attacker;
        private final Entity weapon;
        private float time;
        private final float angularVelocity;

        public MeleeAttack(Entity attacker, Entity weapon, float angularVelocity){
            this.attacker = attacker;
            this.weapon = weapon;
            this.time = 0;
            this.angularVelocity = angularVelocity;
        }

        public Entity getAttacker(){
            return attacker;
        }

        public Entity getWeapon(){
            return weapon;
        }

        public float getTime(){
            return time;
        }

        public void setTime(float time){
            this.time = time;
        }

        public float getAngularVelocity() {
            return angularVelocity;
        }
    }

    /**
     * A hit. Used to store hits.
     */
    private static class Hit {
        private final Entity weapon;
        private final Entity entity;

        public Hit(Entity weapon, Entity entity) {
            this.weapon = weapon;
            this.entity = entity;
        }

        public Entity getWeapon() {
            return weapon;
        }

        public Entity getEntity() {
            return entity;
        }
    }

    protected CombatSystem(GameWorld gameWorld, List<Class<? extends Component>> requiredComponents, boolean requireAllComponents) {
        super(gameWorld, requiredComponents, requireAllComponents);
    }

    /**
     * Creates a new combat system.
     * @param gameWorld The game world
     */
    public CombatSystem(GameWorld gameWorld){
        super(gameWorld, Arrays.asList(CharacterStatsComponent.class), true);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        solveMeleeAttacks(dt);
        solveHits();
    }

    private void solveMeleeAttacks(float dt){
        for(MeleeAttack attack : meleeAttacks){
            attack.setTime(attack.getTime() + dt);
            float newAngle = attack.getTime() * attack.getAngularVelocity();
            float radius = 1;
            Body attackerBody = attack.getAttacker().getComponent(PhysicsComponent.class).getBody();
            Body weaponBody = attack.getWeapon().getComponent(PhysicsComponent.class).getBody();
            float newX = attackerBody.getPosition().x + (float) (radius * Math.cos(newAngle));
            float newY = attackerBody.getPosition().y + (float) (radius * Math.sin(newAngle));
            // Add pi/2 to the angle to make the weapon point to the right direction
            weaponBody.setTransform(new Vec2(newX,newY), newAngle + ((float) Math.PI / 2));
        }
    }

    private void solveHits(){
        Hit hit = hits.poll();
        if(hit != null){
            Entity weapon = hit.getWeapon();
            Entity entity = hit.getEntity();
            if(weapon.hasComponent(WeaponComponent.class) && entity.hasComponent(CharacterStatsComponent.class)){
                Log.d("CombatSystem", "Entity " + entity.getId() + " hit by weapon " + weapon.getId() + ".");
                int damageTaken = 0;
                WeaponComponent weaponComponent = weapon.getComponent(WeaponComponent.class);
                CharacterStatsComponent characterStatsComponent = entity.getComponent(CharacterStatsComponent.class);

                if(weaponComponent.getWeapon() instanceof MeleeWeapon){ // Handle melee weapons
                    MeleeWeapon meleeWeapon = (MeleeWeapon) weaponComponent.getWeapon();
                    damageTaken = meleeWeapon.getDamage();
                    Body entityBody = entity.getComponent(PhysicsComponent.class).getBody();
                    int knockbackPower = (int) (meleeWeapon.getKnockback());
                    Vec2 impulse = new Vec2(entityBody.getPosition().x - weapon.getComponent(PhysicsComponent.class).getBody().getPosition().x,
                            entityBody.getPosition().y - weapon.getComponent(PhysicsComponent.class).getBody().getPosition().y);
                    impulse.normalize();
                    impulse.mulLocal(knockbackPower);
                    entityBody.applyLinearImpulse(impulse, entityBody.getPosition());
                }
                else{ // Handle ranged and collision weapons
                    damageTaken = weaponComponent.getWeapon().getDamage();
                }

                characterStatsComponent.setStat("health", (int) characterStatsComponent.getStat("health") - damageTaken);
                SystemEvent event = new SystemEvent("STAT_UPDATED");
                event.put("entity", entity);
                event.put("stat", "health");
                event.put("value", characterStatsComponent.getStat("health"));
                gameWorld.broadcastEvent(event);
                if((int) characterStatsComponent.getStat("health") <= 0){
                    characterStatsComponent.setAlive(false);
                }
                Log.d("CombatSystem", "Entity " + entity.getId() + " took " + damageTaken + " damage. Health: " + characterStatsComponent.getStat("health"));
            }
        }
    }

    private void solveCollision(Entity weapon, Entity target){
        if (weapon.hasComponent(WeaponComponent.class) && target.hasComponent(CharacterStatsComponent.class)) {
            WeaponComponent weaponComponent = weapon.getComponent(WeaponComponent.class);
            boolean isCollisionWeapon = !(weaponComponent.getWeapon() instanceof MeleeWeapon) && !(weaponComponent.getWeapon() instanceof RangedWeapon);

            boolean isNotSameFaction = !weaponComponent.getIgnoreFactions().contains(target.getComponent(CharacterStatsComponent.class).getStat("faction"));
            if (weaponComponent.getOwner() != target && isNotSameFaction) {
                if (weapon.hasComponent(CharacterStatsComponent.class) && isCollisionWeapon) {
                    hits.add(new Hit(weapon, target));
                } else if (!weapon.hasComponent(CharacterStatsComponent.class) && !isCollisionWeapon) {
                    hits.add(new Hit(weapon, target));
                } else if (!weapon.hasComponent(CharacterStatsComponent.class) && isCollisionWeapon) {
                    hits.add(new Hit(weapon, target));
                }
            }
        }
    }

    @Override
    public void onEvent(SystemEvent event) {
        if(event.getCode().equals("COLLISION")) {
            Collision collision = (Collision) event.get("collision");
            Entity entity1 = collision.getA();
            Entity entity2 = collision.getB();

            solveCollision(entity1, entity2);
            solveCollision(entity2, entity1);
        }

        if(event.getCode().equals("ATTACK")){
            Entity attacker = (Entity) event.get("attacker");
            if(attacker != null && attacker.hasComponent(WeaponComponent.class)){
                WeaponComponent weaponComponent = attacker.getComponent(WeaponComponent.class);
                if(weaponComponent.getWeapon() instanceof MeleeWeapon){
                    MeleeWeapon meleeWeapon = (MeleeWeapon) weaponComponent.getWeapon();
                    long currentTime = java.lang.System.currentTimeMillis();
                    if(currentTime - weaponComponent.getLastFired() < meleeWeapon.getSlashSpeed() * 1000){
                        return; // Handle cooldown (prevent spamming)
                    }
                    weaponComponent.setLastFired(currentTime);
                    Collection<Component> extraComponents = new ArrayList<>();
                    extraComponents.add(new LimitedLifeComponent(meleeWeapon.getSlashSpeed()));
                    WeaponComponent extraWeaponComponent = new WeaponComponent(meleeWeapon, Arrays.asList());
                    extraWeaponComponent.setOwner(attacker);
                    extraComponents.add(extraWeaponComponent);
                    Entity w = gameWorld.createEntityById(meleeWeapon.getArchetype(), extraComponents);
                    Body weaponBody = w.getComponent(PhysicsComponent.class).getBody();
                    Body attackerBody = attacker.getComponent(PhysicsComponent.class).getBody();

                    weaponBody.setTransform(attackerBody.getPosition(), 0);
                    meleeAttacks.add(new MeleeAttack(attacker, w, (float)(2 * Math.PI) / meleeWeapon.getSlashSpeed()));
                }
                else if(weaponComponent.getWeapon() instanceof RangedWeapon){
                    Log.d("COMBAT", "Ranged weapon attack");
                }
            }
        }
    }

    @Override
    public boolean registerEntity(Entity entity) {
        if(!super.registerEntity(entity)) return false;
        if(entity.hasComponent(WeaponComponent.class)){
            WeaponComponent weaponComponent = entity.getComponent(WeaponComponent.class);
            if(weaponComponent != null){
                weaponComponent.setOwner(entity);
            }
        }
        return true;
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
}
