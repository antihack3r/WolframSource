/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui;

import net.wolframclient.Wolfram;
import net.wolframclient.clickgui.Button;
import net.wolframclient.clickgui.Window;

public final class CommandButton
extends Button {
    public String command;

    public CommandButton(Window window, int id, int offX, int offY, String title, String tooltip, String command) {
        super(window, id, offX, offY, title, tooltip);
        this.command = command;
        this.width = Math.max(80, window.getWidth());
        this.height = 15;
        this.type = "CommandButton";
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
        Wolfram.getWolfram().commandManager.runCommand(this.command);
    }
}

