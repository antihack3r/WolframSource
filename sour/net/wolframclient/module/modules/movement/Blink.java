/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.module.modules.movement;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.util.math.AxisAlignedBB;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WConnection;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.NetworkManagerPacketSendEvent;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;
import net.wolframclient.module.modules.render.Breadcrumbs;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

public class Blink
extends ModuleListener {
    Breadcrumbs crumbs = new Breadcrumbs();
    int count = 0;
    private final List<Packet> packetList = new ArrayList<Packet>();
    private EntityOtherPlayerMP fakePlayer;

    public Blink() {
        super("Blink", Module.Category.MOVEMENT, "Teleport");
        this.crumbs.customColor = true;
        this.crumbs.color = 0xFFFFFF;
    }

    @Override
    public void onEnable2() {
        try {
            this.fakePlayer = new EntityOtherPlayerMP(WMinecraft.getWorld(), WMinecraft.getPlayer().getGameProfile());
            this.fakePlayer.copyLocationAndAnglesFrom(WMinecraft.getPlayer());
        }
        catch (Exception e) {
            this.setEnabledDirectly(false);
            return;
        }
        this.crumbs.onEnable();
        this.count = 0;
    }

    @EventTarget
    public void onRender(WorldRenderEvent event) {
        if (this.fakePlayer != null) {
            double x = this.fakePlayer.posX - RenderManager.renderPosX;
            double y = this.fakePlayer.posY - RenderManager.renderPosY;
            double z = this.fakePlayer.posZ - RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GL11.glTranslated((double)x, (double)y, (double)z);
            RenderUtils.drawOutlinedBox(new AxisAlignedBB(0.5, 0.0, -0.5, -0.5, (double)this.fakePlayer.height + 0.1, 0.5), 0xFFFFFF);
            GL11.glPopMatrix();
        }
        this.setDisplayName("Blink [" + this.count + "]");
    }

    @Override
    public void onDisable2() {
        try {
            this.fakePlayer = null;
            for (Packet unsentPacket : this.packetList) {
                WConnection.sendPacket(unsentPacket);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.packetList.clear();
        this.setDisplayName("Blink");
        this.count = 0;
        this.crumbs.onDisable();
    }

    @Override
    public void onShutdown() {
        this.setEnabledDirectly(false);
        Wolfram.getWolfram().storageManager.moduleStates.set(this.getName(), false);
    }

    @EventTarget
    public void onPacketSend(NetworkManagerPacketSendEvent event) {
        if (WMinecraft.getPlayer().isDead) {
            this.setEnabled(false, true);
            return;
        }
        Packet packet = event.getPacket();
        if (WMinecraft.getPlayer() != null && this.shouldKeepPacket(packet)) {
            this.packetList.add(packet);
            event.setCancelled(true);
            ++this.count;
        }
    }

    private boolean shouldKeepPacket(Packet packet) {
        return !(packet instanceof CPacketKeepAlive);
    }
}

