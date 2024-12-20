/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.module.modules.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WConnection;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.utils.EntityFakePlayer;
import net.wolframclient.utils.RenderUtils;
import net.wolframclient.utils.RotationUtils;
import org.lwjgl.opengl.GL11;

public final class Freecam
extends Module
implements Listener {
    private EntityFakePlayer fakePlayer;

    public Freecam() {
        super("Freecam", Module.Category.PLAYER, "Go out of your player");
    }

    @Override
    public void onEnable() {
        try {
            this.fakePlayer = new EntityFakePlayer();
            registry.registerListener(this);
        }
        catch (Exception e) {
            e.printStackTrace();
            this.setEnabledDirectly(false);
        }
    }

    @Override
    public void onDisable() {
        registry.unregisterListener(this);
        this.fakePlayer.resetPlayerPosition();
        this.fakePlayer.despawn();
    }

    @EventTarget
    public void onPlayerUpdate(PlayerUpdateEvent event) {
        float speed;
        EntityPlayerSP player = WMinecraft.getPlayer();
        player.capabilities.isFlying = false;
        player.onGround = false;
        player.motionX = 0.0;
        player.motionY = 0.0;
        player.motionZ = 0.0;
        player.jumpMovementFactor = speed = this.getSettings().getFloat("flight_speed");
        GameSettings gs = Minecraft.getMinecraft().gameSettings;
        if (gs.keyBindJump.isKeyDown()) {
            player.motionY += (double)speed;
        }
        if (gs.keyBindSneak.isKeyDown()) {
            player.motionY -= (double)speed;
        }
        WConnection.sendPacket(new CPacketPlayer());
        event.setCancelled(true);
    }

    @EventTarget
    public void onRender(WorldRenderEvent event) {
        if (this.fakePlayer == null) {
            return;
        }
        double x = this.fakePlayer.posX - RenderManager.renderPosX;
        double y = this.fakePlayer.posY - RenderManager.renderPosY;
        double z = this.fakePlayer.posZ - RenderManager.renderPosZ;
        EntityPlayerSP player = WMinecraft.getPlayer();
        Vec3d start = RotationUtils.getClientLookVec().addVector(0.0, player.getEyeHeight(), 0.0);
        RenderUtils.drawLine3D(start.xCoord, start.yCoord, start.zCoord, x, y + (double)this.fakePlayer.height * 0.5, z, 0xFFFFFF);
        GL11.glPushMatrix();
        GL11.glTranslated((double)x, (double)y, (double)z);
        AxisAlignedBB bb = new AxisAlignedBB(-0.45, 0.0, -0.45, 0.45, (double)this.fakePlayer.height + 0.2, 0.45);
        RenderUtils.drawOutlinedBox(bb, 0xFFFFFF);
        GL11.glPopMatrix();
    }

    @Override
    public void onShutdown() {
        this.setEnabledDirectly(false);
        Wolfram.getWolfram().storageManager.moduleStates.set(this.getName(), false);
    }
}

