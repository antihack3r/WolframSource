/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.gui.screen.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WMath;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.utils.RenderUtils;

public class WolframOptionSlider
extends GuiButton {
    public float sliderValue;
    public boolean dragging;
    private final String option;

    public WolframOptionSlider(int buttonId, int x, int y, int width, int height, String option, String sliderText) {
        super(buttonId, x, y, width, height, "");
        this.sliderValue = Wolfram.getWolfram().storageManager.clientSettings.getFloat(option);
        Minecraft.getMinecraft();
        this.option = option;
        this.displayString = sliderText;
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }

    private void onUpdate() {
        Wolfram.getWolfram().storageManager.clientSettings.set(this.option, Float.valueOf(this.sliderValue));
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible && this.dragging) {
            this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
            this.sliderValue = WMath.clamp(this.sliderValue, 0.0f, 1.0f);
            this.onUpdate();
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        this.getHoverState(this.hovered);
        this.anim.update(this.hovered);
        int color = this.enabled ? -263693982 : 1883789666;
        this.mouseDragged(mc, mouseX, mouseY);
        FontRenderers.BIG.drawCenteredString(this.displayString, this.xPosition + this.width / 2, this.yPosition + 5, color, false);
        if (this.enabled) {
            RenderUtils.drawLine2D((double)(this.xPosition + this.width / 2) - this.anim.getValue() * (double)this.width / 2.0, this.yPosition, (double)(this.xPosition + this.width / 2) + this.anim.getValue() * (double)this.width / 2.0, this.yPosition, 1.0f, 1346918754);
            RenderUtils.drawLine2D((double)(this.xPosition + this.width / 2) - this.anim.getValue() * (double)this.width / 2.0, this.yPosition + this.height, (double)(this.xPosition + this.width / 2) + this.anim.getValue() * (double)this.width / 2.0, this.yPosition + this.height, 1.0f, 1346918754);
            RenderUtils.drawLine2D((double)(this.xPosition + this.width / 2) - this.anim.getValue() * (double)this.width / 2.0 + (double)((int)((double)this.sliderValue * (this.anim.getValue() * (double)this.width - 8.0))), this.yPosition, (double)(this.xPosition + this.width / 2) - this.anim.getValue() * (double)this.width / 2.0 + (double)((int)((double)this.sliderValue * (this.anim.getValue() * (double)this.width - 8.0))) + 8.0, this.yPosition, 1.0f, 21518690 + 0x1000000 * (int)(239.0 * this.anim.getValue()));
            RenderUtils.drawLine2D((double)(this.xPosition + this.width / 2) - this.anim.getValue() * (double)this.width / 2.0 + (double)((int)((double)this.sliderValue * (this.anim.getValue() * (double)this.width - 8.0))), this.yPosition + this.height, (double)(this.xPosition + this.width / 2) - this.anim.getValue() * (double)this.width / 2.0 + (double)((int)((double)this.sliderValue * (this.anim.getValue() * (double)this.width - 8.0))) + 8.0, this.yPosition + this.height, 1.0f, 21518690 + 0x1000000 * (int)(239.0 * this.anim.getValue()));
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
            this.sliderValue = WMath.clamp(this.sliderValue, 0.0f, 1.0f);
            this.onUpdate();
            this.dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        Wolfram.getWolfram().storageManager.clientSettings.save();
        this.dragging = false;
    }
}

