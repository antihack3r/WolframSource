/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.wolframclient.Wolfram;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.gui.screen.chat.WolframNewChat;

public class WolframChat
implements Listener {
    private boolean lastState = false;

    @EventTarget
    public void onUpdate(PlayerUpdateEvent e) {
        boolean enabled = Wolfram.getWolfram().getClientSettings().getBoolean("wolfram_chat");
        if (this.lastState != enabled) {
            if (enabled) {
                this.onEnable();
            } else {
                this.onDisable();
            }
        }
        this.lastState = enabled;
    }

    public void onEnable() {
        GuiNewChat oldChat = Minecraft.getMinecraft().ingameGUI.persistantChatGUI;
        WolframNewChat newChat = new WolframNewChat(Minecraft.getMinecraft());
        newChat.chatLines = oldChat.chatLines;
        newChat.sentMessages = oldChat.sentMessages;
        newChat.mcChat = oldChat.mcChat;
        Minecraft.getMinecraft().ingameGUI.persistantChatGUI = newChat;
    }

    public void onDisable() {
        GuiNewChat oldChat = Minecraft.getMinecraft().ingameGUI.persistantChatGUI;
        GuiNewChat newChat = new GuiNewChat(Minecraft.getMinecraft());
        newChat.chatLines = oldChat.chatLines;
        newChat.sentMessages = oldChat.sentMessages;
        newChat.mcChat = oldChat.mcChat;
        Minecraft.getMinecraft().ingameGUI.persistantChatGUI = newChat;
    }
}

