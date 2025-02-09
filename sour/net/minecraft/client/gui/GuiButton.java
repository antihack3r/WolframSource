/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonMinecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.utils.AnimationTimer;
import net.wolframclient.utils.RenderUtils;

public class GuiButton
extends GuiButtonMinecraft {
    protected AnimationTimer anim = new AnimationTimer(10);

    public GuiButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public GuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        this.drawButton(mc, mouseX, mouseY);
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int var5 = this.getHoverState(this.hovered);
            this.anim.update(this.hovered);
            int color = this.enabled ? -263693982 : 1883789666;
            this.mouseDragged(mc, mouseX, mouseY);
            FontRenderers.BIG.drawCenteredString(this.displayString, this.xPosition + this.width / 2, this.yPosition + 5, color, false);
            int stringWidth = FontRenderers.BIG.getStringWidth(this.displayString);
            if (this.enabled) {
                RenderUtils.drawLine2D((double)(this.xPosition + this.width / 2) - this.anim.getValue() * (double)stringWidth / 2.0, this.yPosition + 18, (double)(this.xPosition + this.width / 2) + this.anim.getValue() * (double)stringWidth / 2.0, this.yPosition + 18, 1.0f, color);
            }
        }
    }
}

