/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.gui.screen.wolfram;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.wolframclient.account_manager.AccountManagerScreen;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.gui.screen.wolfram.WolframCredits;
import net.wolframclient.gui.screen.wolfram.WolframOptionsScreen;

public final class WolframMenuScreen
extends GuiScreen {
    private final GuiScreen prevScreen;

    public WolframMenuScreen(GuiScreen prevScreen) {
        this.prevScreen = prevScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(-100, this.width / 2 - 100, this.height / 6, "Client Settings"));
        GuiButton accountManagerButton = new GuiButton(-99, this.width / 2 - 100, this.height / 6 + 25, "Account Manager");
        this.buttonList.add(accountManagerButton);
        accountManagerButton.enabled = WMinecraft.getPlayer() == null;
        this.buttonList.add(new GuiButton(-98, this.width / 2 - 100, this.height / 6 + 50, "Official Website"));
        this.buttonList.add(new GuiButton(-97, this.width / 2 - 100, this.height / 6 + 75, "Twitter Page"));
        this.buttonList.add(new GuiButton(-95, this.width / 2 - 100, this.height / 6 + 125, "Credits"));
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, "Done"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(FontRenderers.TITLE, "Wolfram Menu", this.width / 2, 15, -263693982);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 200: {
                this.mc.displayGuiScreen(this.prevScreen);
                break;
            }
            case -100: {
                this.mc.displayGuiScreen(new WolframOptionsScreen(this));
                break;
            }
            case -99: {
                this.mc.displayGuiScreen(new AccountManagerScreen(this));
                break;
            }
            case -98: {
                Desktop.getDesktop().browse(URI.create("https://www.wolframclient.net/"));
                break;
            }
            case -97: {
                Desktop.getDesktop().browse(URI.create("https://twitter.com/Wurst_Imperium"));
                break;
            }
            case -95: {
                this.mc.displayGuiScreen(new WolframCredits(this));
            }
        }
    }
}

