/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package net.wolframclient.module;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.wolframclient.Wolfram;
import net.wolframclient.storage.MapStorage;

public class Module {
    private final String name;
    private final String description;
    private final Category category;
    private String displayName;
    private boolean enabled;

    public Module(String moduleName, Category category, String description) {
        this.name = moduleName;
        this.displayName = moduleName;
        this.category = category;
        this.description = description;
    }

    public final String getName() {
        return this.name;
    }

    public final String getDescription() {
        return this.description;
    }

    public final String getDisplayName() {
        return this.displayName;
    }

    protected final void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public final boolean isEnabled() {
        return this.enabled;
    }

    public final Category getCategory() {
        return this.category;
    }

    public final void setEnabled(boolean enabled, boolean useTracking) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        if (useTracking) {
            Wolfram.getWolfram().analytics.trackEvent("Module", this.name, enabled ? "Enable" : "Disable");
        }
        if (Wolfram.getWolfram().getClientSettings().getBoolean("toggle_msg")) {
            this.showToggleMessage(enabled);
        }
        this.onToggle();
        if (enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
        Wolfram.getWolfram().storageManager.moduleStates.set(this.name, enabled);
    }

    protected final void setEnabledDirectly(boolean enabled) {
        this.enabled = enabled;
    }

    protected void showToggleMessage(boolean enabled) {
        String prefix = enabled ? ChatFormatting.GREEN + "Enabling " : ChatFormatting.RED + "Disabling ";
        prefix = String.valueOf(prefix) + ChatFormatting.RESET;
        Wolfram.getWolfram().addChatMessage(String.valueOf(prefix) + this.name);
    }

    public final void toggleModule() {
        this.setEnabled(!this.isEnabled(), true);
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    @Deprecated
    protected void onToggle() {
    }

    public final MapStorage getSettings() {
        return Wolfram.getWolfram().storageManager.moduleSettings;
    }

    public void onShutdown() {
    }

    public static enum Category {
        PLAYER("Player"),
        MOVEMENT("Movement"),
        COMBAT("Combat"),
        WORLD("World"),
        RENDER("Render"),
        AUTO("Auto"),
        FUN("Fun"),
        OTHER("Other Mods");

        public String categoryName;

        private Category(String name) {
            this.categoryName = name;
        }
    }
}

