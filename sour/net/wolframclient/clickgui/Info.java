/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui;

import net.minecraft.util.EnumFacing;
import net.wolframclient.clickgui.Component;
import net.wolframclient.clickgui.Window;
import net.wolframclient.clickgui.WolframGui;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.compatibility.WPlayer;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.utils.RenderUtils;

public final class Info
extends Component {
    private boolean heightInitialized;

    public Info(Window window, int id, int offX, int offY, String title, String tooltip) {
        super(window, id, offX, offY, title, tooltip);
        this.width = Math.max(80, window.getWidth());
        this.height = 80;
        this.type = "Info";
        this.editable = false;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        int fontHeight = FontRenderers.DEFAULT.FONT_HEIGHT;
        int currentY = this.y;
        String coords = "Pos: " + (int)Math.floor(WMinecraft.getPlayer().posX) + " " + (int)Math.floor(WMinecraft.getPlayer().posY) + " " + (int)Math.floor(WMinecraft.getPlayer().posZ);
        RenderUtils.drawRect(this.x, currentY, this.width, 14.0f, WolframGui.getBackgroundColor());
        FontRenderers.DEFAULT.drawString(coords, (float)(this.x + 2), (float)(currentY + 7) - (float)fontHeight / 2.0f, 0xFFFFFF);
        EnumFacing facing = WPlayer.getHorizontalFacing();
        String facingText = "Facing: " + facing.getName().substring(0, 1).toUpperCase() + facing.getName().substring(1) + " (" + (facing.getAxisDirection().getOffset() > 0 ? "+" : "-") + facing.getAxis().name() + ")";
        RenderUtils.drawRect(this.x, currentY += 14, this.width, 14.0f, WolframGui.getBackgroundColor());
        FontRenderers.DEFAULT.drawString(facingText, (float)(this.x + 2), (float)(currentY + 7) - (float)fontHeight / 2.0f, 0xFFFFFF);
        this.height = (currentY += 14) - this.y;
        if (!this.heightInitialized) {
            this.heightInitialized = true;
            this.window.repositionComponents();
        }
    }

    @Override
    public void mouseUpdates(int mouseX, int mouseY, boolean isPressed) {
    }
}

