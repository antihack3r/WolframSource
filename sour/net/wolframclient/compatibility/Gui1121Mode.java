/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.compatibility;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.wolframclient.Wolfram;
import net.wolframclient.update.Version;

public class Gui1121Mode
extends GuiScreen {
    private final GuiScreen prevScreen;

    public Gui1121Mode(GuiScreen prevScreen) {
        this.prevScreen = prevScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 - 150, this.height / 3 * 2, 100, 20, "MC 1.12"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 50, this.height / 3 * 2, 100, 20, "MC 1.12.1"));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 50, this.height / 3 * 2, 100, 20, "MC 1.12.2"));
        this.buttonList.add(new GuiButton(-1, this.width / 2 - 100, this.height / 3 * 2 + 40, "Cancel"));
        int version = Wolfram.getWolfram().getClientSettings().getInt("mc112x_compatibility");
        if (version >= 0 && version < this.buttonList.size() - 1) {
            ((GuiButton)this.buttonList.get((int)version)).enabled = false;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0: 
            case 1: 
            case 2: {
                Wolfram.getWolfram().getClientSettings().set("mc112x_compatibility", button.id);
                Wolfram.getWolfram().getClientSettings().save();
                this.mc.shutdown();
                break;
            }
            case -1: {
                this.mc.displayGuiScreen(this.prevScreen);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Minecraft 1.12.X Compatibility Mode", this.width / 2, 20, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, "\ufffdaCurrent version: " + new Version("1.12." + Wolfram.getWolfram().getClientSettings().getInt("mc112x_compatibility")), this.width / 2, 80, 0xA0A0A0);
        this.drawCenteredString(this.fontRendererObj, "Only one version can be selected at any time.", this.width / 2, 110, 0xA0A0A0);
        this.drawCenteredString(this.fontRendererObj, "Changing this option requires the game to be restarted.", this.width / 2, 120, 0xA0A0A0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

