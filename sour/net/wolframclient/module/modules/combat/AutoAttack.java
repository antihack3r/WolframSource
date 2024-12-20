/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.combat;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.RayTraceResult;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.compatibility.WPlayer;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class AutoAttack
extends ModuleListener {
    private final Random random = new Random();
    private int delay = 5;
    private int counter = 0;

    public AutoAttack() {
        super("AutoAttack", Module.Category.COMBAT, "Automatically hits whatever you aim at (a.k.a. TriggerBot)");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        ++this.counter;
        if (Minecraft.getMinecraft().objectMouseOver == null || Minecraft.getMinecraft().objectMouseOver.typeOfHit != RayTraceResult.Type.ENTITY) {
            return;
        }
        if (WMinecraft.COOLDOWN && this.getSettings().getBoolean("aura_cooldown") ? WPlayer.getCooldown() >= 1.0f : this.counter > this.delay) {
            Minecraft.getMinecraft().clickMouse();
            this.counter = 0;
            this.delay = 3 + this.random.nextInt(5);
        } else if (this.random.nextFloat() < this.getSettings().getFloat("aura_miss_ratio")) {
            WPlayer.swingArmClient();
        }
    }
}

