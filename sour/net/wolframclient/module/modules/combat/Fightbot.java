/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.combat;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.compatibility.WPlayer;
import net.wolframclient.compatibility.WPlayerController;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.RenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.utils.EntityHelper;

public class Fightbot
extends Module
implements Listener {
    WalkNodeProcessor walkNodeProcessor = new WalkNodeProcessor();
    PathFinder pathFinder = new PathFinder(this.walkNodeProcessor);
    Path Path = null;
    float searchRange = 50.0f;
    EntityLivingBase lockedEntity = null;
    int limit = 10;
    int nextDelay = 10;
    Random random = new Random();
    int refreshRate = 5;
    int refreshLimiter = 0;

    public Fightbot() {
        super("Fightbot", Module.Category.COMBAT, "Automatically walk and fight");
    }

    @Override
    public void onEnable() {
        this.walkNodeProcessor.setCanSwim(true);
        registry.registerListener(this);
    }

    @Override
    public void onDisable() {
        Minecraft.getMinecraft().gameSettings.keyBindJump.pressed = false;
        Minecraft.getMinecraft().gameSettings.keyBindForward.pressed = false;
        registry.unregisterListener(this);
    }

    @EventTarget
    public void onUpdate(RenderEvent event) {
        EntityLivingBase entity = EntityHelper.getClosestEntity(false);
        if (this.lockedEntity != null && !EntityHelper.isBiggerThreat(entity, this.lockedEntity) && this.lockedEntity.isEntityAlive()) {
            entity = this.lockedEntity;
        }
        this.lockedEntity = entity;
        if (entity.canEntityBeSeen(WMinecraft.getPlayer())) {
            EntityHelper.faceEntity(entity, 20.0f);
            if (WMinecraft.getPlayer().getDistanceToEntity(entity) < this.getSettings().getFloat("reach")) {
                if (this.getSettings().getBoolean("aura_cooldown") ? WPlayer.getCooldown() >= 1.0f : this.limit > this.nextDelay) {
                    WPlayerController.attackAndSwing(entity);
                    this.limit = 0;
                    this.nextDelay = 10 + this.random.nextInt(3);
                } else if (this.random.nextInt(10) == 0) {
                    WPlayer.swingArmClient();
                }
            }
        } else {
            if (this.refreshLimiter > this.refreshRate) {
                this.Path = this.pathFinder.findPath((IBlockAccess)WMinecraft.getWorld(), (Entity)WMinecraft.getPlayer(), entity, this.searchRange);
                this.Path.incrementPathIndex();
                if (!WMinecraft.getPlayer().isCollidedHorizontally) {
                    this.Path.incrementPathIndex();
                }
                this.refreshLimiter = 0;
            }
            Vec3d pathPoint = this.Path.getPosition(WMinecraft.getPlayer());
            EntityHelper.facePos(pathPoint.xCoord, pathPoint.yCoord, pathPoint.zCoord, true);
        }
        if (WMinecraft.getPlayer().isCollidedHorizontally && WMinecraft.getPlayer().onGround) {
            WMinecraft.getPlayer().jump();
        }
        Minecraft.getMinecraft().gameSettings.keyBindJump.pressed = WMinecraft.getPlayer().isInWater();
        ++this.limit;
        ++this.refreshLimiter;
        Minecraft.getMinecraft().gameSettings.keyBindForward.pressed = true;
    }
}

