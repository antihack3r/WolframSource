/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.gui.screen.keybinds;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.wolframclient.Wolfram;
import net.wolframclient.gui.screen.keybinds.WolframGuiKeybindSlot;
import net.wolframclient.gui.screen.keybinds.WolframGuiNewKeybind;
import net.wolframclient.keybind.Keybind;

public class WolframGuiKeybind
extends GuiScreen {
    private final GuiScreen parentScreen;
    public WolframGuiKeybindSlot keybindSlot;
    private GuiButton remove;

    public WolframGuiKeybind(GuiScreen parent) {
        this.parentScreen = parent;
    }

    @Override
    public void initGui() {
        this.keybindSlot = new WolframGuiKeybindSlot(this);
        this.buttonList.add(new GuiButton(1, this.width / 2 - 155, this.height - 53, 153, 20, "Add"));
        this.remove = new GuiButton(2, this.width / 2 + 2, this.height - 53, 153, 20, "Remove");
        this.buttonList.add(this.remove);
        this.remove.enabled = this.keybindSlot.currentSlotInBounds();
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 28, 200, 20, "Done"));
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.keybindSlot.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.remove.enabled = this.keybindSlot.currentSlotInBounds();
        this.drawDefaultBackground();
        this.keybindSlot.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, "Keybind Manager", this.width / 2, 15, -263693982);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 200: {
                this.mc.displayGuiScreen(this.parentScreen);
                break;
            }
            case 1: {
                this.mc.displayGuiScreen(new WolframGuiNewKeybind(this));
                break;
            }
            case 2: {
                Wolfram.getWolfram().keybindManager.remove((Keybind)Wolfram.getWolfram().keybindManager.getList().get(this.keybindSlot.currentSlot));
                Wolfram.getWolfram().keybindManager.saveKeybinds();
            }
        }
    }
}

