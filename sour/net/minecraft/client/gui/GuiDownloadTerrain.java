/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.wolframclient.Wolfram;

public class GuiDownloadTerrain
extends GuiScreen {
    private final NetHandlerPlayClient connection;
    private int progress;

    public GuiDownloadTerrain(NetHandlerPlayClient netHandler) {
        this.connection = netHandler;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
    }

    @Override
    public void updateScreen() {
        ++this.progress;
        if (this.progress % 20 == 0 && Wolfram.getWolfram().getClientSettings().getInt("mc112x_compatibility") < 2) {
            this.connection.sendPacket(new CPacketKeepAlive());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(0);
        this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingTerrain", new Object[0]), this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

