/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui;

import net.wolframclient.clickgui.Component;
import net.wolframclient.clickgui.Window;
import net.wolframclient.clickgui.WolframGui;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.utils.RenderUtils;

public abstract class Button
extends Component {
    protected boolean toggled;

    public Button(Window window, int id, int offX, int offY, String title, String tooltip) {
        super(window, id, offX, offY, title, tooltip);
        this.width = 80;
        this.height = 15;
        this.type = "Button";
    }

    @Override
    public void render(int mouseX, int mouseY) {
        int renderWidth = this.width - (this.window.isScrollbarEnabled() ? 2 : 0);
        RenderUtils.drawRect(this.x, this.y, renderWidth, this.height, this.hovering ? 0 : WolframGui.getBackgroundColor());
        FontRenderers.SMALL.drawCenteredStringXY(this.title, this.x + renderWidth / 2, this.y + this.height / 2, this.toggled ? GuiManager.getHexMainColor() : 0xFFFFFF, false);
    }

    @Override
    public void update(int mouseX, int mouseY) {
        super.update(mouseX, mouseY);
    }

    @Override
    public void mouseUpdates(int mouseX, int mouseY, boolean isPressed) {
        boolean bl = this.hovering = this.contains(mouseX, mouseY) && this.window.mouseOver(mouseX, mouseY);
        if (isPressed && !this.wasMousePressed && this.hovering) {
            this.onPress();
        }
        this.wasMousePressed = isPressed;
    }

    protected abstract void onPress();
}

