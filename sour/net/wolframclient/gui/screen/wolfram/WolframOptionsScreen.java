/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.gui.screen.wolfram;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.gui.screen.keybinds.WolframGuiKeybind;
import net.wolframclient.gui.screen.wolfram.WolframPreferences;
import net.wolframclient.gui.screen.xray.WolframXrayScreen;

public class WolframOptionsScreen
extends GuiScreen {
    private final GuiScreen parentScreen;

    public WolframOptionsScreen(GuiScreen parent) {
        this.parentScreen = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 75, 40, 150, 20, "Preferences"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 75, 65, 150, 20, "Xray Manager"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 75, 90, 150, 20, "Keybind Manager"));
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done", new Object[0])));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(FontRenderers.TITLE, "Wolfram Options", this.width / 2, 15, -263693982);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1: {
                this.mc.displayGuiScreen(new WolframPreferences(this));
                break;
            }
            case 2: {
                this.mc.displayGuiScreen(new WolframXrayScreen(this));
                break;
            }
            case 3: {
                this.mc.displayGuiScreen(new WolframGuiKeybind(this));
                break;
            }
            case 200: {
                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }
}

