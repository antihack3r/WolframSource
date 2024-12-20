/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package net.wolframclient.module.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.NametagRenderEvent;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.module.Module;
import net.wolframclient.utils.RenderUtils;
import net.wolframclient.utils.TextUtils;

public class Nametags
extends Module
implements Listener {
    public Nametags() {
        super("Nametags", Module.Category.RENDER, "Makes nametags bigger");
        registry.registerListener(this);
    }

    @EventTarget(priority=4)
    public void onRenderWorld(WorldRenderEvent event) {
        if (this.isEnabled()) {
            try {
                for (Object object : WMinecraft.getWorld().loadedEntityList) {
                    EntityLivingBase entity;
                    if (object instanceof EntityLivingBase) {
                        String name;
                        entity = (EntityLivingBase)object;
                        String healthColor = "";
                        healthColor = entity.getHealth() < 6.0f ? ChatFormatting.RED.toString() : (entity.getHealth() < 12.0f ? ChatFormatting.YELLOW.toString() : ChatFormatting.GREEN.toString());
                        if (entity instanceof EntityPlayer && entity != WMinecraft.getPlayer()) {
                            name = entity.getDisplayName().getUnformattedText();
                            this.renderNameTag(String.valueOf(name) + (this.getSettings().getBoolean("nametags_health") ? " " + ChatFormatting.RESET + "(" + healthColor + (float)((int)entity.getHealth()) / 2.0f + ChatFormatting.RESET + ")" : ""), entity);
                            continue;
                        }
                        if (!(entity instanceof IMob) && !(entity instanceof IAnimals) || entity.getCustomNameTag().replaceAll(String.valueOf(TextUtils.chatFormatChar) + ".", "").equals("")) continue;
                        name = entity.getCustomNameTag();
                        this.renderNameTag(String.valueOf(name) + (this.getSettings().getBoolean("nametags_health") ? " " + ChatFormatting.RESET + "(" + healthColor + (float)((int)entity.getHealth()) / 2.0f + ChatFormatting.RESET + ")" : ""), entity);
                        continue;
                    }
                    if (!(object instanceof EntityArmorStand) || (entity = (EntityArmorStand)object).getCustomNameTag().replaceAll(String.valueOf(TextUtils.chatFormatChar) + ".", "").equals("")) continue;
                    String name = entity.getCustomNameTag();
                    this.renderNameTag(name, entity);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @EventTarget
    public void onNamePlateRender(NametagRenderEvent event) {
        if (this.isEnabled()) {
            event.setCancelled(true);
        } else if (this.getSettings().getBoolean("nametags_health") && event.getEntity() instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase)event.getEntity();
            String healthColor = "";
            healthColor = entity.getHealth() < 6.0f ? ChatFormatting.RED.toString() : (entity.getHealth() < 12.0f ? ChatFormatting.YELLOW.toString() : ChatFormatting.GREEN.toString());
            event.setName(String.valueOf(event.getName()) + " " + ChatFormatting.RESET + "(" + healthColor + (float)((int)entity.getHealth()) / 2.0f + ChatFormatting.RESET + ")");
        }
    }

    public void renderNameTag(String name, Entity entity) {
        RenderUtils.renderNameTag(name, entity, 2, this.getSettings().getDouble("nametags_size"), this.getNameColor(entity));
    }

    private int getNameColor(Entity entity) {
        int color = entity instanceof EntityPlayer ? (Wolfram.getWolfram().relationManager.isFriend((EntityPlayer)entity) ? 65280 : (Wolfram.getWolfram().relationManager.isEnemy((EntityPlayer)entity) ? 0xFF0000 : (entity.isSneaking() ? GuiManager.getHexMainColor() : 0xFFFFFF))) : 1421567;
        return color;
    }
}

