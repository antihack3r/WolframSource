/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui_editor;

import java.io.IOException;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.wolframclient.clickgui.Checkbox;
import net.wolframclient.clickgui.CommandButton;
import net.wolframclient.clickgui.Component;
import net.wolframclient.clickgui.Info;
import net.wolframclient.clickgui.ModuleButton;
import net.wolframclient.clickgui.ModuleList;
import net.wolframclient.clickgui.Radar;
import net.wolframclient.clickgui.Slider;
import net.wolframclient.clickgui.TabGui;
import net.wolframclient.clickgui.TextRadar;
import net.wolframclient.clickgui.Window;
import net.wolframclient.gui.font.FontRenderers;

public final class SelectComponentTypeScreen
extends GuiScreen {
    private static final Random RANDOM = new Random();
    private final Window window;
    private final GuiScreen prevScreen;
    private ListGui listGui;
    private GuiButton selectButton;

    public SelectComponentTypeScreen(GuiScreen prevScreen, Window window) {
        this.window = window;
        this.prevScreen = prevScreen;
    }

    @Override
    public void initGui() {
        this.listGui = new ListGui(this.mc, this.width, this.height, 32, this.height - 61, 13);
        this.selectButton = new GuiButton(1, this.width / 2 - 100, this.height - 50, 200, 20, "Select");
        this.buttonList.add(this.selectButton);
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 28, 200, 20, "Back"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 200: {
                this.mc.displayGuiScreen(this.prevScreen);
                break;
            }
            case 1: {
                Component component = this.listGui.createSelectedComponent(this.window);
                this.window.getChildren().add(component);
                this.window.repositionComponents();
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
    public void updateScreen() {
        this.selectButton.enabled = this.listGui.selectedSlot > -1 && this.listGui.selectedSlot < this.listGui.getSize();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(FontRenderers.TITLE, "Select Component Type", this.width / 2, 15, -263693982);
        this.listGui.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private static interface ComponentBuilder {
        public Component createNew(Window var1);
    }

    private static enum ComponentType {
        RADAR("Radar", w -> new Radar(w, RANDOM.nextInt(), 0, 0, "Radar", "")),
        SLIDER("Slider", w -> new Slider(null, w, RANDOM.nextInt(), 0, 0, "New Slider", "", 0.0f, 1.0f, 1.0E-5f, "")),
        CHECKBOX("Checkbox", w -> new Checkbox(null, w, RANDOM.nextInt(), 0, 0, "New Checkbox", "", "")),
        MODULE_BUTTON("Module Button", w -> new ModuleButton(w, RANDOM.nextInt(), 0, 0, "New Module Button", "", null)),
        COMMAND_BUTTON("Command Button", w -> new CommandButton(w, RANDOM.nextInt(), 0, 0, "New Command Button", "", "")),
        INFO("Info", w -> new Info(w, RANDOM.nextInt(), 0, 0, "Info", "")),
        TAB_GUI("Tab GUI", w -> new TabGui(w, RANDOM.nextInt(), 0, 0, "Tab GUI", "")),
        TEXT_RADAR("Text Radar", w -> new TextRadar(w, RANDOM.nextInt(), 0, 0, "Text Radar", "")),
        MODULE_LIST("Module List", w -> new ModuleList(w, RANDOM.nextInt(), 0, 0, "Module List", ""));

        private final String name;
        private final ComponentBuilder builder;

        private ComponentType(String name, ComponentBuilder builder) {
            this.name = name;
            this.builder = builder;
        }

        public static ComponentType get(int index) {
            return ComponentType.values()[index];
        }
    }

    private static final class ListGui
    extends GuiSlot {
        private int selectedSlot = -1;

        public ListGui(Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
            super(mc, width, height, top, bottom, slotHeight);
        }

        public Component createSelectedComponent(Window window) {
            return ComponentType.get(this.selectedSlot).builder.createNew(window);
        }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
            this.selectedSlot = slotIndex;
        }

        @Override
        protected boolean isSelected(int index) {
            return index == this.selectedSlot;
        }

        @Override
        protected int getSize() {
            return ComponentType.values().length;
        }

        @Override
        protected void drawBackground() {
        }

        @Override
        protected void drawSlot(int index, int left, int top, int height, int mouseX, int mouseY, float partialTicks) {
            this.mc.fontRendererObj.drawString(ComponentType.get(index).name, left + 1, top + 1, -263693982);
        }
    }
}

