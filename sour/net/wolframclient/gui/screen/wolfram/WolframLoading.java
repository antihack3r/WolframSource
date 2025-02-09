/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.gui.screen.wolfram;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.utils.RenderUtils;

public class WolframLoading
extends GuiScreen {
    GuiScreen parentScreen;
    public String infoMsg;
    public String statusMsg;
    public boolean endRequested;
    public boolean cancellable;

    public WolframLoading(GuiScreen parent, boolean cancellable) {
        this.parentScreen = parent;
        this.cancellable = cancellable;
    }

    @Override
    public void initGui() {
        super.initGui();
        if (this.cancellable) {
            this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 2 + 30, "Cancel"));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (this.cancellable && button.id == 200) {
            this.endRequested = true;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        FontRenderers.DEFAULT.drawCenteredStringXY(this.infoMsg, RenderUtils.getDisplayWidth() / 2, RenderUtils.getDisplayHeight() / 2 - 15, -263693982, false);
        FontRenderers.DEFAULT.drawCenteredStringXY(this.statusMsg, RenderUtils.getDisplayWidth() / 2, RenderUtils.getDisplayHeight() / 2, -263693982, false);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }
}

