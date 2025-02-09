/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui_editor;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.wolframclient.clickgui.Checkbox;
import net.wolframclient.clickgui.CommandButton;
import net.wolframclient.clickgui.Component;
import net.wolframclient.clickgui.ModuleButton;
import net.wolframclient.clickgui.Slider;
import net.wolframclient.clickgui_editor.SelectModScreen;
import net.wolframclient.clickgui_editor.SelectSettingScreen;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.module.Module;

public final class EditComponentScreen
extends GuiScreen {
    private final GuiScreen parent;
    private final Component component;
    private GuiTextField tooltipField;
    private GuiTextField titleField;
    private GuiTextField commandField;
    private GuiButton doneButton;

    public EditComponentScreen(GuiScreen parent, Component component) {
        this.parent = parent;
        this.component = component;
    }

    @Override
    public void initGui() {
        this.tooltipField = new GuiTextField(1, FontRenderers.DEFAULT, this.width / 2 - 100, 90, 200, 20);
        this.tooltipField.setFocused(true);
        this.tooltipField.setMaxStringLength(125);
        this.tooltipField.setText(this.component.tooltip);
        this.tooltipField.setLabel("Description");
        this.titleField = new GuiTextField(1, FontRenderers.DEFAULT, this.width / 2 - 100, 65, 200, 20);
        this.titleField.setFocused(false);
        this.titleField.setMaxStringLength(125);
        this.titleField.setText(this.component.title);
        this.titleField.setLabel("Title");
        if (this.component instanceof CommandButton) {
            this.commandField = new GuiTextField(1, FontRenderers.DEFAULT, this.width / 2 - 100, 115, 200, 20);
            this.commandField.setFocused(false);
            this.commandField.setMaxStringLength(1000);
            this.commandField.setText(((CommandButton)this.component).command);
            this.commandField.setLabel("Command");
        } else if (this.component instanceof ModuleButton) {
            Module module = ((ModuleButton)this.component).getModule();
            this.buttonList.add(new GuiButton(1, this.width / 2 - 100, 115, 200, 20, "Mod: " + (module == null ? "None" : module.getName())));
        } else if (this.component instanceof Checkbox) {
            String setting = ((Checkbox)this.component).setting;
            this.buttonList.add(new GuiButton(2, this.width / 2 - 100, 115, 200, 20, "Setting: " + (setting == null ? "None" : setting)));
        } else if (this.component instanceof Slider) {
            String setting = ((Slider)this.component).getSetting();
            this.buttonList.add(new GuiButton(2, this.width / 2 - 100, 115, 200, 20, "Setting: " + (setting == null ? "None" : setting)));
        }
        this.doneButton = new GuiButton(200, this.width / 2 - 100, this.height - 28, 200, 20, "Done");
        this.buttonList.add(this.doneButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        this.component.title = this.titleField.getText();
        this.component.tooltip = this.tooltipField.getText();
        if (this.commandField != null) {
            ((CommandButton)this.component).command = this.commandField.getText();
        }
        switch (button.id) {
            case 200: {
                this.mc.displayGuiScreen(this.parent);
                break;
            }
            case 1: {
                this.mc.displayGuiScreen(new SelectModScreen(this, (ModuleButton)this.component));
                break;
            }
            case 2: {
                this.mc.displayGuiScreen(new SelectSettingScreen(this, this.component));
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        this.titleField.mouseClicked(mouseX, mouseY, button);
        this.tooltipField.mouseClicked(mouseX, mouseY, button);
        if (this.commandField != null) {
            this.commandField.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void keyTyped(char ch, int key) throws IOException {
        this.tooltipField.textboxKeyTyped(ch, key);
        this.titleField.textboxKeyTyped(ch, key);
        if (this.commandField != null) {
            this.commandField.textboxKeyTyped(ch, key);
        }
        if (key == 28 || key == 1) {
            this.actionPerformed(this.doneButton);
        }
    }

    @Override
    public void updateScreen() {
        this.tooltipField.updateCursorCounter();
        this.titleField.updateCursorCounter();
        if (this.commandField != null) {
            this.commandField.updateCursorCounter();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(FontRenderers.TITLE, "Edit Component", this.width / 2, 15, -263693982);
        this.titleField.drawTextBox();
        this.tooltipField.drawTextBox();
        if (this.commandField != null) {
            this.commandField.drawTextBox();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

