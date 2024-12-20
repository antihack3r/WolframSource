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
import net.wolframclient.clickgui.ModuleButton;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.module.Module;

public final class SelectModScreen
extends GuiScreen {
    private final GuiScreen prevScreen;
    private final ModuleButton moduleButton;
    private ListGui listGui;
    private GuiButton selectButton;
    private GuiButton backButton;

    public SelectModScreen(GuiScreen prevScreen, ModuleButton moduleButton) {
        this.prevScreen = prevScreen;
        this.moduleButton = moduleButton;
    }

    @Override
    public void initGui() {
        this.listGui = new ListGui(this.mc, this.width, this.height, 32, this.height - 61, 13);
        this.selectButton = new GuiButton(1, this.width / 2 - 100, this.height - 50, 200, 20, "Select");
        this.buttonList.add(this.selectButton);
        this.backButton = new GuiButton(200, this.width / 2 - 100, this.height - 28, 200, 20, "Back");
        this.buttonList.add(this.backButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 200: {
                this.mc.displayGuiScreen(this.prevScreen);
                break;
            }
            case 1: {
                this.moduleButton.setModule((Module)Wolfram.getWolfram().moduleManager.getList().get(this.listGui.selectedSlot));
                this.mc.displayGuiScreen(this.prevScreen);
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.listGui.handleMouseInput();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.actionPerformed(this.backButton);
        }
    }

    @Override
    public void updateScreen() {
        this.selectButton.enabled = this.listGui.selectedSlot > -1 && this.listGui.selectedSlot < this.listGui.getSize();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(FontRenderers.TITLE, "Select Mod", this.width / 2, 15, -263693982);
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
            Module module = (Module)Wolfram.getWolfram().moduleManager.getList().get(index);
            this.mc.fontRendererObj.drawString(module.getName(), left + 1, top + 1, -263693982);
        }

        @Override
        protected int getSize() {
            return Wolfram.getWolfram().moduleManager.getList().size();
        }
    }
}

