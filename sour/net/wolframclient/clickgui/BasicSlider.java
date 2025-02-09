/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 */
package net.wolframclient.clickgui;

import java.math.BigDecimal;
import net.wolframclient.Wolfram;
import net.wolframclient.clickgui.Component;
import net.wolframclient.clickgui.Window;
import net.wolframclient.clickgui.WolframGui;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.utils.MathUtils;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.input.Mouse;

public class BasicSlider
extends Component {
    public float min;
    public float max;
    public float value;
    public float increment;
    protected boolean isDragging;
    public String customDisplayValue = null;

    public BasicSlider(Window window, int id, int offX, int offY, String title, float min, float max, float increment, String tooltip) {
        super(window, id, offX, offY, title, tooltip);
        this.min = min;
        this.max = max;
        this.increment = increment;
        this.width = 100;
        this.height = 18;
        this.type = "BasicSlider";
    }

    @Override
    public void render(int mouseX, int mouseY) {
        int scrollbarWidth = this.window.isScrollbarEnabled() ? 2 : 0;
        int renderWidth = this.width - scrollbarWidth;
        RenderUtils.drawRect(this.x, this.y, renderWidth, this.height, this.hovering ? 0 : WolframGui.getBackgroundColor());
        String displayValue = this.customDisplayValue != null ? this.customDisplayValue : this.round(this.value, 1);
        FontRenderers.SMALL.drawString(displayValue, this.x + renderWidth - FontRenderers.SMALL.getStringWidth(displayValue) - 2, this.y + 4, GuiManager.getHexMainColor());
        FontRenderers.SMALL.drawString(this.title, this.x + 2, this.y + 4, 0xFFFFFF);
        RenderUtils.drawRect(this.x + 2, this.y + this.height - 4, renderWidth - 4, 1.0f, GuiManager.getHexMainColor() + 0x50000000);
        RenderUtils.drawRect(this.x + 2, this.y + this.height - 4, MathUtils.map(this.value, this.min, this.max, 0.0f, renderWidth - 4), 1.0f, GuiManager.getHexMainColor());
    }

    private String round(float f, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(f));
        bd = bd.setScale(decimalPlace, 4);
        return "" + bd;
    }

    @Override
    public void update(int mouseX, int mouseY) {
        super.update(mouseX, mouseY);
        if (this.isDragging) {
            this.value = MathUtils.map(mouseX - this.x, 2.0f, this.width - 2 - (this.window.isScrollbarEnabled() ? 2 : 0), this.min, this.max);
            this.value -= this.value % this.increment;
            if (this.value > this.max) {
                this.value = this.max;
            }
            if (this.value < this.min) {
                this.value = this.min;
            }
        }
        if (this.window.mouseOver(mouseX, mouseY) && this.contains(mouseX, mouseY)) {
            Wolfram.getWolfram().guiManager.gui.setTooltip(this.tooltip);
        }
    }

    @Override
    public void mouseUpdates(int mouseX, int mouseY, boolean isPressed) {
        boolean bl = this.hovering = this.contains(mouseX, mouseY) && this.window.mouseOver(mouseX, mouseY);
        if (isPressed && !this.wasMousePressed && this.hovering) {
            this.isDragging = true;
        }
        if (!isPressed) {
            this.isDragging = false;
        }
        this.wasMousePressed = isPressed;
    }

    @Override
    public void noMouseUpdates() {
        super.noMouseUpdates();
        if (!Mouse.isButtonDown((int)0)) {
            this.isDragging = false;
        }
    }
}

