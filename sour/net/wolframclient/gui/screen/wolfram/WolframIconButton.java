/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.gui.screen.wolfram;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.wolframclient.utils.AnimationTimer;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

public class WolframIconButton
extends GuiButton {
    private final ResourceLocation img;
    private final int u;
    private final int v;
    private final int uWidth;
    private final int vHeight;
    private final int tileWidth;
    private final int tileHeight;
    int color;
    AnimationTimer anim = new AnimationTimer(10);

    public WolframIconButton(int buttonId, int x, int y, int widthIn, int heightIn, ResourceLocation img, int color) {
        super(buttonId, x, y, widthIn / 2, heightIn / 2, "");
        this.img = img;
        this.u = 0;
        this.v = 0;
        this.uWidth = widthIn;
        this.vHeight = heightIn;
        this.tileWidth = widthIn;
        this.tileHeight = heightIn;
        this.color = color;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = mouseX >= this.xPosition - 5 && mouseY >= this.yPosition - 5 && mouseX < this.xPosition + this.width + 5 && mouseY < this.yPosition + this.height + 5;
            this.getHoverState(this.hovered);
            this.color = -263693982;
            this.anim.update(this.hovered && this.enabled);
            RenderUtils.setColor(this.color);
            GL11.glEnable((int)3042);
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glPushMatrix();
            GL11.glTranslated((double)this.xPosition, (double)this.yPosition, (double)0.0);
            float alpha = (float)(this.color >> 24 & 0xFF) / 255.0f;
            float red = (float)(this.color >> 16 & 0xFF) / 255.0f;
            float green = (float)(this.color >> 8 & 0xFF) / 255.0f;
            float blue = (float)(this.color & 0xFF) / 255.0f;
            GL11.glColor4d((double)((double)red + (1.0 - (double)red) * this.anim.getValue()), (double)((double)green + (1.0 - (double)green) * this.anim.getValue()), (double)((double)blue + (1.0 - (double)blue) * this.anim.getValue()), (double)(alpha == 0.0f ? 1.0f : alpha));
            GL11.glScaled((double)0.5, (double)0.5, (double)0.5);
            mc.getTextureManager().bindTexture(this.img);
            Gui.drawScaledCustomSizeModalRect(0, 0, this.u, this.v, this.uWidth, this.vHeight, this.width * 2, this.height * 2, this.tileWidth, this.tileHeight);
            GL11.glPopMatrix();
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.xPosition - 5 && mouseY >= this.yPosition - 5 && mouseX < this.xPosition + this.width + 5 && mouseY < this.yPosition + this.height + 5;
    }
}

