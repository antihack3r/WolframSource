/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.math.MathHelper;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.utils.RenderUtils;

public class GuiOptionSlider
extends GuiButton {
    private float sliderValue = 1.0f;
    public boolean dragging;
    private final GameSettings.Options options;
    private final float minValue;
    private final float maxValue;

    public GuiOptionSlider(int buttonId, int x, int y, GameSettings.Options optionIn) {
        this(buttonId, x, y, optionIn, 0.0f, 1.0f);
    }

    public GuiOptionSlider(int buttonId, int x, int y, GameSettings.Options optionIn, float minValueIn, float maxValue) {
        super(buttonId, x, y, 150, 20, "");
        this.options = optionIn;
        this.minValue = minValueIn;
        this.maxValue = maxValue;
        Minecraft minecraft = Minecraft.getMinecraft();
        this.sliderValue = optionIn.normalizeValue(minecraft.gameSettings.getOptionFloatValue(optionIn));
        this.displayString = minecraft.gameSettings.getKeyBinding(optionIn);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        int var5 = this.getHoverState(this.hovered);
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
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible && this.dragging) {
            this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
            this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0f, 1.0f);
            float f = this.options.denormalizeValue(this.sliderValue);
            mc.gameSettings.setOptionFloatValue(this.options, f);
            this.sliderValue = this.options.normalizeValue(f);
            this.displayString = mc.gameSettings.getKeyBinding(this.options);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
            this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0f, 1.0f);
            mc.gameSettings.setOptionFloatValue(this.options, this.options.denormalizeValue(this.sliderValue));
            this.displayString = mc.gameSettings.getKeyBinding(this.options);
            this.dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }
}

