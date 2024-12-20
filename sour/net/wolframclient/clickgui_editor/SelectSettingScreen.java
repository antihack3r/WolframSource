/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui_editor;

import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.wolframclient.Wolfram;
import net.wolframclient.clickgui.Checkbox;
import net.wolframclient.clickgui.Component;
import net.wolframclient.clickgui.Slider;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.storage.SettingData;

public final class SelectSettingScreen
extends GuiScreen {
    private final GuiScreen prevScreen;
    private final Component component;
    private final ArrayList<SettingData> settings = new ArrayList();
    private ListGui listGui;
    private GuiButton selectButton;
    private GuiButton backButton;

    public SelectSettingScreen(GuiScreen prevScreen, Component component) {
        this.prevScreen = prevScreen;
        this.component = component;
        boolean isFloat = component instanceof Slider;
        for (SettingData data : Wolfram.getWolfram().allData) {
            if (data.isFloat != isFloat) continue;
            this.settings.add(data);
        }
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
                SettingData setting = this.settings.get(this.listGui.selectedSlot);
                if (this.component instanceof Slider) {
                    Slider slider = (Slider)this.component;
                    slider.setStorage(setting.storage);
                    slider.setSetting(setting.setting);
                    slider.min = setting.min;
                    slider.max = setting.max;
                    slider.increment = setting.increment;
                } else if (this.component instanceof Checkbox) {
                    Checkbox checkbox = (Checkbox)this.component;
                    checkbox.storage = setting.storage;
                    checkbox.setting = setting.setting;
                }
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
        this.drawCenteredString(FontRenderers.TITLE, "Select Setting", this.width / 2, 15, -263693982);
        this.listGui.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private final class ListGui
    extends GuiSlot {
        private int selectedSlot;

        public ListGui(Minecraft mcIn, int width, int height, int top, int bottom, int slotHeight) {
            super(mcIn, width, height, top, bottom, slotHeight);
            this.selectedSlot = -1;
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
            SettingData data = (SettingData)SelectSettingScreen.this.settings.get(index);
            this.mc.fontRendererObj.drawString(String.valueOf(data.name) + " - " + data.setting, left + 1, top + 1, -263693982);
        }

        @Override
        protected int getSize() {
            return SelectSettingScreen.this.settings.size();
        }
    }
}

