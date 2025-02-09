/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.gui.screen.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.utils.AnimationTimer;
import net.wolframclient.utils.RenderUtils;

public class WolframButton
extends GuiButton {
    AnimationTimer anim = new AnimationTimer(10);

    public WolframButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x - 5, y - 5, FontRenderers.TITLE.getStringWidth(buttonText) + 10, FontRenderers.TITLE.getStringHeight(buttonText) + 10, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            this.getHoverState(this.hovered);
            this.anim.update(this.hovered);
            int color = -263693982;
            this.mouseDragged(mc, mouseX, mouseY);
            FontRenderers.TITLE.drawString(this.displayString, this.xPosition + 5, this.yPosition + 5, color);
            int stringWidth = FontRenderers.TITLE.getStringWidth(this.displayString);
            RenderUtils.drawLine2D((double)(this.xPosition + 5 + stringWidth / 2) - this.anim.getValue() * (double)stringWidth / 2.0, this.yPosition + 22, (double)(this.xPosition + 5 + stringWidth / 2) + this.anim.getValue() * (double)stringWidth / 2.0, this.yPosition + 22, 1.0f, color);
        }
    }
}

