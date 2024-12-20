/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui_editor;

import java.io.IOException;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.wolframclient.clickgui.Component;
import net.wolframclient.clickgui.Window;
import net.wolframclient.clickgui_editor.EditComponentScreen;
import net.wolframclient.clickgui_editor.SelectComponentTypeScreen;
import net.wolframclient.gui.font.FontRenderers;

public final class ComponentEditorScreen
extends GuiScreen {
    private final Window window;
    private final GuiScreen parent;
    private ListGui listGui;
    private GuiButton editButton;
    private GuiButton removeButton;
    private GuiButton moveUpButton;
    private GuiButton moveDownButton;
    private GuiButton backButton;

    public ComponentEditorScreen(Window window, GuiScreen parent) {
        this.window = window;
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.listGui = new ListGui(this.mc, this.width, this.height, 32, this.height - 61, 13);
        this.editButton = new GuiButton(1, this.width / 2 - 254, this.height - 50, 100, 20, "Edit");
        this.buttonList.add(this.editButton);
        this.removeButton = new GuiButton(2, this.width / 2 - 152, this.height - 50, 100, 20, "Remove");
        this.buttonList.add(this.removeButton);
        this.buttonList.add(new GuiButton(3, this.width / 2 - 50, this.height - 50, 100, 20, "Add"));
        this.moveUpButton = new GuiButton(4, this.width / 2 + 52, this.height - 50, 100, 20, "Move Up");
        this.buttonList.add(this.moveUpButton);
        this.moveDownButton = new GuiButton(5, this.width / 2 + 154, this.height - 50, 100, 20, "Move Down");
        this.buttonList.add(this.moveDownButton);
        this.backButton = new GuiButton(200, this.width / 2 - 100, this.height - 28, 200, 20, "Back");
        this.buttonList.add(this.backButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 200: {
                this.mc.displayGuiScreen(this.parent);
                break;
            }
            case 1: {
                this.mc.displayGuiScreen(new EditComponentScreen(this, this.window.getChildren().get(this.listGui.selectedSlot)));
                break;
            }
            case 2: {
                this.window.getChildren().remove(this.listGui.selectedSlot);
                this.window.repositionComponents();
                break;
            }
            case 3: {
                this.mc.displayGuiScreen(new SelectComponentTypeScreen(this, this.window));
                break;
            }
            case 4: {
                Collections.swap(this.window.getChildren(), this.listGui.selectedSlot, this.listGui.selectedSlot - 1);
                ListGui listGui = this.listGui;
                listGui.selectedSlot = listGui.selectedSlot - 1;
                break;
            }
            case 5: {
                Collections.swap(this.window.getChildren(), this.listGui.selectedSlot, this.listGui.selectedSlot + 1);
                ListGui listGui = this.listGui;
                listGui.selectedSlot = listGui.selectedSlot + 1;
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.listGui.handleMouseInput();
    }

    @Override
    public void keyTyped(char ch, int key) throws IOException {
        if (key == 1) {
            this.actionPerformed(this.backButton);
        }
    }

    @Override
    public void updateScreen() {
        if (!this.listGui.currentSlotInBounds()) {
            this.editButton.enabled = false;
            this.removeButton.enabled = false;
            this.moveUpButton.enabled = false;
            this.moveDownButton.enabled = false;
            return;
        }
        this.editButton.enabled = this.window.getChildren().get((int)((ListGui)this.listGui).selectedSlot).editable;
        this.removeButton.enabled = true;
        this.moveUpButton.enabled = this.listGui.currentSlotInBoundsOffset(1);
        this.moveDownButton.enabled = this.listGui.currentSlotInBoundsOffset(-1);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(FontRenderers.TITLE, "Component Editor", this.width / 2, 15, -263693982);
        this.listGui.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private final class ListGui
    extends GuiSlot {
        private int selectedSlot;

        public ListGui(Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
            super(mc, width, height, top, bottom, slotHeight);
            this.selectedSlot = -1;
        }

        public boolean currentSlotInBounds() {
            return this.selectedSlot > -1 && this.selectedSlot < this.getSize();
        }

        public boolean currentSlotInBoundsOffset(int offset) {
            if (offset > 0) {
                return this.selectedSlot > -1 + offset && this.selectedSlot < this.getSize();
            }
            return this.selectedSlot > -1 && this.selectedSlot < this.getSize() + offset;
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
            Component component = ComponentEditorScreen.this.window.getChildren().get(index);
            ComponentEditorScreen.this.drawString(this.mc.fontRendererObj, component.title, left + 1, top + 1, -263693982);
        }

        @Override
        protected int getSize() {
            return ComponentEditorScreen.this.window.getChildren().size();
        }
    }
}

