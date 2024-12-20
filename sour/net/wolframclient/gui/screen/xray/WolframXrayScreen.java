/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package net.wolframclient.gui.screen.xray;

import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.wolframclient.Wolfram;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.gui.screen.xray.WolframXraySlot;
import net.wolframclient.module.modules.render.Xray;
import org.lwjgl.input.Keyboard;

public class WolframXrayScreen
extends GuiScreen {
    public ArrayList<Object[]> blockList;
    private final GuiScreen parentScreen;
    public WolframXraySlot xrayList;
    public GuiTextField search;

    public WolframXrayScreen(GuiScreen parent) {
        this.blockList = Xray.getInstance().blockList;
        this.parentScreen = parent;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents((boolean)true);
        this.search = new GuiTextField(1, FontRenderers.DEFAULT, this.width / 2 - 100, this.height - 52, 200, 20);
        this.search.setFocused(true);
        this.search.setMaxStringLength(128);
        this.search.setText("");
        this.buttonList.add(new GuiButton(1, this.width / 2 - 155, this.height - 28, 153, 20, "Toggle"));
        this.buttonList.add(new GuiButton(200, this.width / 2 + 2, this.height - 28, 153, 20, "Done"));
        this.xrayList = new WolframXraySlot(this);
    }

    @Override
    public void updateScreen() {
        this.search.updateCursorCounter();
    }

    @Override
    public void handleMouseInput() throws IOException {
        this.xrayList.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void keyTyped(char ch, int key) throws IOException {
        super.keyTyped(ch, key);
        this.search.textboxKeyTyped(ch, key);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.xrayList.drawScreen(mouseX, mouseY, partialTicks);
        this.search.drawTextBox();
        this.drawCenteredString(FontRenderers.TITLE, "Xray Manager", this.width / 2, 15, -263693982);
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
                Block block = Block.getBlockById((Integer)this.blockList.get(this.xrayList.currentSlot)[0]);
                if (Xray.getInstance().isXrayBlock(block)) {
                    Wolfram.getWolfram().storageManager.xrayBlocks.remove(new Integer(Block.getIdFromBlock(block)).toString());
                } else {
                    Wolfram.getWolfram().storageManager.xrayBlocks.add(new Integer(Block.getIdFromBlock(block)).toString());
                }
                Wolfram.getWolfram().storageManager.xrayBlocks.save();
            }
        }
    }
}

