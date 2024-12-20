/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.auto;

import net.minecraft.client.Minecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class AutoMine
extends ModuleListener {
    public AutoMine() {
        super("AutoMine", Module.Category.AUTO, "Break blocks automatically");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        Minecraft.getMinecraft().gameSettings.keyBindAttack.pressed = true;
    }

    @Override
    public void onDisable2() {
        Minecraft.getMinecraft().gameSettings.keyBindAttack.pressed = false;
    }
}

