/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui;

import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiListButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.IntHashMap;

public class GuiPageButtonList
extends GuiListExtended {
    private final List<GuiEntry> entries = Lists.newArrayList();
    private final IntHashMap<Gui> componentMap = new IntHashMap();
    private final List<GuiTextField> editBoxes = Lists.newArrayList();
    private final GuiListEntry[][] pages;
    private int page;
    private final GuiResponder responder;
    private Gui focusedControl;

    public GuiPageButtonList(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn, GuiResponder p_i45536_7_, GuiListEntry[] ... p_i45536_8_) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.responder = p_i45536_7_;
        this.pages = p_i45536_8_;
        this.centerListVertically = false;
        this.populateComponents();
        this.populateEntries();
    }

    private void populateComponents() {
        GuiListEntry[][] guiListEntryArray = this.pages;
        int n = this.pages.length;
        int n2 = 0;
        while (n2 < n) {
            GuiListEntry[] aguipagebuttonlist$guilistentry = guiListEntryArray[n2];
            int i = 0;
            while (i < aguipagebuttonlist$guilistentry.length) {
                GuiListEntry guipagebuttonlist$guilistentry = aguipagebuttonlist$guilistentry[i];
                GuiListEntry guipagebuttonlist$guilistentry1 = i < aguipagebuttonlist$guilistentry.length - 1 ? aguipagebuttonlist$guilistentry[i + 1] : null;
                Gui gui = this.createEntry(guipagebuttonlist$guilistentry, 0, guipagebuttonlist$guilistentry1 == null);
                Gui gui1 = this.createEntry(guipagebuttonlist$guilistentry1, 160, guipagebuttonlist$guilistentry == null);
                GuiEntry guipagebuttonlist$guientry = new GuiEntry(gui, gui1);
                this.entries.add(guipagebuttonlist$guientry);
                if (guipagebuttonlist$guilistentry != null && gui != null) {
                    this.componentMap.addKey(guipagebuttonlist$guilistentry.getId(), gui);
                    if (gui instanceof GuiTextField) {
                        this.editBoxes.add((GuiTextField)gui);
                    }
                }
                if (guipagebuttonlist$guilistentry1 != null && gui1 != null) {
                    this.componentMap.addKey(guipagebuttonlist$guilistentry1.getId(), gui1);
                    if (gui1 instanceof GuiTextField) {
                        this.editBoxes.add((GuiTextField)gui1);
                    }
                }
                i += 2;
            }
            ++n2;
        }
    }

    private void populateEntries() {
        this.entries.clear();
        int i = 0;
        while (i < this.pages[this.page].length) {
            GuiListEntry guipagebuttonlist$guilistentry = this.pages[this.page][i];
            GuiListEntry guipagebuttonlist$guilistentry1 = i < this.pages[this.page].length - 1 ? this.pages[this.page][i + 1] : null;
            Gui gui = this.componentMap.lookup(guipagebuttonlist$guilistentry.getId());
            Gui gui1 = guipagebuttonlist$guilistentry1 != null ? this.componentMap.lookup(guipagebuttonlist$guilistentry1.getId()) : null;
            GuiEntry guipagebuttonlist$guientry = new GuiEntry(gui, gui1);
            this.entries.add(guipagebuttonlist$guientry);
            i += 2;
        }
    }

    public void setPage(int p_181156_1_) {
        if (p_181156_1_ != this.page) {
            int i = this.page;
            this.page = p_181156_1_;
            this.populateEntries();
            this.markVisibility(i, p_181156_1_);
            this.amountScrolled = 0.0f;
        }
    }

    public int getPage() {
        return this.page;
    }

    public int getPageCount() {
        return this.pages.length;
    }

    public Gui getFocusedControl() {
        return this.focusedControl;
    }

    public void previousPage() {
        if (this.page > 0) {
            this.setPage(this.page - 1);
        }
    }

    public void nextPage() {
        if (this.page < this.pages.length - 1) {
            this.setPage(this.page + 1);
        }
    }

    public Gui getComponent(int p_178061_1_) {
        return this.componentMap.lookup(p_178061_1_);
    }

    private void markVisibility(int p_178060_1_, int p_178060_2_) {
        GuiListEntry[] guiListEntryArray = this.pages[p_178060_1_];
        int n = guiListEntryArray.length;
        int n2 = 0;
        while (n2 < n) {
            GuiListEntry guipagebuttonlist$guilistentry = guiListEntryArray[n2];
            if (guipagebuttonlist$guilistentry != null) {
                this.setComponentVisibility(this.componentMap.lookup(guipagebuttonlist$guilistentry.getId()), false);
            }
            ++n2;
        }
        guiListEntryArray = this.pages[p_178060_2_];
        n = guiListEntryArray.length;
        n2 = 0;
        while (n2 < n) {
            GuiListEntry guipagebuttonlist$guilistentry1 = guiListEntryArray[n2];
            if (guipagebuttonlist$guilistentry1 != null) {
                this.setComponentVisibility(this.componentMap.lookup(guipagebuttonlist$guilistentry1.getId()), true);
            }
            ++n2;
        }
    }

    private void setComponentVisibility(Gui p_178066_1_, boolean p_178066_2_) {
        if (p_178066_1_ instanceof GuiButton) {
            ((GuiButton)p_178066_1_).visible = p_178066_2_;
        } else if (p_178066_1_ instanceof GuiTextField) {
            ((GuiTextField)p_178066_1_).setVisible(p_178066_2_);
        } else if (p_178066_1_ instanceof GuiLabel) {
            ((GuiLabel)p_178066_1_).visible = p_178066_2_;
        }
    }

    @Nullable
    private Gui createEntry(@Nullable GuiListEntry p_178058_1_, int p_178058_2_, boolean p_178058_3_) {
        if (p_178058_1_ instanceof GuiSlideEntry) {
            return this.createSlider(this.width / 2 - 155 + p_178058_2_, 0, (GuiSlideEntry)p_178058_1_);
        }
        if (p_178058_1_ instanceof GuiButtonEntry) {
            return this.createButton(this.width / 2 - 155 + p_178058_2_, 0, (GuiButtonEntry)p_178058_1_);
        }
        if (p_178058_1_ instanceof EditBoxEntry) {
            return this.createTextField(this.width / 2 - 155 + p_178058_2_, 0, (EditBoxEntry)p_178058_1_);
        }
        return p_178058_1_ instanceof GuiLabelEntry ? this.createLabel(this.width / 2 - 155 + p_178058_2_, 0, (GuiLabelEntry)p_178058_1_, p_178058_3_) : null;
    }

    public void setActive(boolean p_181155_1_) {
        for (GuiEntry guipagebuttonlist$guientry : this.entries) {
            if (guipagebuttonlist$guientry.component1 instanceof GuiButton) {
                ((GuiButton)((GuiEntry)guipagebuttonlist$guientry).component1).enabled = p_181155_1_;
            }
            if (!(guipagebuttonlist$guientry.component2 instanceof GuiButton)) continue;
            ((GuiButton)((GuiEntry)guipagebuttonlist$guientry).component2).enabled = p_181155_1_;
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
        boolean flag = super.mouseClicked(mouseX, mouseY, mouseEvent);
        int i = this.getSlotIndexFromScreenCoords(mouseX, mouseY);
        if (i >= 0) {
            GuiEntry guipagebuttonlist$guientry = this.getListEntry(i);
            if (this.focusedControl != guipagebuttonlist$guientry.focusedControl && this.focusedControl != null && this.focusedControl instanceof GuiTextField) {
                ((GuiTextField)this.focusedControl).setFocused(false);
            }
            this.focusedControl = guipagebuttonlist$guientry.focusedControl;
        }
        return flag;
    }

    private GuiSlider createSlider(int p_178067_1_, int p_178067_2_, GuiSlideEntry p_178067_3_) {
        GuiSlider guislider = new GuiSlider(this.responder, p_178067_3_.getId(), p_178067_1_, p_178067_2_, p_178067_3_.getCaption(), p_178067_3_.getMinValue(), p_178067_3_.getMaxValue(), p_178067_3_.getInitalValue(), p_178067_3_.getFormatter());
        guislider.visible = p_178067_3_.shouldStartVisible();
        return guislider;
    }

    private GuiListButton createButton(int p_178065_1_, int p_178065_2_, GuiButtonEntry p_178065_3_) {
        GuiListButton guilistbutton = new GuiListButton(this.responder, p_178065_3_.getId(), p_178065_1_, p_178065_2_, p_178065_3_.getCaption(), p_178065_3_.getInitialValue());
        guilistbutton.visible = p_178065_3_.shouldStartVisible();
        return guilistbutton;
    }

    private GuiTextField createTextField(int p_178068_1_, int p_178068_2_, EditBoxEntry p_178068_3_) {
        GuiTextField guitextfield = new GuiTextField(p_178068_3_.getId(), this.mc.fontRendererObj, p_178068_1_, p_178068_2_, 150, 20);
        guitextfield.setText(p_178068_3_.getCaption());
        guitextfield.setGuiResponder(this.responder);
        guitextfield.setVisible(p_178068_3_.shouldStartVisible());
        guitextfield.setValidator(p_178068_3_.getFilter());
        return guitextfield;
    }

    private GuiLabel createLabel(int p_178063_1_, int p_178063_2_, GuiLabelEntry p_178063_3_, boolean p_178063_4_) {
        GuiLabel guilabel = p_178063_4_ ? new GuiLabel(this.mc.fontRendererObj, p_178063_3_.getId(), p_178063_1_, p_178063_2_, this.width - p_178063_1_ * 2, 20, -1) : new GuiLabel(this.mc.fontRendererObj, p_178063_3_.getId(), p_178063_1_, p_178063_2_, 150, 20, -1);
        guilabel.visible = p_178063_3_.shouldStartVisible();
        guilabel.addLine(p_178063_3_.getCaption());
        guilabel.setCentered();
        return guilabel;
    }

    public void onKeyPressed(char p_178062_1_, int p_178062_2_) {
        block1: {
            int i;
            block2: {
                GuiTextField guitextfield;
                block3: {
                    int i1;
                    block4: {
                        if (!(this.focusedControl instanceof GuiTextField)) break block1;
                        guitextfield = (GuiTextField)this.focusedControl;
                        if (GuiScreen.isKeyComboCtrlV(p_178062_2_)) break block2;
                        if (p_178062_2_ != 15) break block3;
                        guitextfield.setFocused(false);
                        int k = this.editBoxes.indexOf(this.focusedControl);
                        k = GuiScreen.isShiftKeyDown() ? (k == 0 ? this.editBoxes.size() - 1 : --k) : (k == this.editBoxes.size() - 1 ? 0 : ++k);
                        this.focusedControl = this.editBoxes.get(k);
                        guitextfield = (GuiTextField)this.focusedControl;
                        guitextfield.setFocused(true);
                        int l = guitextfield.yPosition + this.slotHeight;
                        i1 = guitextfield.yPosition;
                        if (l <= this.bottom) break block4;
                        this.amountScrolled += (float)(l - this.bottom);
                        break block1;
                    }
                    if (i1 >= this.top) break block1;
                    this.amountScrolled = i1;
                    break block1;
                }
                guitextfield.textboxKeyTyped(p_178062_1_, p_178062_2_);
                break block1;
            }
            String s = GuiScreen.getClipboardString();
            String[] astring = s.split(";");
            int j = i = this.editBoxes.indexOf(this.focusedControl);
            String[] stringArray = astring;
            int n = astring.length;
            int n2 = 0;
            while (n2 < n) {
                String s1 = stringArray[n2];
                GuiTextField guitextfield1 = this.editBoxes.get(j);
                guitextfield1.setText(s1);
                guitextfield1.func_190516_a(guitextfield1.getId(), s1);
                j = j == this.editBoxes.size() - 1 ? 0 : ++j;
                if (j == i) break;
                ++n2;
            }
        }
    }

    @Override
    public GuiEntry getListEntry(int index) {
        return this.entries.get(index);
    }

    @Override
    public int getSize() {
        return this.entries.size();
    }

    @Override
    public int getListWidth() {
        return 400;
    }

    @Override
    protected int getScrollBarX() {
        return super.getScrollBarX() + 32;
    }

    public static class EditBoxEntry
    extends GuiListEntry {
        private final Predicate<String> filter;

        public EditBoxEntry(int p_i45534_1_, String p_i45534_2_, boolean p_i45534_3_, Predicate<String> p_i45534_4_) {
            super(p_i45534_1_, p_i45534_2_, p_i45534_3_);
            this.filter = (Predicate)MoreObjects.firstNonNull(p_i45534_4_, (Object)Predicates.alwaysTrue());
        }

        public Predicate<String> getFilter() {
            return this.filter;
        }
    }

    public static class GuiButtonEntry
    extends GuiListEntry {
        private final boolean initialValue;

        public GuiButtonEntry(int p_i45535_1_, String p_i45535_2_, boolean p_i45535_3_, boolean p_i45535_4_) {
            super(p_i45535_1_, p_i45535_2_, p_i45535_3_);
            this.initialValue = p_i45535_4_;
        }

        public boolean getInitialValue() {
            return this.initialValue;
        }
    }

    public static class GuiEntry
    implements GuiListExtended.IGuiListEntry {
        private final Minecraft client = Minecraft.getMinecraft();
        private final Gui component1;
        private final Gui component2;
        private Gui focusedControl;

        public GuiEntry(@Nullable Gui p_i45533_1_, @Nullable Gui p_i45533_2_) {
            this.component1 = p_i45533_1_;
            this.component2 = p_i45533_2_;
        }

        public Gui getComponent1() {
            return this.component1;
        }

        public Gui getComponent2() {
            return this.component2;
        }

        @Override
        public void func_192634_a(int p_192634_1_, int p_192634_2_, int p_192634_3_, int p_192634_4_, int p_192634_5_, int p_192634_6_, int p_192634_7_, boolean p_192634_8_, float p_192634_9_) {
            this.func_192636_a(this.component1, p_192634_3_, p_192634_6_, p_192634_7_, false, p_192634_9_);
            this.func_192636_a(this.component2, p_192634_3_, p_192634_6_, p_192634_7_, false, p_192634_9_);
        }

        private void func_192636_a(Gui p_192636_1_, int p_192636_2_, int p_192636_3_, int p_192636_4_, boolean p_192636_5_, float p_192636_6_) {
            if (p_192636_1_ != null) {
                if (p_192636_1_ instanceof GuiButton) {
                    this.func_192635_a((GuiButton)p_192636_1_, p_192636_2_, p_192636_3_, p_192636_4_, p_192636_5_, p_192636_6_);
                } else if (p_192636_1_ instanceof GuiTextField) {
                    this.renderTextField((GuiTextField)p_192636_1_, p_192636_2_, p_192636_5_);
                } else if (p_192636_1_ instanceof GuiLabel) {
                    this.renderLabel((GuiLabel)p_192636_1_, p_192636_2_, p_192636_3_, p_192636_4_, p_192636_5_);
                }
            }
        }

        private void func_192635_a(GuiButton p_192635_1_, int p_192635_2_, int p_192635_3_, int p_192635_4_, boolean p_192635_5_, float p_192635_6_) {
            p_192635_1_.yPosition = p_192635_2_;
            if (!p_192635_5_) {
                p_192635_1_.drawButton(this.client, p_192635_3_, p_192635_4_, p_192635_6_);
            }
        }

        private void renderTextField(GuiTextField p_178027_1_, int p_178027_2_, boolean p_178027_3_) {
            p_178027_1_.yPosition = p_178027_2_;
            if (!p_178027_3_) {
                p_178027_1_.drawTextBox();
            }
        }

        private void renderLabel(GuiLabel p_178025_1_, int p_178025_2_, int p_178025_3_, int p_178025_4_, boolean p_178025_5_) {
            p_178025_1_.y = p_178025_2_;
            if (!p_178025_5_) {
                p_178025_1_.drawLabel(this.client, p_178025_3_, p_178025_4_);
            }
        }

        @Override
        public void func_192633_a(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) {
            this.func_192636_a(this.component1, p_192633_3_, 0, 0, true, p_192633_4_);
            this.func_192636_a(this.component2, p_192633_3_, 0, 0, true, p_192633_4_);
        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            boolean flag = this.clickComponent(this.component1, mouseX, mouseY, mouseEvent);
            boolean flag1 = this.clickComponent(this.component2, mouseX, mouseY, mouseEvent);
            return flag || flag1;
        }

        private boolean clickComponent(Gui p_178026_1_, int p_178026_2_, int p_178026_3_, int p_178026_4_) {
            if (p_178026_1_ == null) {
                return false;
            }
            if (p_178026_1_ instanceof GuiButton) {
                return this.clickButton((GuiButton)p_178026_1_, p_178026_2_, p_178026_3_, p_178026_4_);
            }
            if (p_178026_1_ instanceof GuiTextField) {
                this.clickTextField((GuiTextField)p_178026_1_, p_178026_2_, p_178026_3_, p_178026_4_);
            }
            return false;
        }

        private boolean clickButton(GuiButton p_178023_1_, int p_178023_2_, int p_178023_3_, int p_178023_4_) {
            boolean flag = p_178023_1_.mousePressed(this.client, p_178023_2_, p_178023_3_);
            if (flag) {
                this.focusedControl = p_178023_1_;
            }
            return flag;
        }

        private void clickTextField(GuiTextField p_178018_1_, int p_178018_2_, int p_178018_3_, int p_178018_4_) {
            p_178018_1_.mouseClicked(p_178018_2_, p_178018_3_, p_178018_4_);
            if (p_178018_1_.isFocused()) {
                this.focusedControl = p_178018_1_;
            }
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            this.releaseComponent(this.component1, x, y, mouseEvent);
            this.releaseComponent(this.component2, x, y, mouseEvent);
        }

        private void releaseComponent(Gui p_178016_1_, int p_178016_2_, int p_178016_3_, int p_178016_4_) {
            if (p_178016_1_ != null && p_178016_1_ instanceof GuiButton) {
                this.releaseButton((GuiButton)p_178016_1_, p_178016_2_, p_178016_3_, p_178016_4_);
            }
        }

        private void releaseButton(GuiButton p_178019_1_, int p_178019_2_, int p_178019_3_, int p_178019_4_) {
            p_178019_1_.mouseReleased(p_178019_2_, p_178019_3_);
        }
    }

    public static class GuiLabelEntry
    extends GuiListEntry {
        public GuiLabelEntry(int p_i45532_1_, String p_i45532_2_, boolean p_i45532_3_) {
            super(p_i45532_1_, p_i45532_2_, p_i45532_3_);
        }
    }

    public static class GuiListEntry {
        private final int id;
        private final String caption;
        private final boolean startVisible;

        public GuiListEntry(int p_i45531_1_, String p_i45531_2_, boolean p_i45531_3_) {
            this.id = p_i45531_1_;
            this.caption = p_i45531_2_;
            this.startVisible = p_i45531_3_;
        }

        public int getId() {
            return this.id;
        }

        public String getCaption() {
            return this.caption;
        }

        public boolean shouldStartVisible() {
            return this.startVisible;
        }
    }

    public static interface GuiResponder {
        public void setEntryValue(int var1, boolean var2);

        public void setEntryValue(int var1, float var2);

        public void setEntryValue(int var1, String var2);
    }

    public static class GuiSlideEntry
    extends GuiListEntry {
        private final GuiSlider.FormatHelper formatter;
        private final float minValue;
        private final float maxValue;
        private final float initialValue;

        public GuiSlideEntry(int p_i45530_1_, String p_i45530_2_, boolean p_i45530_3_, GuiSlider.FormatHelper p_i45530_4_, float p_i45530_5_, float p_i45530_6_, float p_i45530_7_) {
            super(p_i45530_1_, p_i45530_2_, p_i45530_3_);
            this.formatter = p_i45530_4_;
            this.minValue = p_i45530_5_;
            this.maxValue = p_i45530_6_;
            this.initialValue = p_i45530_7_;
        }

        public GuiSlider.FormatHelper getFormatter() {
            return this.formatter;
        }

        public float getMinValue() {
            return this.minValue;
        }

        public float getMaxValue() {
            return this.maxValue;
        }

        public float getInitalValue() {
            return this.initialValue;
        }
    }
}

