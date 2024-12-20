/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.utils;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.compatibility.WPlayer;

public class EntityFakePlayer
extends EntityOtherPlayerMP {
    public EntityFakePlayer() {
        super(WMinecraft.getWorld(), WMinecraft.getPlayer().getGameProfile());
        this.copyLocationAndAnglesFrom(WMinecraft.getPlayer());
        this.inventory.copyInventory(WMinecraft.getPlayer().inventory);
        WPlayer.copyPlayerModel(WMinecraft.getPlayer(), this);
        this.rotationYawHead = WMinecraft.getPlayer().rotationYawHead;
        this.renderYawOffset = WMinecraft.getPlayer().renderYawOffset;
        this.chasingPosX = this.posX;
        this.chasingPosY = this.posY;
        this.chasingPosZ = this.posZ;
        WMinecraft.getWorld().addEntityToWorld(this.getEntityId(), this);
    }

    public void resetPlayerPosition() {
        WMinecraft.getPlayer().setPositionAndRotation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
    }

    public void despawn() {
        WMinecraft.getWorld().removeEntityFromWorld(this.getEntityId());
    }
}

