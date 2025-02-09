/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package net.wolframclient.gui.screen.keybinds;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.wolframclient.Wolfram;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.gui.screen.keybinds.WolframGuiKeybind;
import net.wolframclient.keybind.Keybind;
import org.lwjgl.input.Keyboard;

public class WolframGuiNewKeybind
extends GuiScreen {
    private GuiTextField keybind;
    private GuiTextField command;
    private GuiButton keybindButton;
    private final WolframGuiKeybind prevScreen;
    private boolean keybindButtonSelected;

    public WolframGuiNewKeybind(WolframGuiKeybind prevScreen) {
        this.prevScreen = prevScreen;
    }

    @Override
    public void updateScreen() {
        this.keybind.updateCursorCounter();
        this.command.updateCursorCounter();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents((boolean)true);
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, "Add"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, "Cancel"));
        this.keybind = new GuiTextField(1, FontRenderers.DEFAULT, this.width / 2 - 100, 66, 129, 20);
        this.keybind.setFocused(true);
        this.keybind.setMaxStringLength(128);
        this.keybind.setText("");
        this.command = new GuiTextField(1, FontRenderers.DEFAULT, this.width / 2 - 100, 106, 200, 20);
        this.command.setMaxStringLength(128);
        this.command.setText("");
        this.keybindButton = new GuiButton(2, this.width / 2 + 30, 65, 70, 22, "NONE");
        this.buttonList.add(this.keybindButton);
    }

    @Override
    protected void actionPerformed(GuiButton par1WolframButton) {
        if (par1WolframButton.id == 0) {
            Wolfram.getWolfram().keybindManager.add(new Keybind(Keyboard.getKeyIndex((String)this.keybind.getText().toUpperCase()), this.command.getText()));
            Wolfram.getWolfram().keybindManager.saveKeybinds();
            this.mc.displayGuiScreen(this.prevScreen);
        } else if (par1WolframButton.id == 1) {
            this.mc.displayGuiScreen(this.prevScreen);
        } else if (par1WolframButton.id == 2) {
            this.keybindButtonSelected = true;
        }
    }

    @Override
    public void keyTyped(char ch, int key) throws IOException {
        if (this.keybindButtonSelected) {
            this.keybindButtonSelected = false;
            if (key == 1) {
                return;
            }
            this.keybindButton.displayString = Keyboard.getKeyName((int)key);
            this.keybind.setText(Keyboard.getKeyName((int)key));
            return;
        }
        super.keyTyped(ch, key);
        this.keybind.textboxKeyTyped(ch, key);
        this.command.textboxKeyTyped(ch, key);
        if (key == 15) {
            if (this.keybind.isFocused()) {
                this.keybind.setFocused(false);
                this.command.setFocused(true);
            } else {
                this.keybind.setFocused(true);
                this.command.setFocused(false);
            }
        }
        if (key == 28) {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
        ((GuiButton)this.buttonList.get((int)0)).enabled = Keyboard.getKeyIndex((String)this.keybind.getText().toUpperCase()) != 0 && this.command.getText().length() > 0;
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) throws IOException {
        super.mouseClicked(par1, par2, par3);
        this.keybind.mouseClicked(par1, par2, par3);
        this.command.mouseClicked(par1, par2, par3);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.keybindButton.displayString = Keyboard.getKeyName((int)Keyboard.getKeyIndex((String)this.keybind.getText().toUpperCase()));
        if (this.keybindButton.displayString == null) {
            this.keybindButton.displayString = "NONE";
        }
        if (this.keybindButtonSelected) {
            this.keybindButton.displayString = "> " + this.keybindButton.displayString + " <";
        }
        this.drawDefaultBackground();
        this.drawCenteredString(this.mc.fontRendererObj, "You can run multiple commands at once", this.width / 2, 135, -1605871262);
        this.drawCenteredString(this.mc.fontRendererObj, "by separating them with '&&'", this.width / 2, 150, -1605871262);
        this.drawCenteredString(this.mc.fontRendererObj, "Add a keybind", this.width / 2, 17, -1605871262);
        this.drawString(this.mc.fontRendererObj, "Key", this.width / 2 - 100, 53, -263693982);
        this.drawString(this.mc.fontRendererObj, "Command", this.width / 2 - 100, 94, -263693982);
        this.keybind.drawTextBox();
        this.command.drawTextBox();
        super.drawScreen(par1, par2, par3);
        ((GuiButton)this.buttonList.get((int)0)).enabled = Keyboard.getKeyIndex((String)this.keybind.getText().toUpperCase()) != 0 && this.command.getText().length() > 0;
    }
}

