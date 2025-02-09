/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.wolframclient.Wolfram;
import net.wolframclient.gui.screen.wolfram.WolframErrorScreen;
import net.wolframclient.gui.screen.wolfram.WolframLoading;

public class GuiDisconnected
extends GuiScreen {
    private final String reason;
    private final ITextComponent message;
    private List<String> multilineMessage;
    private final GuiScreen parentScreen;
    private int textHeight;
    private GuiButton reconnectAlt;

    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, ITextComponent chatComp) {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey, new Object[0]);
        this.message = chatComp;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.textHeight = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + this.fontRendererObj.FONT_HEIGHT, this.height - 30), I18n.format("gui.toMenu", new Object[0])));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + 25 + this.fontRendererObj.FONT_HEIGHT, "Reconnect"));
        this.reconnectAlt = new GuiButton(2, this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + 50 + this.fontRendererObj.FONT_HEIGHT, "Reconnect with a random Alt");
        this.buttonList.add(this.reconnectAlt);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiConnecting(null, this.mc, Wolfram.getWolfram().lastServer));
        } else if (button.id == 2) {
            WolframLoading loading = new WolframLoading(null, false);
            this.mc.displayGuiScreen(loading);
            loading.infoMsg = "Logging in to a random account...";
            Wolfram.getWolfram().accountManager.lastLoginFailed = !Wolfram.getWolfram().accountManager.randomLogin();
            Wolfram.getWolfram().accountManager.lastLoginStatus = null;
            if (Wolfram.getWolfram().accountManager.lastLoginFailed) {
                this.mc.displayGuiScreen(new WolframErrorScreen(this, "Failed to login after 20 attempts!!!"));
            } else {
                this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, Wolfram.getWolfram().lastServer));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.textHeight / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 0xAAAAAA);
        this.reconnectAlt.enabled = !Wolfram.getWolfram().storageManager.altAccounts.isEmpty();
        int i = this.height / 2 - this.textHeight / 2;
        if (this.multilineMessage != null) {
            for (String s : this.multilineMessage) {
                this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 0xFFFFFF);
                i += this.fontRendererObj.FONT_HEIGHT;
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

