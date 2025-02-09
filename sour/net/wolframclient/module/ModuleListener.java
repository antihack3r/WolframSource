/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module;

import net.wolframclient.event.Listener;
import net.wolframclient.module.Module;

public abstract class ModuleListener
extends Module
implements Listener {
    public ModuleListener(String name, Module.Category category, String description) {
        super(name, category, description);
    }

    @Override
    protected final void onEnable() {
        registry.registerListener(this);
        this.onEnable2();
    }

    @Override
    protected final void onDisable() {
        registry.unregisterListener(this);
        this.onDisable2();
    }

    protected void onEnable2() {
    }

    protected void onDisable2() {
    }
}

