/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.module.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.util.math.AxisAlignedBB;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.GuiRenderEvent;
import net.wolframclient.event.events.RenderEvent;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;
import net.wolframclient.utils.EntityHelper;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

public final class BowAimbot
extends ModuleListener {
    private EntityLivingBase target = null;
    private float velocity;

    public BowAimbot() {
        super("BowAimbot", Module.Category.COMBAT, "Aims the bow for you");
    }

    @EventTarget
    public void onGuiRender(GuiRenderEvent event) {
        if (!Wolfram.getWolfram().moduleManager.isEnabled("fastbow") && this.velocity != -1.0f) {
            if (this.velocity > 0.0f) {
                FontRenderers.DEFAULT.drawCenteredStringXY("Ready!", RenderUtils.getDisplayWidth() / 2, RenderUtils.getDisplayHeight() / 2 - 20, 0xFFFFFF, true);
            } else {
                FontRenderers.DEFAULT.drawCenteredStringXY("Charging...", RenderUtils.getDisplayWidth() / 2, RenderUtils.getDisplayHeight() / 2 - 20, 0xFF7000, true);
            }
        }
    }

    @EventTarget
    public void onWorldRender(WorldRenderEvent event) {
        if (this.target != null) {
            double[] pos = EntityHelper.interpolate(this.target);
            double x = pos[0] - RenderManager.renderPosX;
            double y = pos[1] - RenderManager.renderPosY;
            double z = pos[2] - RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GL11.glTranslated((double)x, (double)y, (double)z);
            GL11.glRotatef((float)(-this.target.rotationYaw), (float)0.0f, (float)1.0f, (float)0.0f);
            RenderUtils.drawOutlinedBox(new AxisAlignedBB((double)this.target.width / 2.0, 0.0, -((double)this.target.width / 2.0), (double)(-this.target.width) / 2.0, (double)this.target.height + 0.1, (double)this.target.width / 2.0), 0xFF0000);
            GL11.glPopMatrix();
        }
    }

    @EventTarget
    public void onRender(RenderEvent event) {
        this.target = null;
        if (WMinecraft.getPlayer().inventory.getCurrentItem() != null && WMinecraft.getPlayer().inventory.getCurrentItem().getItem() instanceof ItemBow && Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed) {
            this.target = EntityHelper.getClosestEntity(false);
            this.aimAtTarget();
        } else {
            this.velocity = -1.0f;
        }
    }

    private void aimAtTarget() {
        if (this.target == null) {
            return;
        }
        this.velocity = (float)(72000 - WMinecraft.getPlayer().getItemInUseCount()) / 20.0f;
        this.velocity = (this.velocity * this.velocity + this.velocity * 2.0f) / 3.0f;
        if (this.velocity > 1.0f) {
            this.velocity = 1.0f;
        }
        if (Wolfram.getWolfram().moduleManager.isEnabled("fastbow")) {
            this.velocity = 1.0f;
        }
        if ((double)this.velocity < 0.1) {
            if (this.target instanceof EntityLivingBase) {
                EntityHelper.faceEntity(this.target, 50.0f);
            }
            return;
        }
        if (this.velocity > 1.0f) {
            this.velocity = 1.0f;
        }
        double posX = this.target.posX - WMinecraft.getPlayer().posX;
        double posY = this.target.posY + (double)this.target.getEyeHeight() - 0.15 - WMinecraft.getPlayer().posY - (double)WMinecraft.getPlayer().getEyeHeight();
        double posZ = this.target.posZ - WMinecraft.getPlayer().posZ;
        float yaw = (float)(Math.atan2(posZ, posX) * 180.0 / Math.PI) - 90.0f;
        double y2 = Math.sqrt(posX * posX + posZ * posZ);
        float g = 0.006f;
        float tmp = (float)((double)(this.velocity * this.velocity * this.velocity * this.velocity) - (double)g * ((double)g * (y2 * y2) + 2.0 * posY * (double)(this.velocity * this.velocity)));
        float pitch = (float)(-Math.toDegrees(Math.atan(((double)(this.velocity * this.velocity) - Math.sqrt(tmp)) / ((double)g * y2))));
        WMinecraft.getPlayer().rotationYaw = yaw;
        WMinecraft.getPlayer().rotationPitch = pitch;
    }
}

