/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package net.wolframclient.module;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.wolframclient.Wolfram;
import net.wolframclient.module.Module;

public abstract class InstantModule
extends Module {
    public InstantModule(String name, Module.Category category, String description) {
        super(name, category, description);
    }

    public final void use() {
        this.setEnabled(true, true);
    }

    @Override
    protected final void showToggleMessage(boolean enabled) {
        if (!enabled) {
            return;
        }
        Wolfram.getWolfram().addChatMessage(ChatFormatting.AQUA + "Using " + ChatFormatting.RESET + this.getName());
    }

    @Override
    protected final void onToggle() {
    }

    @Override
    protected final void onEnable() {
        this.onUse();
        this.setEnabled(false, false);
    }

    @Override
    protected final void onDisable() {
    }

    protected abstract void onUse();
}

