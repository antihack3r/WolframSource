/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui;

import net.wolframclient.Wolfram;
import net.wolframclient.clickgui.Component;
import net.wolframclient.clickgui.Window;
import net.wolframclient.clickgui.WolframGui;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.module.Module;
import net.wolframclient.utils.RenderUtils;

public final class ModuleList
extends Component {
    private int lastHeight = 0;

    public ModuleList(Window window, int id, int offX, int offY, String title, String tooltip) {
        super(window, id, offX, offY, title, tooltip);
        this.width = Math.max(80, window.getWidth());
        this.type = "ModuleList";
        this.editable = false;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        int currentY = this.y;
        RenderUtils.drawRect(this.x, currentY, this.width, 2.0f, WolframGui.getBackgroundColor());
        currentY += 2;
        boolean shortArrayList = Wolfram.getWolfram().getClientSettings().getBoolean("short_arraylist");
        int renderMods = 0;
        for (Module m : Wolfram.getWolfram().moduleManager.getList()) {
            if (!m.isEnabled()) continue;
            if (shortArrayList && m.getCategory() == Module.Category.RENDER) {
                ++renderMods;
                continue;
            }
            RenderUtils.drawRect(this.x, currentY, this.width, 14.0f, WolframGui.getBackgroundColor());
            FontRenderers.SMALL.drawCenteredStringXY(m.getDisplayName(), this.x + this.width / 2, currentY + 7, 0xFFFFFF, false);
            currentY += 14;
        }
        if (renderMods > 0) {
            RenderUtils.drawRect(this.x, currentY, this.width, 14.0f, WolframGui.getBackgroundColor());
            FontRenderers.SMALL.drawCenteredStringXY(String.valueOf(renderMods) + " render mods", this.x + this.width / 2, currentY + 7, 0xFFFFFF, false);
            currentY += 14;
        }
        RenderUtils.drawRect(this.x, currentY, this.width, 2.0f, WolframGui.getBackgroundColor());
        this.height = (currentY += 2) - this.y;
        if (this.height != this.lastHeight) {
            this.window.repositionComponents();
            this.lastHeight = this.height;
        }
    }

    @Override
    public void mouseUpdates(int mouseX, int mouseY, boolean isPressed) {
    }
}

