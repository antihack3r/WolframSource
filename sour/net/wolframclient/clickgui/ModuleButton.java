/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui;

import net.wolframclient.Wolfram;
import net.wolframclient.clickgui.Button;
import net.wolframclient.clickgui.Window;
import net.wolframclient.module.Module;

public final class ModuleButton
extends Button {
    private Module module;

    public ModuleButton(Window window, int id, int offX, int offY, String title, String tooltip, Module module) {
        super(window, id, offX, offY, title, tooltip);
        this.module = module;
        this.width = Math.max(80, window.getWidth());
        this.height = 15;
        this.type = "ModuleButton";
    }

    @Override
    public void update(int mouseX, int mouseY) {
        super.update(mouseX, mouseY);
        this.toggled = this.module != null ? this.module.isEnabled() : false;
        if (this.window.mouseOver(mouseX, mouseY) && this.contains(mouseX, mouseY)) {
            Wolfram.getWolfram().guiManager.gui.setTooltip(this.tooltip);
        }
    }

    @Override
    protected void onPress() {
        if (this.module != null) {
            this.module.toggleModule();
        }
    }

    public Module getModule() {
        return this.module;
    }

    public void setModule(Module module) {
        this.module = module;
    }
}

