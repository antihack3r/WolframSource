/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.utils.RenderUtils;

public class WolframCustomButton
extends GuiButton {
    protected int delay;

    public WolframCustomButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public WolframCustomButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            boolean bl = this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            if (this.enabled) {
                this.delay = this.hovered ? ++this.delay : --this.delay;
                if (this.delay > 5) {
                    this.delay = 5;
                }
                if (this.delay < 0) {
                    this.delay = 0;
                }
            } else {
                this.delay = 0;
            }
            int alpha = Math.min(this.delay, 5);
            RenderUtils.drawBorderedRect(this.xPosition, this.yPosition, this.width, this.height, 0.5f, 9482955, 0x1FFFFFF + 0x1A000000 * alpha);
            this.mouseDragged(mc, mouseX, mouseY);
            int var6 = 0xFFFFFF;
            if (!this.enabled) {
                var6 = 0x888888;
            }
            FontRenderers.getFontRenderer().drawString(this.displayString, (float)this.xPosition + (float)this.width / 2.0f - (float)FontRenderers.getFontRenderer().getStringWidth(this.displayString) / 2.0f, (float)this.yPosition + (float)this.height / 2.0f - (float)(FontRenderers.getFontRenderer().FONT_HEIGHT / 2), var6, false);
        }
    }
}

