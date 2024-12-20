/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package net.wolframclient.clickgui;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.player.EntityPlayer;
import net.wolframclient.clickgui.Component;
import net.wolframclient.clickgui.Window;
import net.wolframclient.clickgui.WolframGui;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.gui.font.WolframFontRenderer;
import net.wolframclient.utils.EntityFakePlayer;
import net.wolframclient.utils.RenderUtils;

public final class TextRadar
extends Component {
    private int lastHeight = 0;

    public TextRadar(Window window, int id, int offX, int offY, String title, String tooltip) {
        super(window, id, offX, offY, title, tooltip);
        this.width = Math.max(80, window.getWidth());
        this.type = "TextRadar";
        this.editable = false;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        RenderUtils.drawRect(this.x, this.y, this.width, 2.0f, WolframGui.getBackgroundColor());
        int currentY = this.y + 2;
        int renderWidth = this.width - (this.window.isScrollbarEnabled() ? 2 : 0) - 4;
        for (EntityPlayer player : WMinecraft.getWorld().playerEntities) {
            if (player == WMinecraft.getPlayer() || player instanceof EntityFakePlayer) continue;
            int distance = (int)WMinecraft.getPlayer().getDistanceToEntity(player);
            ChatFormatting color = distance < 10 ? ChatFormatting.RED : (distance < 30 ? ChatFormatting.YELLOW : ChatFormatting.GREEN);
            String distanceString = " [" + color + distance + ChatFormatting.RESET + "]";
            WolframFontRenderer fr = FontRenderers.SMALL;
            int maxNameWidth = renderWidth - fr.getStringWidth(distanceString);
            String nameString = fr.trimStringToWidth(player.getName(), maxNameWidth);
            RenderUtils.drawRect(this.x, currentY, this.width, 10.0f, WolframGui.getBackgroundColor());
            fr.drawString(String.valueOf(nameString) + distanceString, this.x + 2, currentY + 1, 0xFFFFFF, false);
            currentY += 10;
        }
        RenderUtils.drawRect(this.x, currentY, this.width, 2.0f, WolframGui.getBackgroundColor());
        this.height = (currentY += 2) - this.y;
        if (this.height != this.lastHeight) {
            this.window.repositionComponents();
            this.lastHeight = this.height;
        }
    }

    @Override
    public void mouseUpdates(int mouseX, int mouseY, boolean isPressed) {
    }
}

