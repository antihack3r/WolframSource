/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.gui.screen.keybinds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.wolframclient.Wolfram;
import net.wolframclient.gui.screen.keybinds.WolframGuiKeybind;
import net.wolframclient.keybind.Keybind;

public class WolframGuiKeybindSlot
extends GuiSlot {
    private final WolframGuiKeybind owner;
    public int currentSlot = -1;

    public WolframGuiKeybindSlot(WolframGuiKeybind owner) {
        this(owner, Minecraft.getMinecraft(), owner.width, owner.height, 32, owner.height - 61, 13);
    }

    public WolframGuiKeybindSlot(WolframGuiKeybind owner, Minecraft mcIn, int width, int height, int top, int bottom, int slotHeight) {
        super(mcIn, width, height, top, bottom, slotHeight);
        this.owner = owner;
    }

    public boolean currentSlotInBounds() {
        return this.currentSlot > -1 && this.currentSlot < Wolfram.getWolfram().keybindManager.getList().size();
    }

    @Override
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
        this.currentSlot = slotIndex;
    }

    @Override
    protected boolean isSelected(int slotIndex) {
        return slotIndex == this.currentSlot;
    }

    @Override
    protected void drawBackground() {
    }

    @Override
    protected void drawSlot(int slotIndex, int left, int top, int height, int mouseX, int mouseY, float partialTicks) {
        Keybind keybind = (Keybind)Wolfram.getWolfram().keybindManager.getList().get(slotIndex);
        this.owner.drawString(this.mc.fontRendererObj, String.valueOf(keybind.getKeyName()) + " - " + keybind.getCommand(), left + 1, top + 1, -263693982);
    }

    @Override
    protected int getSize() {
        return Wolfram.getWolfram().keybindManager.getList().size();
    }
}

