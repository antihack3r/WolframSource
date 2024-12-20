/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.utils.RenderUtils;

public class GuiLockIconButton
extends GuiButton {
    private boolean locked;

    public GuiLockIconButton(int p_i45538_1_, int p_i45538_2_, int p_i45538_3_) {
        super(p_i45538_1_, p_i45538_2_, p_i45538_3_, 20, 20, "");
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean lockedIn) {
        this.locked = lockedIn;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int var5 = this.getHoverState(this.hovered);
            this.anim.update(this.hovered);
            String displayString = this.locked ? "Locked" : "Lock";
            int color = this.enabled ? -263693982 : 1883789666;
            this.mouseDragged(mc, mouseX, mouseY);
            FontRenderers.BIG.drawCenteredString(displayString, this.xPosition + this.width / 2, this.yPosition + 5, color, false);
            int stringWidth = FontRenderers.BIG.getStringWidth(displayString);
            if (this.enabled) {
                RenderUtils.drawLine2D((double)(this.xPosition + this.width / 2) - this.anim.getValue() * (double)stringWidth / 2.0, this.yPosition + 18, (double)(this.xPosition + this.width / 2) + this.anim.getValue() * (double)stringWidth / 2.0, this.yPosition + 18, 1.0f, color);
            }
        }
    }

    static enum Icon {
        LOCKED(0, 146),
        LOCKED_HOVER(0, 166),
        LOCKED_DISABLED(0, 186),
        UNLOCKED(20, 146),
        UNLOCKED_HOVER(20, 166),
        UNLOCKED_DISABLED(20, 186);

        private final int x;
        private final int y;

        private Icon(int p_i45537_3_, int p_i45537_4_) {
            this.x = p_i45537_3_;
            this.y = p_i45537_4_;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }
    }
}

