/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.compatibility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.TextComponentString;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.module.modules.render.ChatTab;

public final class WChat {
    private static GuiNewChat getChatGui() {
        return WMinecraft.getChatGui();
    }

    public static void clearMessages() {
        WChat.getChatGui().clearChatMessages(true);
    }

    public static void setChatLineForTab(String message, ChatTab tab) {
        WChat.getChatGui().setChatLineForTab(new TextComponentString(message), 0, Minecraft.getMinecraft().ingameGUI.getUpdateCounter(), false, tab);
    }
}

