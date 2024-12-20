/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui;

import net.wolframclient.Wolfram;
import net.wolframclient.clickgui.Component;
import net.wolframclient.clickgui.Window;
import net.wolframclient.clickgui.WolframGui;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.storage.MapStorage;
import net.wolframclient.utils.AnimationTimer;
import net.wolframclient.utils.RenderUtils;

public final class Checkbox
extends Component {
    private boolean checked;
    public MapStorage storage;
    public String setting;
    private final AnimationTimer anim = new AnimationTimer(20);

    public Checkbox(MapStorage storage, Window window, int id, int offX, int offY, String title, String setting, String tooltip) {
        super(window, id, offX, offY, title, tooltip);
        this.storage = storage;
        this.setting = setting;
        this.width = 100;
        this.height = 18;
        this.type = "Checkbox";
    }

    @Override
    public void update(int mouseX, int mouseY) {
        super.update(mouseX, mouseY);
        this.checked = this.storage == null ? false : this.storage.getBoolean(this.setting);
        this.anim.update(this.checked);
        if (this.window.mouseOver(mouseX, mouseY) && this.contains(mouseX, mouseY)) {
            Wolfram.getWolfram().guiManager.gui.setTooltip(this.tooltip);
        }
    }

    @Override
    public void render(int mouseX, int mouseY) {
        int renderWidth = this.width - (this.window.isScrollbarEnabled() ? 2 : 0);
        RenderUtils.drawRect(this.x, this.y, renderWidth, this.height, this.hovering ? 0 : WolframGui.getBackgroundColor());
        FontRenderers.SMALL.drawString(this.title, (float)(this.x + 18), (float)this.y + 5.5f, 0xFFFFFF);
        RenderUtils.drawRect(this.x + 5, this.y + 5, 8.0f, 8.0f, GuiManager.getHexMainColor());
        if (this.anim.getValue() > 0.0) {
            RenderUtils.drawLine2D(this.x + 7, this.y + 8, this.x + 7, (double)(this.y + 8) + (double)(this.height - 15) * Math.min(0.5, this.anim.getValue()) * 2.0, 1.5f, 0xFFFFFF);
        }
        if (this.anim.getValue() > 0.5) {
            RenderUtils.drawLine2D(this.x + 7, this.y + this.height - 7, (double)(this.x + 7) + (double)(this.height - 10) * (this.anim.getValue() - 0.5) * 2.0, (double)(this.y + this.height - 7) - (double)(this.height - 13) * (this.anim.getValue() - 0.5) * 2.0, 1.5f, 0xFFFFFF);
        }
    }

    @Override
    public void mouseUpdates(int mouseX, int mouseY, boolean isPressed) {
        boolean bl = this.hovering = this.contains(mouseX, mouseY) && this.window.mouseOver(mouseX, mouseY);
        if (isPressed && !this.wasMousePressed && this.hovering && this.storage != null) {
            this.checked = !this.checked;
            this.storage.set(this.setting, this.checked);
        }
        this.wasMousePressed = isPressed;
    }
}

