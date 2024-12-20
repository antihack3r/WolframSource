/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.gui.screen.wolfram;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.utils.RenderUtils;

public class WolframErrorScreen
extends GuiScreen {
    GuiScreen parentScreen;
    public String infoMsg;
    public boolean endRequested;
    public boolean cancellable;

    public WolframErrorScreen(GuiScreen parent, String message) {
        this.parentScreen = parent;
        this.infoMsg = message;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 2 + 30, "Back"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 200) {
            Minecraft.getMinecraft().displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        FontRenderers.DEFAULT.drawCenteredStringXY(this.infoMsg, RenderUtils.getDisplayWidth() / 2, RenderUtils.getDisplayHeight() / 2 - 15, -263693982, false);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

