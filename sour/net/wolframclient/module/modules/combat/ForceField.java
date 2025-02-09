/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.combat;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.compatibility.WPlayer;
import net.wolframclient.compatibility.WPlayerController;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;
import net.wolframclient.storage.MapStorage;

public class ForceField
extends ModuleListener {
    public ForceField() {
        super("ForceField", Module.Category.COMBAT, "Attack all around you");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        MapStorage settings = this.getSettings();
        if (settings.getBoolean("aura_cooldown") && WPlayer.getCooldown() < 1.0f) {
            return;
        }
        EntityPlayerSP player = WMinecraft.getPlayer();
        Wolfram wolfram = Wolfram.getWolfram();
        for (Entity object : WMinecraft.getWorld().loadedEntityList) {
            EntityLivingBase entity;
            if (!(object instanceof EntityLivingBase) || (entity = (EntityLivingBase)object) == player || !entity.isEntityAlive() || wolfram.relationManager.isFriend(entity) || entity instanceof EntityPlayer && !settings.getBoolean("killaura_players") || entity instanceof IMob && !settings.getBoolean("killaura_mobs") || entity instanceof IAnimals && !settings.getBoolean("killaura_animals") || player.getDistanceToEntity(entity) > settings.getFloat("reach") || !entity.canEntityBeSeen(player)) continue;
            WPlayerController.attackAndSwing(entity);
        }
    }
}

