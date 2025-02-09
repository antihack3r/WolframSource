/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.wolframclient.Wolfram;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.module.modules.render.ChatTab;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiNewChat
extends Gui {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final Minecraft mc;
    public List<String> sentMessages = Lists.newArrayList();
    public List<ChatLine> chatLines = Lists.newArrayList();
    protected int scrollPos;
    protected boolean isScrolled;
    public List<ChatTab> chatTabs = new ArrayList<ChatTab>();
    public ChatTab currentTab = null;
    public ChatTab mcChat = new ChatTab("Chat");
    public ChatTab commandChat = new ChatTab("Commands");
    private String lastMessage = "";
    private int count = 0;

    public GuiNewChat(Minecraft mcIn) {
        this.mc = mcIn;
        this.chatTabs.add(this.mcChat);
        this.currentTab = this.mcChat;
        this.chatTabs.add(this.commandChat);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void drawChat(int updateCounter) {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int i = this.getLineCount();
            int j = this.currentTab.lines.size();
            float f = this.mc.gameSettings.chatOpacity * 0.9f + 0.1f;
            if (j > 0) {
                boolean flag = false;
                if (this.getChatOpen()) {
                    flag = true;
                }
                float f1 = this.getChatScale();
                int k = MathHelper.ceil((float)this.getChatWidth() / f1);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0f, 8.0f, 0.0f);
                GlStateManager.scale(f1, f1, 1.0f);
                int l = 0;
                int i1 = 0;
                while (i1 + this.scrollPos < this.currentTab.lines.size() && i1 < i) {
                    int j1;
                    ChatLine chatline = (ChatLine)this.currentTab.lines.get(i1 + this.scrollPos);
                    if (chatline != null && ((j1 = updateCounter - chatline.getUpdatedCounter()) < 200 || flag)) {
                        double d0 = (double)j1 / 200.0;
                        d0 = 1.0 - d0;
                        d0 *= 10.0;
                        d0 = MathHelper.clamp(d0, 0.0, 1.0);
                        d0 *= d0;
                        int l1 = (int)(255.0 * d0);
                        if (flag) {
                            l1 = 255;
                        }
                        l1 = (int)((float)l1 * f);
                        ++l;
                        if (l1 > 3) {
                            boolean i2 = false;
                            int j2 = -i1 * 9;
                            GuiNewChat.drawRect(-2, j2 - 9, 0 + k + 4, j2, l1 / 2 << 24);
                            String s = chatline.getChatComponent().getFormattedText();
                            GlStateManager.enableBlend();
                            this.mc.fontRendererObj.drawStringWithShadow(s, 0.0f, j2 - 8, 0xFFFFFF + (l1 << 24));
                            GlStateManager.disableAlpha();
                            GlStateManager.disableBlend();
                        }
                    }
                    ++i1;
                }
                if (flag) {
                    int k2 = this.mc.fontRendererObj.FONT_HEIGHT;
                    GlStateManager.translate(-3.0f, 0.0f, 0.0f);
                    int l2 = j * k2 + j;
                    int i3 = l * k2 + l;
                    int j3 = this.scrollPos * i3 / j;
                    int k1 = i3 * i3 / l2;
                    if (l2 != i3) {
                        int k3 = j3 > 0 ? 170 : 96;
                        int l3 = this.isScrolled ? 0xCC3333 : 0x3333AA;
                        GuiNewChat.drawRect(0, -j3, 2, -j3 - k1, l3 + (k3 << 24));
                        GuiNewChat.drawRect(2, -j3, 1, -j3 - k1, 0xCCCCCC + (k3 << 24));
                    }
                }
                GlStateManager.popMatrix();
            }
        }
    }

    public void clearChatMessages(boolean p_146231_1_) {
        for (ChatTab ct : this.chatTabs) {
            ct.lines.clear();
        }
        this.currentTab.lines.clear();
        this.chatLines.clear();
        if (p_146231_1_) {
            this.sentMessages.clear();
        }
    }

    public void printChatMessage(ITextComponent chatComponent) {
        this.printChatMessageWithOptionalDeletion(chatComponent, 0);
    }

    public void printChatMessageWithOptionalDeletion(ITextComponent chatComponent, int chatLineId) {
        this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
        LOGGER.info("[CHAT] {}", (Object)chatComponent.getUnformattedText().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    private void setChatLine(ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        this.setChatLineForTab(chatComponent, chatLineId, updateCounter, displayOnly, this.mcChat);
    }

    public void setChatLineForTab(ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, ChatTab tab) {
        if (Wolfram.getWolfram().getClientSettings().getBoolean("chat_autoswitch")) {
            this.currentTab = tab;
        }
        if (chatComponent.getUnformattedText().equalsIgnoreCase(this.lastMessage) && Wolfram.getWolfram().moduleManager.isEnabled("antispam")) {
            ++this.count;
            this.chatLines.remove(0);
            int var5 = MathHelper.floor((float)this.getChatWidth() / this.getChatScale());
            List<ITextComponent> var6 = GuiUtilRenderComponents.splitText(chatComponent, var5, FontRenderers.MINECRAFT, false, false);
            int i = var6.size();
            while (i > 0) {
                tab.lines.remove(0);
                --i;
            }
            chatComponent.appendText(" [x" + this.count + "]");
        } else {
            this.count = 1;
            this.lastMessage = chatComponent.getUnformattedText();
        }
        if (chatLineId != 0) {
            this.deleteChatLine(chatLineId);
        }
        int i = MathHelper.floor((float)this.getChatWidth() / this.getChatScale());
        List<ITextComponent> list = GuiUtilRenderComponents.splitText(chatComponent, i, FontRenderers.MINECRAFT, false, false);
        boolean flag = this.getChatOpen();
        for (ITextComponent itextcomponent : list) {
            if (flag && this.scrollPos > 0) {
                this.isScrolled = true;
                this.scroll(1);
            }
            tab.lines.add(0, new ChatLine(updateCounter, itextcomponent, chatLineId));
        }
        while (tab.lines.size() > 100) {
            tab.lines.remove(tab.lines.size() - 1);
        }
        if (!displayOnly) {
            this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));
            while (this.chatLines.size() > 100) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    public void refreshChat() {
        for (ChatTab ct : this.chatTabs) {
            ct.lines.clear();
        }
        this.resetScroll();
        int i = this.chatLines.size() - 1;
        while (i >= 0) {
            ChatLine chatline = this.chatLines.get(i);
            this.setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
            --i;
        }
    }

    public List<String> getSentMessages() {
        return this.sentMessages;
    }

    public void addToSentMessages(String message) {
        if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(message)) {
            this.sentMessages.add(message);
        }
    }

    public void resetScroll() {
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    public void scroll(int amount) {
        this.scrollPos += amount;
        int i = this.currentTab.lines.size();
        if (this.scrollPos > i - this.getLineCount()) {
            this.scrollPos = i - this.getLineCount();
        }
        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }

    @Nullable
    public ITextComponent getChatComponent(int mouseX, int mouseY) {
        if (!this.getChatOpen()) {
            return null;
        }
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int i = scaledresolution.getScaleFactor();
        float f = this.getChatScale();
        int j = mouseX / i - 2;
        int k = mouseY / i - 40;
        j = MathHelper.floor((float)j / f);
        k = MathHelper.floor((float)k / f);
        if (j >= 0 && k >= 0) {
            int l = Math.min(this.getLineCount(), this.currentTab.lines.size());
            if (j <= MathHelper.floor((float)this.getChatWidth() / this.getChatScale()) && k < this.mc.fontRendererObj.FONT_HEIGHT * l + l) {
                int i1 = k / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;
                if (i1 >= 0 && i1 < this.currentTab.lines.size()) {
                    ChatLine chatline = (ChatLine)this.currentTab.lines.get(i1);
                    int j1 = 0;
                    for (ITextComponent itextcomponent : chatline.getChatComponent()) {
                        if (!(itextcomponent instanceof TextComponentString) || (j1 += this.mc.fontRendererObj.getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured(((TextComponentString)itextcomponent).getText(), false))) <= j) continue;
                        return itextcomponent;
                    }
                }
                return null;
            }
            return null;
        }
        return null;
    }

    public boolean getChatOpen() {
        return this.mc.currentScreen instanceof GuiChat;
    }

    public void deleteChatLine(int id) {
        Iterator<Object> iterator = this.currentTab.lines.iterator();
        while (iterator.hasNext()) {
            ChatLine chatline = (ChatLine)iterator.next();
            if (chatline.getChatLineID() != id) continue;
            iterator.remove();
        }
        iterator = this.chatLines.iterator();
        while (iterator.hasNext()) {
            ChatLine chatline1 = (ChatLine)iterator.next();
            if (chatline1.getChatLineID() != id) continue;
            iterator.remove();
            break;
        }
    }

    public int getChatWidth() {
        return GuiNewChat.calculateChatboxWidth(this.mc.gameSettings.chatWidth);
    }

    public int getChatHeight() {
        return GuiNewChat.calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }

    public float getChatScale() {
        return this.mc.gameSettings.chatScale;
    }

    public static int calculateChatboxWidth(float scale) {
        int i = 320;
        int j = 40;
        return MathHelper.floor(scale * 280.0f + 40.0f);
    }

    public static int calculateChatboxHeight(float scale) {
        int i = 180;
        int j = 20;
        return MathHelper.floor(scale * 160.0f + 20.0f);
    }

    public int getLineCount() {
        return this.getChatHeight() / 9;
    }
}

