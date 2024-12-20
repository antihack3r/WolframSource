/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.module.InstantModule;
import net.wolframclient.module.Module;

public class Flip
extends InstantModule {
    public Flip() {
        super("Flip", Module.Category.MOVEMENT, "Instantly turn by 180 degrees");
    }

    @Override
    public void onUse() {
        if (WMinecraft.getPlayer() == null) {
            return;
        }
        WMinecraft.getPlayer().rotationYawHead += 180.0f;
        WMinecraft.getPlayer().rotationYaw += 180.0f;
    }
}

