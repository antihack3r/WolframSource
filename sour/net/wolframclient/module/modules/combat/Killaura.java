/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 */
package net.wolframclient.module.modules.combat;

import java.lang.constant.Constable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WMath;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.compatibility.WPlayer;
import net.wolframclient.compatibility.WPlayerController;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.NetworkManagerPacketSendEvent;
import net.wolframclient.event.events.PostMotionUpdateEvent;
import net.wolframclient.event.events.PreMotionUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;
import net.wolframclient.storage.SettingData;
import net.wolframclient.utils.EntityHelper;
import net.wolframclient.utils.MillisTimer;
import org.lwjgl.input.Mouse;

public class Killaura
extends ModuleListener {
    int nextDelay = 1000;
    Random random = new Random();
    public List<EntityLivingBase> entities = new CopyOnWriteArrayList<EntityLivingBase>();
    public Entity target;
    private float prePitch;
    private float preYaw;
    private final MillisTimer time = new MillisTimer();
    private final Minecraft mc = Minecraft.getMinecraft();
    public static List<SettingData> settingsData = new ArrayList<SettingData>();
    private ArrayDeque<EntityLivingBase> fakeEntities = new ArrayDeque();

    public Killaura() {
        super("KillAura", Module.Category.COMBAT, "Fully customizable Aura");
        settingsData.add(new SettingData(this.getSettings(), "aura_rotspeed", 40.0f, 0.0f, 180.0f, 1.0f, "Rotation Speed", "Speed of player's rotation when Lock View enabled"));
        settingsData.add(new SettingData(this.getSettings(), "aura_rotlimit", 80.0f, 0.0f, 180.0f, 1.0f, "Rotation Limit", "Angle at which ixAura still attacks enemies"));
        if (WMinecraft.COOLDOWN) {
            settingsData.add(new SettingData(this.getSettings(), "aura_cooldown", true, "Cooldown", "Use attack cooldown as speed (disables Hit Speed slider)"));
        }
        settingsData.add(new SettingData(this.getSettings(), "aura_speed", 11.0f, 0.0f, 20.0f, 1.0f, "Hit Speed", "Attack speed"));
        settingsData.add(new SettingData(this.getSettings(), "aura_reach", 4.2f, 2.0f, 8.0f, 0.1f, "Hit Reach", "Maximum attack range"));
        settingsData.add(new SettingData(this.getSettings(), "aura_bypass", true, "Watchdog Bypass", "Bypass Watchdog, Gwen, etc."));
        settingsData.add(new SettingData(this.getSettings(), "aura_miss_ratio", 0.1f, 0.0f, 1.0f, 0.001f, "Miss rate", "Sets a miss/hit ratio."));
        settingsData.add(new SettingData(this.getSettings(), "aura_randomizer", true, "Speed Randomizer", "Randomize attack delays"));
        settingsData.add(new SettingData(this.getSettings(), "aura_lockview", false, "Lock View", "Always look at your enemy"));
        settingsData.add(new SettingData(this.getSettings(), "aura_players", true, "Players", "Attack players"));
        settingsData.add(new SettingData(this.getSettings(), "aura_neutral_mobs", true, "Neutral Mobs", "Attack neutral mobs"));
        settingsData.add(new SettingData(this.getSettings(), "aura_hostile_mobs", true, "Hostile Mobs", "Attack hostile mobs"));
        settingsData.add(new SettingData(this.getSettings(), "aura_tame_animals", false, "Tamable Mobs", "Attack tamable animals"));
        settingsData.add(new SettingData(this.getSettings(), "aura_passive_animals", true, "Passive Mobs", "Attack passive animals"));
        settingsData.add(new SettingData(this.getSettings(), "aura_silverfish", true, "Silverfish (MineZ)", "Attack silverfish"));
        for (SettingData data : settingsData) {
            data.storage.set(data.setting, data.isFloat ? (Constable)Float.valueOf(data.value) : (Constable)Boolean.valueOf(data.value == 1.0f));
        }
    }

    @EventTarget
    public void preUpdate(PreMotionUpdateEvent event) {
        this.prePitch = WMinecraft.getPlayer().rotationPitch;
        this.preYaw = WMinecraft.getPlayer().rotationYaw;
        if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && Mouse.getEventButton() == 2) {
            Entity entity = this.mc.objectMouseOver.entityHit;
            if (!(entity instanceof EntityPlayer)) {
                return;
            }
            if (Wolfram.getWolfram().relationManager.isFriend((EntityPlayer)entity)) {
                return;
            }
        }
        if (this.entities.isEmpty()) {
            this.populateEntities();
        }
        this.target = null;
        for (EntityLivingBase e : this.entities) {
            if (this.canAttackEntity(e)) {
                this.target = e;
                EntityHelper.faceEntity(e, this.getSettings().getInt("aura_rotspeed"));
                this.attackEntity(e);
                break;
            }
            this.entities.remove(e);
        }
        if (this.entities.isEmpty()) {
            this.populateEntities();
        }
    }

    @EventTarget
    public void postUpdate(PostMotionUpdateEvent event) {
        if (!this.getSettings().getBoolean("aura_lockview")) {
            WMinecraft.getPlayer().rotationPitch = this.prePitch;
            WMinecraft.getPlayer().rotationYaw = this.preYaw;
        }
    }

    @EventTarget
    public void onPacket(NetworkManagerPacketSendEvent event) {
        if (event.getPacket() instanceof CPacketPlayer && this.target != null && !this.getSettings().getBoolean("aura_lockview")) {
            EntityHelper.faceEntity(this.target, this.getSettings().getInt("aura_rotspeed"));
        }
    }

    public void populateEntities() {
        for (Object o : WMinecraft.getWorld().loadedEntityList) {
            EntityLivingBase entity;
            if (!(o instanceof EntityLivingBase) || !this.canAttackEntity(entity = (EntityLivingBase)o)) continue;
            this.entities.add(entity);
        }
    }

    public boolean canAttackEntity(EntityLivingBase entity) {
        EntityTameable tameable;
        if (!entity.isEntityAlive()) {
            return false;
        }
        if (entity.isDead) {
            return false;
        }
        if (!WMinecraft.getPlayer().canEntityBeSeen(entity)) {
            return false;
        }
        boolean friend = Wolfram.getWolfram().relationManager.isFriend(entity);
        if (entity == WMinecraft.getPlayer()) {
            return false;
        }
        if (friend) {
            return false;
        }
        if (entity.deathTime > 0) {
            return false;
        }
        if (!(entity instanceof EntityZombie) && entity.ticksExisted < 20) {
            return false;
        }
        if (this.getSettings().getBoolean("aura_bypass")) {
            if (entity.isInvisible()) {
                if (!this.fakeEntities.contains(entity)) {
                    this.fakeEntities.addLast(entity);
                }
                if (this.fakeEntities.size() >= 64) {
                    this.fakeEntities.removeFirst();
                }
                return false;
            }
            if (this.fakeEntities.contains(entity)) {
                return false;
            }
        }
        float distance = WMinecraft.getPlayer().getDistanceToEntity(entity);
        if (!WMinecraft.getPlayer().canEntityBeSeen(entity) && !(distance <= this.getSettings().getFloat("aura_reach") / 2.0f)) {
            return false;
        }
        if (distance > this.getSettings().getFloat("aura_reach")) {
            return false;
        }
        double x = entity.posX;
        double y = entity.posY;
        double z = entity.posZ;
        double diffX = x - WMinecraft.getPlayer().posX;
        double diffY = y - WMinecraft.getPlayer().posY;
        double diffZ = z - WMinecraft.getPlayer().posZ;
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(diffY, distance) * 180.0 / Math.PI));
        float diffYaw = Math.abs(WMath.wrapDegrees(yaw - WMinecraft.getPlayer().rotationYaw));
        float diffPitch = Math.abs(WMath.wrapDegrees(pitch - WMinecraft.getPlayer().rotationPitch));
        if (diffYaw > this.getSettings().getFloat("aura_rotlimit") || diffPitch > this.getSettings().getFloat("aura_rotlimit")) {
            return false;
        }
        if (entity instanceof EntitySilverfish && this.getSettings().getBoolean("aura_silverfish")) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            if (entity.equals(WMinecraft.getPlayer())) {
                return false;
            }
            if ((double)entity.height < 0.5) {
                return false;
            }
            if (entity.getName().length() <= 1) {
                return false;
            }
            if (this.getSettings().getBoolean("aura_players")) {
                EntityPlayer entityPlayer = (EntityPlayer)entity;
                return !Wolfram.getWolfram().relationManager.isFriend(entityPlayer);
            }
        }
        if (entity instanceof EntityMob) {
            EntityMob entityMob = (EntityMob)entity;
            if (entityMob instanceof EntitySpider) {
                if (entityMob.getBrightness() >= 0.5f && this.getSettings().getBoolean("aura_neutral_mobs")) {
                    return true;
                }
            } else if (entityMob instanceof EntityPigZombie) {
                EntityPigZombie pigZombie = (EntityPigZombie)entityMob;
                double angerLevel = pigZombie.angerLevel;
                if (angerLevel == 0.0 && this.getSettings().getBoolean("aura_neutral_mobs")) {
                    return true;
                }
            }
            if (this.getSettings().getBoolean("aura_hostile_mobs")) {
                return true;
            }
        }
        if (entity instanceof EntitySlime) {
            EntitySlime entitySlime = (EntitySlime)entity;
            if (entitySlime.getSlimeSize() <= 1 && this.getSettings().getBoolean("aura_neutral_mobs")) {
                return true;
            }
            if (entitySlime.getSlimeSize() >= 2 && this.getSettings().getBoolean("aura_hostile_mobs")) {
                return true;
            }
        }
        if (entity instanceof EntityTameable && (tameable = (EntityTameable)entity).isTamed() && this.getSettings().getBoolean("aura_tame_animals")) {
            return true;
        }
        if ((entity instanceof EntityAnimal || entity instanceof EntitySnowman) && this.getSettings().getBoolean("aura_passive_animals")) {
            if (entity instanceof EntityTameable && ((EntityTameable)entity).isTamed() && this.getSettings().getBoolean("aura_tame_animals")) {
                return true;
            }
            if (this.getSettings().getBoolean("aura_passive_animals")) {
                return true;
            }
        }
        return false;
    }

    private void attackEntity(Entity entity) {
        if (this.getSettings().getBoolean("aura_cooldown") ? WPlayer.getCooldown() >= 1.0f : this.time.check((float)this.nextDelay / this.getSettings().getFloat("aura_speed"))) {
            if (this.random.nextFloat() > this.getSettings().getFloat("aura_miss_ratio")) {
                WPlayerController.attackAndSwing(entity);
            } else {
                WPlayer.swingArmClient();
            }
            this.target = null;
            this.entities.remove(entity);
            this.time.reset();
            this.nextDelay = 900 + (this.getSettings().getBoolean("ixaura_randomizer") ? this.random.nextInt(200) : 0);
        }
    }
}

