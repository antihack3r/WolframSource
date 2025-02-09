/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WMath;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.RenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;
import net.wolframclient.utils.EntityHelper;

public final class AimAssist
extends ModuleListener {
    public AimAssist() {
        super("AimAssist", Module.Category.COMBAT, "Helps your aiming");
    }

    @EventTarget
    public void onUpdate(RenderEvent event) {
        Entity bestEntity = null;
        double bestAngle = 45.0;
        for (Entity entity : WMinecraft.getWorld().loadedEntityList) {
            double angle;
            if (!this.isValid(entity) || !((angle = this.getAngle(entity)) < bestAngle)) continue;
            bestEntity = entity;
            bestAngle = angle;
        }
        if (bestEntity != null) {
            EntityHelper.faceEntity(bestEntity, 1.0f);
        }
    }

    private boolean isValid(Entity entity) {
        if (!(entity instanceof EntityLivingBase)) {
            return false;
        }
        if (((EntityLivingBase)entity).getHealth() <= 0.0f) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            if (entity == WMinecraft.getPlayer()) {
                return false;
            }
            if (Wolfram.getWolfram().relationManager.isFriend((EntityPlayer)entity)) {
                return false;
            }
            if (!this.getSettings().getBoolean("killaura_players")) {
                return false;
            }
        }
        if (entity instanceof IMob && !this.getSettings().getBoolean("killaura_mobs")) {
            return false;
        }
        if (entity instanceof IAnimals && !this.getSettings().getBoolean("killaura_animals")) {
            return false;
        }
        return !(WMinecraft.getPlayer().getDistanceToEntity(entity) > this.getSettings().getFloat("reach"));
    }

    private double getAngle(Entity entity) {
        double diffX = entity.posX - WMinecraft.getPlayer().posX;
        double diffY = entity.posY - WMinecraft.getPlayer().posY;
        double diffZ = entity.posZ - WMinecraft.getPlayer().posZ;
        float distance = WMinecraft.getPlayer().getDistanceToEntity(entity);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(diffY, distance) * 180.0 / Math.PI));
        float diffYaw = Math.abs(WMath.wrapDegrees(yaw - WMinecraft.getPlayer().rotationYaw));
        float diffPitch = Math.abs(WMath.wrapDegrees(pitch - WMinecraft.getPlayer().rotationPitch));
        return Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
    }
}

