/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

public class GuiButtonMinecraft
extends Gui {
    protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");
    protected int width = 200;
    protected int height = 20;
    public int xPosition;
    public int yPosition;
    public String displayString;
    public int id;
    public boolean enabled = true;
    public boolean visible = true;
    protected boolean hovered;

    public GuiButtonMinecraft(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public GuiButtonMinecraft(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        this.id = buttonId;
        this.xPosition = x;
        this.yPosition = y;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
    }

    protected int getHoverState(boolean mouseOver) {
        int i = 1;
        if (!this.enabled) {
            i = 0;
        } else if (mouseOver) {
            i = 2;
        }
        return i;
    }

    public void drawButton(Minecraft p_191745_1_, int p_191745_2_, int p_191745_3_, float p_191745_4_) {
        if (this.visible) {
            FontRenderer fontrenderer = p_191745_1_.fontRendererObj;
            p_191745_1_.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = p_191745_2_ >= this.xPosition && p_191745_3_ >= this.yPosition && p_191745_2_ < this.xPosition + this.width && p_191745_3_ < this.yPosition + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            this.mouseDragged(p_191745_1_, p_191745_2_, p_191745_3_);
            int j = 0xE0E0E0;
            if (!this.enabled) {
                j = 0xA0A0A0;
            } else if (this.hovered) {
                j = 0xFFFFA0;
            }
            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
        }
    }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
    }

    public void mouseReleased(int mouseX, int mouseY) {
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    public boolean isMouseOver() {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    public int getButtonWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}

