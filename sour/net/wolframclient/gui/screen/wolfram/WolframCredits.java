/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.gui.screen.wolfram;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.wolframclient.Wolfram;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.utils.RenderUtils;

public class WolframCredits
extends GuiScreen {
    GuiScreen parentScreen;

    public WolframCredits(GuiScreen parent) {
        this.parentScreen = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 2 + 30, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 200) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        FontRenderers.TITLE.drawCenteredStringXY("Credits", RenderUtils.getDisplayWidth() / 2, 40, -263693982, false);
        int y = 60;
        String[] stringArray = Wolfram.CREDITS;
        int n = Wolfram.CREDITS.length;
        int n2 = 0;
        while (n2 < n) {
            String s = stringArray[n2];
            FontRenderers.DEFAULT.drawCenteredStringXY(s, RenderUtils.getDisplayWidth() / 2, y, -263693982, false);
            y += 14;
            ++n2;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }
}

