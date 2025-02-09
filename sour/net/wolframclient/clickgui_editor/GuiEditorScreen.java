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
import net.wolframclient.clickgui_editor.ComponentEditorScreen;
import net.wolframclient.clickgui_editor.EditWindowScreen;
import net.wolframclient.gui.font.FontRenderers;

public final class GuiEditorScreen
extends GuiScreen {
    private ListGui listGui;
    private GuiButton selectButton;
    private GuiButton editButton;
    private GuiButton removeButton;

    @Override
    public void initGui() {
        this.listGui = new ListGui(this.mc, this.width, this.height, 32, this.height - 61, 13);
        this.selectButton = new GuiButton(1, this.width / 2 - 203, this.height - 50, 100, 20, "Select");
        this.buttonList.add(this.selectButton);
        this.editButton = new GuiButton(2, this.width / 2 - 101, this.height - 50, 100, 20, "Edit");
        this.buttonList.add(this.editButton);
        this.removeButton = new GuiButton(3, this.width / 2 + 1, this.height - 50, 100, 20, "Remove");
        this.buttonList.add(this.removeButton);
        this.buttonList.add(new GuiButton(4, this.width / 2 + 103, this.height - 50, 100, 20, "Add"));
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
                this.mc.displayGuiScreen(new ComponentEditorScreen(Wolfram.getWolfram().guiManager.gui.getWindows().get(this.listGui.currentSlot), this));
                break;
            }
            case 2: {
                this.mc.displayGuiScreen(new EditWindowScreen(this, Wolfram.getWolfram().guiManager.gui.getWindows().get(this.listGui.currentSlot)));
                break;
            }
            case 3: {
                Wolfram.getWolfram().guiManager.gui.getWindows().remove(this.listGui.currentSlot);
                Wolfram.getWolfram().guiManager.gui.reloadHubWindow();
                break;
            }
            case 4: {
                Window window = new Window("New Window", 50, 5);
                window.setEnabled(true);
                window.repositionComponents();
                Wolfram.getWolfram().guiManager.gui.getWindows().add(window);
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
        this.selectButton.enabled = inBounds = this.listGui.currentSlot > -1 && this.listGui.currentSlot < this.listGui.getSize();
        this.editButton.enabled = inBounds;
        this.removeButton.enabled = inBounds;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(FontRenderers.TITLE, "GUI Editor", this.width / 2, 15, -263693982);
        this.listGui.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private static final class ListGui
    extends GuiSlot {
        public int currentSlot = -1;

        public ListGui(Minecraft mcIn, int width, int height, int top, int bottom, int slotHeight) {
            super(mcIn, width, height, top, bottom, slotHeight);
        }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
            this.currentSlot = slotIndex;
        }

        @Override
        protected boolean isSelected(int index) {
            return index == this.currentSlot;
        }

        @Override
        protected void drawBackground() {
        }

        @Override
        protected void drawSlot(int index, int left, int top, int height, int mouseX, int mouseY, float partialTicks) {
            Window window = Wolfram.getWolfram().guiManager.gui.getWindows().get(index);
            this.mc.fontRendererObj.drawString(window.getTitle(), left + 1, top + 1, -263693982);
        }

        @Override
        protected int getSize() {
            return Wolfram.getWolfram().guiManager.gui.getWindows().size();
        }
    }
}

