/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.render;

import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.module.Module;
import net.wolframclient.utils.RenderUtils;

public class PotionEffects
extends Module {
    public PotionEffects() {
        super("PotionEffects", Module.Category.RENDER, "Shows active potion effects");
    }

    public static PotionEffects getInstance() {
        return (PotionEffects)Wolfram.getWolfram().moduleManager.getModule("PotionEffects");
    }

    public int getHeight() {
        if (!this.isEnabled() || WMinecraft.getPlayer().getActivePotionEffects().size() == 0) {
            return 0;
        }
        int y = 2 + WMinecraft.getPlayer().getActivePotionEffects().size() * 10;
        return y;
    }

    public void render(int y) {
        Iterator<PotionEffect> iterator = WMinecraft.getPlayer().getActivePotionEffects().iterator();
        while (iterator.hasNext()) {
            PotionEffect object;
            PotionEffect potionEffect = object = iterator.next();
            String renderString = String.valueOf(I18n.format(potionEffect.getEffectName(), new Object[0])) + " - " + Potion.getPotionDurationString(potionEffect, 0.0f);
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(renderString, RenderUtils.getDisplayWidth() - Minecraft.getMinecraft().fontRendererObj.getStringWidth(renderString) - 2, y, GuiManager.getHexMainColor());
            y += 10;
        }
    }
}

