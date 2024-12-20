/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.module.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;
import net.wolframclient.utils.EntityHelper;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

public class ArmorESP
extends ModuleListener {
    public ArmorESP() {
        super("ArmorESP", Module.Category.RENDER, "Shows your enemies' armor");
    }

    @EventTarget
    public void onRenderWorld(WorldRenderEvent event) {
        for (Object object : WMinecraft.getWorld().loadedEntityList) {
            if (!(object instanceof EntityPlayer) || object == WMinecraft.getPlayer()) continue;
            EntityPlayer player = (EntityPlayer)object;
            EntityEquipmentSlot[] entityEquipmentSlotArray = EntityEquipmentSlot.values();
            int n = entityEquipmentSlotArray.length;
            int n2 = 0;
            while (n2 < n) {
                EntityEquipmentSlot i = entityEquipmentSlotArray[n2];
                if (player.getItemStackFromSlot(i) != null) {
                    GL11.glPushMatrix();
                    RenderUtils.enableRender3D(true);
                    float distance = WMinecraft.getPlayer().getDistanceToEntity(player);
                    double[] coords = EntityHelper.interpolate(player);
                    double x = coords[0] - RenderManager.renderPosX;
                    double y = coords[1] - RenderManager.renderPosY + (double)player.height + 0.5;
                    double z = coords[2] - RenderManager.renderPosZ;
                    GL11.glTranslated((double)x, (double)y, (double)z);
                    GL11.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
                    double scaleSize = 10.0f - this.getSettings().getFloat("nametags_size");
                    if ((double)distance > scaleSize) {
                        GL11.glScaled((double)((double)distance / scaleSize), (double)((double)distance / scaleSize), (double)((double)distance / scaleSize));
                    }
                    GL11.glScaled((double)-0.025, (double)-0.025, (double)0.025);
                    GL11.glRotatef((float)Minecraft.getMinecraft().renderManager.playerViewY, (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glRotatef((float)(-Minecraft.getMinecraft().renderManager.playerViewX), (float)1.0f, (float)0.0f, (float)0.0f);
                    GL11.glEnable((int)3553);
                    Minecraft.getMinecraft().getRenderItem().zLevel = -147.0f;
                    Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(player.getItemStackFromSlot(i), -50 + i.getSlotIndex() * 20, -20);
                    RenderUtils.drawRect(-50 + i.getSlotIndex() * 20, 0.0f, 15.0f, 1.0f, -803200992);
                    RenderUtils.drawRect(-50 + i.getSlotIndex() * 20, 0.0f, (1.0f - (float)player.getItemStackFromSlot(i).getItemDamage() / (float)player.getItemStackFromSlot(i).getMaxDamage()) * 15.0f, 1.0f, 65280);
                    GL11.glDisable((int)3553);
                    GlStateManager.shadeModel(7424);
                    GlStateManager.disableBlend();
                    GlStateManager.enableAlpha();
                    GlStateManager.enableTexture2D();
                    RenderUtils.disableRender3D(true);
                    RenderUtils.disableRender2D();
                    GL11.glPopMatrix();
                }
                ++n2;
            }
        }
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }
}

