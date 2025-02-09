/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package net.wolframclient.account_manager;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.wolframclient.Wolfram;
import net.wolframclient.account_manager.AccountManagerScreen;
import net.wolframclient.account_manager.LoginUtils;
import net.wolframclient.gui.font.FontRenderers;
import org.lwjgl.input.Keyboard;

public final class AccountLoginScreen
extends GuiScreen {
    private GuiButton mainButton;
    private GuiTextField nameField;
    private GuiTextField passwordField;
    private final AccountManagerScreen prevScreen;
    private final boolean addToList;

    public AccountLoginScreen(AccountManagerScreen prevScreen, boolean addToList) {
        this.prevScreen = prevScreen;
        this.addToList = addToList;
    }

    @Override
    public void updateScreen() {
        this.mainButton.enabled = !this.nameField.getText().isEmpty();
        this.nameField.updateCursorCounter();
        this.passwordField.updateCursorCounter();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents((boolean)true);
        this.mainButton = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, this.addToList ? "Add" : "Login");
        this.buttonList.add(this.mainButton);
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, "Cancel"));
        this.nameField = new GuiTextField(1, FontRenderers.DEFAULT, this.width / 2 - 100, 66, 200, 20);
        this.nameField.setFocused(true);
        this.nameField.setMaxStringLength(128);
        this.nameField.setText("");
        this.passwordField = new GuiTextField(1, FontRenderers.DEFAULT, this.width / 2 - 100, 106, 200, 20);
        this.passwordField.setMaxStringLength(128);
        this.passwordField.setText("");
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            if (this.nameField.getText().isEmpty()) {
                return;
            }
            if (this.nameField.getText().equalsIgnoreCase("Alexander01998")) {
                Wolfram.getWolfram().accountManager.lastLoginStatus = "Can't use the username Alexander01998!";
                Wolfram.getWolfram().accountManager.lastLoginFailed = true;
                return;
            }
            if (this.addToList) {
                this.mc.displayGuiScreen(this.prevScreen);
                Wolfram.getWolfram().storageManager.altAccounts.set(this.nameField.getText(), this.passwordField.getText());
                Wolfram.getWolfram().storageManager.altAccounts.save();
            } else {
                String status;
                Wolfram.getWolfram().accountManager.lastLoginStatus = status = LoginUtils.login(this.nameField.getText(), this.passwordField.getText());
                boolean bl = Wolfram.getWolfram().accountManager.lastLoginFailed = !status.equals("Logged in as non-premium.") && !status.equals("Logged in as premium.");
                if (!Wolfram.getWolfram().accountManager.lastLoginFailed) {
                    this.mc.displayGuiScreen(this.prevScreen);
                }
            }
        } else if (button.id == 1) {
            this.mc.displayGuiScreen(this.prevScreen);
        }
    }

    @Override
    public void keyTyped(char ch, int key) throws IOException {
        super.keyTyped(ch, key);
        this.nameField.textboxKeyTyped(ch, key);
        this.passwordField.textboxKeyTyped(ch, key);
        if (key == 15) {
            if (this.nameField.isFocused()) {
                this.nameField.setFocused(false);
                this.passwordField.setFocused(true);
            } else {
                this.nameField.setFocused(true);
                this.passwordField.setFocused(false);
            }
        }
        if (key == 28) {
            this.actionPerformed(this.mainButton);
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) throws IOException {
        super.mouseClicked(par1, par2, par3);
        this.nameField.mouseClicked(par1, par2, par3);
        this.passwordField.mouseClicked(par1, par2, par3);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        this.drawCenteredString(FontRenderers.TITLE, this.addToList ? "Add an account" : "Login with an account", this.width / 2, 17, -263693982);
        this.drawString(this.mc.fontRendererObj, "Username / Email", this.width / 2 - 100, 53, -1337435806);
        this.drawString(this.mc.fontRendererObj, "Password (Optional)", this.width / 2 - 100, 94, -1337435806);
        this.nameField.drawTextBox();
        String password = this.passwordField.getText();
        this.passwordField.setText(password.replaceAll(".", "*"));
        this.passwordField.drawTextBox();
        this.passwordField.setText(password);
        super.drawScreen(par1, par2, par3);
        if (Wolfram.getWolfram().accountManager.lastLoginFailed) {
            this.mc.fontRendererObj.drawStringWithShadow(Wolfram.getWolfram().accountManager.lastLoginStatus, 2.0f, 2.0f, 0xFF0000);
        }
    }
}

