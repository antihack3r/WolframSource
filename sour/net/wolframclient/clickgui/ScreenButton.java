/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.wolframclient.Wolfram;
import net.wolframclient.clickgui.Button;
import net.wolframclient.clickgui.Window;

public final class ScreenButton
extends Button {
    private final GuiScreen screen;

    public ScreenButton(Window window, int id, int offX, int offY, String title, String tooltip, GuiScreen screen) {
        super(window, id, offX, offY, title, tooltip);
        this.screen = screen;
        this.width = Math.max(80, window.getWidth());
        this.height = 15;
        this.type = "ScreenButton";
    }

    @Override
    public void update(int mouseX, int mouseY) {
        super.update(mouseX, mouseY);
        if (this.window.mouseOver(mouseX, mouseY) && this.contains(mouseX, mouseY)) {
            Wolfram.getWolfram().guiManager.gui.setTooltip(this.tooltip);
        }
    }

    @Override
    protected void onPress() {
        Minecraft.getMinecraft().displayGuiScreen(this.screen);
    }
}

