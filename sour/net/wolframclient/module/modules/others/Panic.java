/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.others;

import net.wolframclient.Wolfram;
import net.wolframclient.module.InstantModule;
import net.wolframclient.module.Module;

public final class Panic
extends InstantModule {
    public Panic() {
        super("Panic", Module.Category.OTHER, "Turn off all mods");
    }

    @Override
    public void onUse() {
        Wolfram.getWolfram().moduleManager.disableModules();
    }
}

