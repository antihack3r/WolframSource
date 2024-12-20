/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui_editor;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.wolframclient.Wolfram;
import net.wolframclient.clickgui.Window;
import net.wolframclient.gui.font.FontRenderers;

public final class EditWindowScreen
extends GuiScreen {
    private final GuiScreen prevScreen;
    private final Window window;
    private GuiTextField titleField;
    private GuiButton doneButton;

    public EditWindowScreen(GuiScreen prevScreen, Window window) {
        this.prevScreen = prevScreen;
        this.window = window;
    }

    @Override
    public void initGui() {
        this.titleField = new GuiTextField(1, FontRenderers.DEFAULT, this.width / 2 - 100, 66, 200, 20);
        this.titleField.setFocused(true);
        this.titleField.setMaxStringLength(25);
        this.titleField.setText(this.window.getTitle());
        this.titleField.setLabel("Title");
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, 100, 200, 20, "Pinnable: " + (this.window.isPinnable() ? "Yes" : "No")));
        this.doneButton = new GuiButton(200, this.width / 2 - 100, this.height - 28, 200, 20, "Done");
        this.buttonList.add(this.doneButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1: {
                this.window.setPinnable(!this.window.isPinnable());
                button.displayString = "Pinnable: " + (this.window.isPinnable() ? "Yes" : "No");
                break;
            }
            case 200: {
                this.window.setTitle(this.titleField.getText());
                Minecraft.getMinecraft().displayGuiScreen(this.prevScreen);
                Wolfram.getWolfram().guiManager.gui.reloadHubWindow();
            }
        }
    }

    @Override
    public void keyTyped(char ch, int key) throws IOException {
        super.keyTyped(ch, key);
        this.titleField.textboxKeyTyped(ch, key);
        if (key == 28 || key == 1) {
            this.actionPerformed(this.doneButton);
        }
    }

    @Override
    public void updateScreen() {
        this.titleField.updateCursorCounter();
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        this.drawCenteredString(FontRenderers.TITLE, "Edit Window", this.width / 2, 15, -263693982);
        this.titleField.drawTextBox();
        super.drawScreen(par1, par2, par3);
    }
}

