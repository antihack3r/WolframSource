/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.settings.GameSettings;

public class GuiOptionsRowList
extends GuiListExtended {
    private final List<Row> options = Lists.newArrayList();

    public GuiOptionsRowList(Minecraft mcIn, int p_i45015_2_, int p_i45015_3_, int p_i45015_4_, int p_i45015_5_, int p_i45015_6_, GameSettings.Options ... p_i45015_7_) {
        super(mcIn, p_i45015_2_, p_i45015_3_, p_i45015_4_, p_i45015_5_, p_i45015_6_);
        this.centerListVertically = false;
        int i = 0;
        while (i < p_i45015_7_.length) {
            GameSettings.Options gamesettings$options = p_i45015_7_[i];
            GameSettings.Options gamesettings$options1 = i < p_i45015_7_.length - 1 ? p_i45015_7_[i + 1] : null;
            GuiButton guibutton = this.createButton(mcIn, p_i45015_2_ / 2 - 155, 0, gamesettings$options);
            GuiButton guibutton1 = this.createButton(mcIn, p_i45015_2_ / 2 - 155 + 160, 0, gamesettings$options1);
            this.options.add(new Row(guibutton, guibutton1));
            i += 2;
        }
    }

    private GuiButton createButton(Minecraft mcIn, int p_148182_2_, int p_148182_3_, GameSettings.Options options) {
        if (options == null) {
            return null;
        }
        int i = options.returnEnumOrdinal();
        return options.getEnumFloat() ? new GuiOptionSlider(i, p_148182_2_, p_148182_3_, options) : new GuiOptionButton(i, p_148182_2_, p_148182_3_, options, mcIn.gameSettings.getKeyBinding(options));
    }

    @Override
    public Row getListEntry(int index) {
        return this.options.get(index);
    }

    @Override
    protected int getSize() {
        return this.options.size();
    }

    @Override
    public int getListWidth() {
        return 400;
    }

    @Override
    protected int getScrollBarX() {
        return super.getScrollBarX() + 32;
    }

    public static class Row
    implements GuiListExtended.IGuiListEntry {
        private final Minecraft client = Minecraft.getMinecraft();
        private final GuiButton buttonA;
        private final GuiButton buttonB;

        public Row(GuiButton buttonAIn, GuiButton buttonBIn) {
            this.buttonA = buttonAIn;
            this.buttonB = buttonBIn;
        }

        @Override
        public void func_192634_a(int p_192634_1_, int p_192634_2_, int p_192634_3_, int p_192634_4_, int p_192634_5_, int p_192634_6_, int p_192634_7_, boolean p_192634_8_, float p_192634_9_) {
            if (this.buttonA != null) {
                this.buttonA.yPosition = p_192634_3_;
                this.buttonA.drawButton(this.client, p_192634_6_, p_192634_7_, p_192634_9_);
            }
            if (this.buttonB != null) {
                this.buttonB.yPosition = p_192634_3_;
                this.buttonB.drawButton(this.client, p_192634_6_, p_192634_7_, p_192634_9_);
            }
        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            if (this.buttonA.mousePressed(this.client, mouseX, mouseY)) {
                if (this.buttonA instanceof GuiOptionButton) {
                    this.client.gameSettings.setOptionValue(((GuiOptionButton)this.buttonA).returnEnumOptions(), 1);
                    this.buttonA.displayString = this.client.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(this.buttonA.id));
                }
                return true;
            }
            if (this.buttonB != null && this.buttonB.mousePressed(this.client, mouseX, mouseY)) {
                if (this.buttonB instanceof GuiOptionButton) {
                    this.client.gameSettings.setOptionValue(((GuiOptionButton)this.buttonB).returnEnumOptions(), 1);
                    this.buttonB.displayString = this.client.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(this.buttonB.id));
                }
                return true;
            }
            return false;
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            if (this.buttonA != null) {
                this.buttonA.mouseReleased(x, y);
            }
            if (this.buttonB != null) {
                this.buttonB.mouseReleased(x, y);
            }
        }

        @Override
        public void func_192633_a(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) {
        }
    }
}

