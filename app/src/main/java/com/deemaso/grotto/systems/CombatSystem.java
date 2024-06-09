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
import com.deemaso.grotto.components.SoundComponent;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * A combat system.
 */
public class CombatSystem extends System implements EventListener {

    private final Collection<MeleeAttack> meleeAttacks = new HashSet<>();
    private final Collection<Bullet> bullets = new HashSet<>();
    private final Queue<Hit> hits = new LinkedList<>();

    /**
     * A bullet. Used to store bullets.
     */
    private static class Bullet {
        private final Entity entity;
        private final long startTime;
        private final long maxTime;
        private boolean isBeingDeleted = false;

        public Bullet(Entity entity, long maxTime) {
            this.entity = entity;
            this.startTime = java.lang.System.currentTimeMillis();
            this.maxTime = maxTime;
        }

        public Entity getEntity() {
            return entity;
        }

        public long getStartTime() {
            return startTime;
        }

        public long getMaxTime() {
            return maxTime;
        }

        public boolean isBeingDeleted() {
            return isBeingDeleted;
        }

        public void setBeingDeleted(boolean beingDeleted) {
            isBeingDeleted = beingDeleted;
        }
    }

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
        solveBullets(dt);
    }

    private void solveBullets(float dt){
        for(Bullet bullet : bullets){
            if(bullet.getEntity() == null){
                bullets.remove(bullet);
                continue;
            }
            long currentTime = java.lang.System.currentTimeMillis();
            if(currentTime - bullet.getStartTime() > bullet.getMaxTime() && !bullet.isBeingDeleted()){
                bullet.setBeingDeleted(true);
                gameWorld.markEntityForDeletion(bullet.getEntity());
            }
        }
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

                int attackerLevel = 1;
                if (weaponComponent.getOwner() != null && weaponComponent.getOwner().hasComponent(CharacterStatsComponent.class)) {
                    attackerLevel = (int) weaponComponent.getOwner().getComponent(CharacterStatsComponent.class).getStat("level");
                }
                double levelModifier = calculateLevelModifier(attackerLevel);

                if(weaponComponent.getWeapon() instanceof MeleeWeapon){ // Handle melee weapons
                    MeleeWeapon meleeWeapon = (MeleeWeapon) weaponComponent.getWeapon();
                    damageTaken = (int) (meleeWeapon.getDamage() * levelModifier);
                    Body entityBody = entity.getComponent(PhysicsComponent.class).getBody();
                    int knockbackPower = (int) (meleeWeapon.getKnockback() * levelModifier);
                    Vec2 impulse = new Vec2(entityBody.getPosition().x - weapon.getComponent(PhysicsComponent.class).getBody().getPosition().x,
                            entityBody.getPosition().y - weapon.getComponent(PhysicsComponent.class).getBody().getPosition().y);
                    impulse.normalize();
                    impulse.mulLocal(knockbackPower);
                    entityBody.applyLinearImpulse(impulse, entityBody.getPosition());
                }
                else{ // Handle ranged and collision weapons
                    damageTaken = (int) (weaponComponent.getWeapon().getDamage() * levelModifier);
                    if(weaponComponent.getWeapon() instanceof RangedWeapon){
                        gameWorld.markEntityForDeletion(weapon);
                    }
                }

                characterStatsComponent.setStat("health", (int) characterStatsComponent.getStat("health") - damageTaken);
                SystemEvent event = new SystemEvent("STAT_UPDATED");
                event.put("entity", entity);
                event.put("stat", "health");
                event.put("value", characterStatsComponent.getStat("health"));
                gameWorld.broadcastEvent(event);
                if((int) characterStatsComponent.getStat("health") <= 0){
                    characterStatsComponent.setAlive(false);
                    SystemEvent combatEvent = new SystemEvent("COMBAT_END");
                    combatEvent.put("winner", weaponComponent.getOwner());
                    combatEvent.put("loser", entity);
                    gameWorld.broadcastEvent(combatEvent);
                }
                if(entity.hasComponent(SoundComponent.class)){
                    SystemEvent soundEvent = new SystemEvent("SOUND");
                    soundEvent.put("sound", entity.getComponent(SoundComponent.class).getSounds().get("damage"));
                    gameWorld.broadcastEvent(soundEvent);
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
        else if((weapon.hasComponent(WeaponComponent.class) && !weapon.hasComponent(CharacterStatsComponent.class)) &&
                (target.hasComponent(WeaponComponent.class) && !target.hasComponent(CharacterStatsComponent.class))){
            WeaponComponent weaponComponent = weapon.getComponent(WeaponComponent.class);
            WeaponComponent targetWeaponComponent = target.getComponent(WeaponComponent.class);
            boolean bothAreRangedOrMelee = (weaponComponent.getWeapon() instanceof MeleeWeapon && targetWeaponComponent.getWeapon() instanceof RangedWeapon) ||
                    (weaponComponent.getWeapon() instanceof RangedWeapon && targetWeaponComponent.getWeapon() instanceof MeleeWeapon);
            if(bothAreRangedOrMelee){
                if(weaponComponent.getWeapon() instanceof RangedWeapon && targetWeaponComponent.getWeapon() instanceof MeleeWeapon){
                    gameWorld.markEntityForDeletion(weapon);
                }
                else if(weaponComponent.getWeapon() instanceof MeleeWeapon && targetWeaponComponent.getWeapon() instanceof RangedWeapon){
                    gameWorld.markEntityForDeletion(target);
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
                int attackerLevel = 1;
                if (attacker.hasComponent(CharacterStatsComponent.class)) {
                    attackerLevel = (int) attacker.getComponent(CharacterStatsComponent.class).getStat("level");
                }
                float levelModifier = calculateLevelModifier(attackerLevel);

                if(weaponComponent.getWeapon() instanceof MeleeWeapon){
                    MeleeWeapon meleeWeapon = (MeleeWeapon) weaponComponent.getWeapon();
                    long currentTime = java.lang.System.currentTimeMillis();
                    if(currentTime - weaponComponent.getLastFired() < (meleeWeapon.getSlashSpeed() / levelModifier) * 1000){
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

                    if(attacker.hasComponent(SoundComponent.class)){
                        SystemEvent soundEvent = new SystemEvent("SOUND");
                        soundEvent.put("sound", attacker.getComponent(SoundComponent.class).getSounds().get("attack"));
                        gameWorld.broadcastEvent(soundEvent);
                    }

                    weaponBody.setTransform(attackerBody.getPosition(), 0);
                    meleeAttacks.add(new MeleeAttack(attacker, w, (float)(2 * Math.PI) / (meleeWeapon.getSlashSpeed() / levelModifier)));
                }
                else if(weaponComponent.getWeapon() instanceof RangedWeapon){
                    RangedWeapon rangedWeapon = (RangedWeapon) weaponComponent.getWeapon();
                    long currentTime = java.lang.System.currentTimeMillis();
                    if (currentTime - weaponComponent.getLastFired() < (rangedWeapon.getReloadTime() / levelModifier) * 1000) {
                        return;
                    }
                    weaponComponent.setLastFired(currentTime);

                    Collection<Component> extraComponents = new ArrayList<>();
                    WeaponComponent projectileWeaponComponent = new WeaponComponent(rangedWeapon, Arrays.asList());
                    projectileWeaponComponent.setOwner(attacker);
                    extraComponents.add(projectileWeaponComponent);
                    Entity projectile = gameWorld.createEntityById(rangedWeapon.getArchetype(), extraComponents);
                    Body projectileBody = projectile.getComponent(PhysicsComponent.class).getBody();
                    Body attackerBody = attacker.getComponent(PhysicsComponent.class).getBody();

                    if(attacker.hasComponent(SoundComponent.class)){
                        SystemEvent soundEvent = new SystemEvent("SOUND");
                        soundEvent.put("sound", attacker.getComponent(SoundComponent.class).getSounds().get("attack"));
                        gameWorld.broadcastEvent(soundEvent);
                    }

                    Vec2 direction = (Vec2) event.get("direction");
                    Vec2 velocity = new Vec2(direction);
                    velocity.normalize();
                    velocity.mulLocal(rangedWeapon.getBulletSpeed() * levelModifier);
                    float angle = (float) Math.atan2(direction.y, direction.x);
                    projectileBody.setTransform(attackerBody.getPosition(), angle + (float) Math.PI / 2);
                    projectileBody.setLinearVelocity(velocity);
                    bullets.add(new Bullet(projectile, 5000));
                }
            }
        }
    }

    private float calculateLevelModifier(int level) {
        return (float) (1.0 + (level - 1) * 0.01);
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
    public void unregisterEntity(Entity entity) {
        super.unregisterEntity(entity);
        if(entity.hasComponent(WeaponComponent.class)){
            Iterator<MeleeAttack> iterator = meleeAttacks.iterator();
            while (iterator.hasNext()) {
                MeleeAttack attack = iterator.next();
                if (attack.getAttacker() == entity) {
                    iterator.remove();
                    gameWorld.markEntityForDeletion(attack.getWeapon());
                }
            }
        }
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
