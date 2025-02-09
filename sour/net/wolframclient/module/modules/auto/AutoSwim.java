/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.auto;

import net.minecraft.client.Minecraft;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class AutoSwim
extends ModuleListener {
    private boolean wasSwimming;

    public AutoSwim() {
        super("AutoSwim", Module.Category.AUTO, "Swim automatically");
    }

    @Override
    public void onDisable2() {
        this.setKeys(false);
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        boolean swimming;
        boolean bl = swimming = WMinecraft.getPlayer().isInWater() && !Minecraft.getMinecraft().gameSettings.keyBindSneak.pressed;
        if (swimming) {
            this.setKeys(true);
        } else if (this.wasSwimming) {
            this.setKeys(false);
        }
        this.wasSwimming = swimming;
    }

    private void setKeys(boolean pressed) {
        Minecraft.getMinecraft().gameSettings.keyBindForward.pressed = pressed;
        Minecraft.getMinecraft().gameSettings.keyBindJump.pressed = pressed;
    }
}

