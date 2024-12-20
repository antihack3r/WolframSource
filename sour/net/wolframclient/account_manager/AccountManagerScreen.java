/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.account_manager;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.wolframclient.Wolfram;
import net.wolframclient.account_manager.AccountListGui;
import net.wolframclient.account_manager.AccountLoginScreen;
import net.wolframclient.gui.font.FontRenderers;

public final class AccountManagerScreen
extends GuiScreen {
    private final GuiScreen prevScreen;
    private AccountListGui listGui;
    private GuiButton loginButton;
    private GuiButton removeButton;

    public AccountManagerScreen(GuiScreen prevScreen) {
        this.prevScreen = prevScreen;
    }

    @Override
    public void initGui() {
        this.listGui = new AccountListGui(this);
        this.loginButton = new GuiButton(4, this.width / 2 - 155, this.height - 52, 75, 20, "Login");
        this.buttonList.add(this.loginButton);
        this.buttonList.add(new GuiButton(2, this.width / 2 - 77, this.height - 52, 75, 20, "Add"));
        this.removeButton = new GuiButton(5, this.width / 2 + 2, this.height - 52, 75, 20, "Remove");
        this.buttonList.add(this.removeButton);
        this.buttonList.add(new GuiButton(3, this.width / 2 + 80, this.height - 52, 75, 20, "Import..."));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 155, this.height - 28, 75, 20, "Direct Login"));
        this.buttonList.add(new GuiButton(6, this.width / 2 - 77, this.height - 28, 75, 20, "Random"));
        this.buttonList.add(new GuiButton(7, this.width / 2 + 2, this.height - 28, 75, 20, "Auto-Remove"));
        this.buttonList.add(new GuiButton(200, this.width / 2 + 80, this.height - 28, 75, 20, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1: {
                this.mc.displayGuiScreen(new AccountLoginScreen(this, false));
                break;
            }
            case 2: {
                this.mc.displayGuiScreen(new AccountLoginScreen(this, true));
                break;
            }
            case 3: {
                Wolfram.getWolfram().accountManager.importAccounts();
                break;
            }
            case 4: {
                this.listGui.login();
                break;
            }
            case 5: {
                this.listGui.removeSelected();
                Wolfram.getWolfram().storageManager.altAccounts.save();
                break;
            }
            case 6: {
                Wolfram.getWolfram().accountManager.randomLogin();
                break;
            }
            case 7: {
                this.listGui.autoRemove();
                break;
            }
            case 200: {
                Wolfram.getWolfram().storageManager.altAccounts.save();
                this.mc.displayGuiScreen(this.prevScreen);
                break;
            }
            default: {
                this.listGui.actionPerformed(button);
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.listGui.handleMouseInput();
    }

    @Override
    public void updateScreen() {
        this.loginButton.enabled = this.listGui.currentSlotInBounds();
        this.removeButton.enabled = this.listGui.currentSlotInBounds();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.listGui.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(FontRenderers.TITLE, "Account Manager", this.width / 2, 15, -263693982);
        if (Wolfram.getWolfram().accountManager.lastLoginStatus != null) {
            this.mc.fontRendererObj.drawString(Wolfram.getWolfram().accountManager.lastLoginStatus, 2, 2, Wolfram.getWolfram().accountManager.lastLoginFailed ? 0xFF0000 : -251658241);
        }
        this.mc.fontRendererObj.drawString("Current Account: " + this.mc.getSession().getUsername(), 2, 12, -251658241);
        this.mc.fontRendererObj.drawString("Count: " + this.listGui.getSize(), this.width - this.mc.fontRendererObj.getStringWidth("Count: " + this.listGui.getSize()) - 5, 12, -251658241);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

