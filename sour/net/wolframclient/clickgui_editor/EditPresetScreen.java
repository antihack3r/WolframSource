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
import net.wolframclient.clickgui.WindowPreset;
import net.wolframclient.gui.font.FontRenderers;

public final class EditPresetScreen
extends GuiScreen {
    private final GuiScreen parent;
    private final WindowPreset preset;
    private GuiTextField titleField;
    private GuiButton doneButton;

    public EditPresetScreen(GuiScreen parent, WindowPreset preset) {
        this.parent = parent;
        this.preset = preset;
    }

    @Override
    public void initGui() {
        this.titleField = new GuiTextField(1, FontRenderers.DEFAULT, this.width / 2 - 100, 66, 200, 20);
        this.titleField.setFocused(true);
        this.titleField.setMaxStringLength(25);
        this.titleField.setText(this.preset.getTitle());
        this.titleField.setLabel("Title");
        this.doneButton = new GuiButton(200, this.width / 2 - 100, this.height - 28, 200, 20, "Done");
        this.buttonList.add(this.doneButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 200) {
            this.preset.setTitle(this.titleField.getText());
            Minecraft.getMinecraft().displayGuiScreen(this.parent);
            Wolfram.getWolfram().guiManager.gui.reloadHubWindow();
        }
    }

    @Override
    public void keyTyped(char ch, int key) throws IOException {
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
        this.drawCenteredString(FontRenderers.TITLE, "Edit Preset", this.width / 2, 15, -263693982);
        this.titleField.drawTextBox();
        super.drawScreen(par1, par2, par3);
    }
}

