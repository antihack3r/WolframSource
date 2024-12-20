/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.gui.screen.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.wolframclient.compatibility.WMath;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.module.modules.render.ChatTab;
import net.wolframclient.utils.MathUtils;
import net.wolframclient.utils.RenderUtils;

public class WolframNewChat
extends GuiNewChat {
    public WolframNewChat(Minecraft mcIn) {
        super(mcIn);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.getChatOpen()) {
            int topY = (-this.getLineCount() + 1) * 11 - 11 + 4 + 3;
            int i = this.chatTabs.size();
            while (i > 0) {
                int width = WMath.ceil((float)this.getChatWidth() / this.getChatScale()) + 1;
                int tabCount = Math.min(3, i);
                float tabWidth = (float)width / (float)tabCount;
                int j = 0;
                while (j < tabCount) {
                    if (MathUtils.contains(mouseX, mouseY, 5 + (int)((float)(tabCount - j - 1) * tabWidth), RenderUtils.getDisplayHeight() + topY - 2 - 50, tabCount - j - 1 == tabCount - 1 ? 5 + width : 4 + (int)((float)(tabCount - j) * tabWidth), RenderUtils.getDisplayHeight() + topY + 13 - 50)) {
                        this.currentTab = (ChatTab)this.chatTabs.get(i - j - 1);
                        return;
                    }
                    ++j;
                }
                topY -= 16;
                i -= 3;
            }
        }
    }

    @Override
    public void drawChat(int p_146230_1_) {
        try {
            if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
                int var14;
                int var11;
                int var2 = this.getLineCount();
                boolean var3 = false;
                int var4 = 0;
                int var5 = this.currentTab.lines.size();
                float var6 = this.mc.gameSettings.chatOpacity * 0.9f + 0.1f;
                if (this.getChatOpen()) {
                    var3 = true;
                }
                float var7 = this.getChatScale();
                int var8 = WMath.ceil((float)this.getChatWidth() / var7);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0f, 20.0f, 0.0f);
                GlStateManager.scale(var7, var7, 1.0f);
                int topY = -1;
                if (var3) {
                    WolframNewChat.drawRect(3, (-var2 + 1) * 11 - 11 + 4, var8 + 4, 7, var3 ? -804253680 : -1341124592);
                    topY = (-var2 + 1) * 11 - 11 + 4;
                }
                int var9 = 0;
                while (var9 + this.scrollPos < this.currentTab.lines.size() && var9 < var2) {
                    ChatLine var10 = (ChatLine)this.currentTab.lines.get(var9 + this.scrollPos);
                    if (var10 != null && ((var11 = p_146230_1_ - var10.getUpdatedCounter()) < 200 || var3)) {
                        double var12 = (double)var11 / 200.0;
                        var12 = 1.0 - var12;
                        var12 *= 10.0;
                        var12 = WMath.clamp(var12, 0.0, 1.0);
                        var12 *= var12;
                        var14 = (int)(255.0 * var12);
                        if (var3) {
                            var14 = 255;
                        }
                        var14 = (int)((float)var14 * var6);
                        ++var4;
                        if (var3 || var14 > 3) {
                            boolean var15 = false;
                            int var16 = -var9 * 11;
                            if (!var3) {
                                if (var9 == 0) {
                                    WolframNewChat.drawRect(3, var16 - 11 + 4, 0 + var8 + 4, var16 + 3 + 4, var3 ? -804253680 : -1341124592);
                                } else {
                                    WolframNewChat.drawRect(3, var16 - 11 + 4, 0 + var8 + 4, var16 + 4, var3 ? -804253680 : -1341124592);
                                }
                                topY = var16 - 11 + 4;
                            }
                            String var17 = var10.getChatComponent().getFormattedText();
                            GlStateManager.enableBlend();
                            this.mc.fontRendererObj.drawStringWithShadow(var17, 10.0f, var16 - 9 + 4, 0xFFFFFF + (var14 << 24));
                            GlStateManager.disableAlpha();
                            GlStateManager.disableBlend();
                        }
                    }
                    ++var9;
                }
                if (topY != -1) {
                    WolframNewChat.drawRect(3, topY - 3, var8 + 4, topY, var3 ? -804253680 : -1341124592);
                }
                if (var3) {
                    var9 = this.mc.fontRendererObj.FONT_HEIGHT;
                    GlStateManager.translate(-3.0f, 0.0f, 0.0f);
                    int var18 = var5 * var9 + var5;
                    var11 = var4 * var9 + var4;
                    int var19 = this.scrollPos * var11 / Math.max(var5, 1);
                    int var13 = var11 * var11 / Math.max(var18, 1);
                    if (var18 != var11) {
                        var14 = var19 > 0 ? 170 : 96;
                        int var20 = this.isScrolled ? 0xCC3333 : 0x3333AA;
                        WolframNewChat.drawRect(0, -var19, 2, -var19 - var13, var20 + (var14 << 24));
                        WolframNewChat.drawRect(2, -var19, 1, -var19 - var13, 0xCCCCCC + (var14 << 24));
                    }
                }
                GlStateManager.popMatrix();
                if (var3) {
                    topY += 3;
                    int i = this.chatTabs.size();
                    while (i > 0) {
                        int width = WMath.ceil((float)this.getChatWidth() / var7) + 1;
                        int tabCount = Math.min(3, i);
                        float tabWidth = (float)width / (float)tabCount;
                        int j = 0;
                        while (j < tabCount) {
                            WolframNewChat.drawRect(5 + (int)((float)(tabCount - j - 1) * tabWidth), topY - 2, tabCount - j - 1 == tabCount - 1 ? 5 + width : 4 + (int)((float)(tabCount - j) * tabWidth), topY + 13, -804253680);
                            this.drawString(this.mc.fontRendererObj, ((ChatTab)this.chatTabs.get((int)(i - j - 1))).name, 9 + (int)((float)(tabCount - j - 1) * tabWidth), topY + 1, this.chatTabs.get(i - j - 1) == this.currentTab ? GuiManager.getHexMainColor() : -1);
                            ++j;
                        }
                        topY -= 16;
                        i -= 3;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

