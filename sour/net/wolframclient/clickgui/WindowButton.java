/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui;

import net.wolframclient.Wolfram;
import net.wolframclient.clickgui.Button;
import net.wolframclient.clickgui.Window;

public final class WindowButton
extends Button {
    @Deprecated
    public final int targetWindow;

    public WindowButton(Window window, int id, int offX, int offY, String title, String tooltip, int targetWindow) {
        super(window, id, offX, offY, title, tooltip);
        this.targetWindow = targetWindow;
        this.width = Math.max(80, window.getWidth());
        this.height = 15;
        this.type = "WindowButton";
    }

    @Override
    public void update(int mouseX, int mouseY) {
        super.update(mouseX, mouseY);
        this.toggled = Wolfram.getWolfram().guiManager.gui.getWindowByID(this.targetWindow).isEnabled();
    }

    @Override
    protected void onPress() {
        Window window2;
        window2.setEnabled(!(window2 = Wolfram.getWolfram().guiManager.gui.getWindowByID(this.targetWindow)).isEnabled());
        if (window2.isEnabled()) {
            Wolfram.getWolfram().guiManager.gui.bringToFront(window2);
        }
    }

    public int getWindowId() {
        return this.targetWindow;
    }
}

