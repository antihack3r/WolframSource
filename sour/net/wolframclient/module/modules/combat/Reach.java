/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.combat;

import net.wolframclient.module.Module;

public final class Reach
extends Module {
    public Reach() {
        super("Reach", Module.Category.COMBAT, "Reach further");
    }

    @Override
    protected void onEnable() {
        this.getSettings().set("reach", Float.valueOf(6.0f));
    }

    @Override
    protected void onDisable() {
        this.getSettings().set("reach", Float.valueOf(3.8f));
    }
}

