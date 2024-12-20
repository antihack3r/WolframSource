/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui;

import net.wolframclient.clickgui.Component;
import net.wolframclient.clickgui.Window;

public final class Placeholder
extends Component {
    public Placeholder(Window window, int id, int offX, int offY, Component target) {
        super(window, id, offX, offY, target.title, target.tooltip);
        this.width = Math.max(80, window.getWidth());
        this.height = 0;
        this.type = "Placeholder";
    }

    @Override
    public void render(int mouseX, int mouseY) {
    }

    @Override
    public void mouseUpdates(int mouseX, int mouseY, boolean isPressed) {
    }
}

