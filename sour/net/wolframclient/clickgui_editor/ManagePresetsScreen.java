/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui_editor;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.wolframclient.Wolfram;
import net.wolframclient.clickgui.Window;
import net.wolframclient.clickgui.WindowPreset;
import net.wolframclient.clickgui_editor.EditPresetScreen;
import net.wolframclient.gui.font.FontRenderers;

public final class ManagePresetsScreen
extends GuiScreen {
    private ListGui listGui;
    private GuiButton editButton;
    private GuiButton removeButton;
    private GuiButton loadButton;

    @Override
    public void initGui() {
        this.listGui = new ListGui(this.mc, this.width, this.height, 32, this.height - 61, 13);
        this.editButton = new GuiButton(1, this.width / 2 - 254, this.height - 50, 100, 20, "Edit");
        this.buttonList.add(this.editButton);
        this.removeButton = new GuiButton(2, this.width / 2 - 152, this.height - 50, 100, 20, "Remove");
        this.buttonList.add(this.removeButton);
        this.buttonList.add(new GuiButton(3, this.width / 2 - 50, this.height - 50, 100, 20, "Add"));
        this.loadButton = new GuiButton(5, this.width / 2 + 52, this.height - 50, 100, 20, "Load");
        this.buttonList.add(this.loadButton);
        this.buttonList.add(new GuiButton(4, this.width / 2 + 154, this.height - 50, 100, 20, "Default"));
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 28, 200, 20, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 200: {
                this.mc.displayGuiScreen(null);
                break;
            }
            case 1: {
                this.mc.displayGuiScreen(new EditPresetScreen(this, Wolfram.getWolfram().guiManager.gui.getPresets().get(this.listGui.selectedSlot)));
                break;
            }
            case 2: {
                Wolfram.getWolfram().guiManager.gui.getPresets().remove(this.listGui.selectedSlot);
                break;
            }
            case 3: {
                WindowPreset preset = new WindowPreset("Preset " + (this.listGui.getSize() + 1));
                for (Window window : Wolfram.getWolfram().guiManager.gui.getWindows()) {
                    preset.add(window);
                }
                Wolfram.getWolfram().guiManager.gui.getPresets().add(preset);
                Wolfram.getWolfram().guiManager.gui.savePresets();
                Wolfram.getWolfram().guiManager.gui.loadPresets();
                break;
            }
            case 4: {
                Wolfram.getWolfram().guiManager.gui.setupDefaultPreset();
                Wolfram.getWolfram().guiManager.gui.loadPreset(Wolfram.getWolfram().guiManager.gui.getDefaultPreset());
                Wolfram.getWolfram().guiManager.gui.reloadHubWindow();
                break;
            }
            case 5: {
                Wolfram.getWolfram().guiManager.gui.loadPreset(Wolfram.getWolfram().guiManager.gui.getPresets().get(this.listGui.selectedSlot));
                Wolfram.getWolfram().guiManager.gui.reloadHubWindow();
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
        boolean inBounds;
        this.editButton.enabled = inBounds = this.listGui.selectedSlot >= 0 && this.listGui.selectedSlot < this.listGui.getSize();
        this.removeButton.enabled = inBounds;
        this.loadButton.enabled = inBounds;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(FontRenderers.TITLE, "Manage Presets", this.width / 2, 15, -263693982);
        this.listGui.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private static final class ListGui
    extends GuiSlot {
        public int selectedSlot = -1;

        public ListGui(Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
            super(mc, width, height, top, bottom, slotHeight);
        }

        @Override
        protected void elementClicked(int index, boolean isDoubleClick, int mouseX, int mouseY) {
            this.selectedSlot = index;
        }

        @Override
        protected boolean isSelected(int index) {
            return index == this.selectedSlot;
        }

        @Override
        protected void drawBackground() {
        }

        @Override
        protected void drawSlot(int index, int left, int top, int height, int mouseX, int mouseY, float partialTicks) {
            this.mc.fontRendererObj.drawString(Wolfram.getWolfram().guiManager.gui.getPresets().get(index).getTitle(), left + 1, top + 1, -263693982);
        }

        @Override
        protected int getSize() {
            return Wolfram.getWolfram().guiManager.gui.getPresets().size();
        }
    }
}

