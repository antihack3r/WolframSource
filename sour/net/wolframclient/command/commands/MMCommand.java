/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package net.wolframclient.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.wolframclient.Wolfram;
import net.wolframclient.command.Command;
import net.wolframclient.compatibility.WMinecraft;

public final class MMCommand
extends Command {
    boolean running = false;

    public MMCommand() {
        super("MM", "Send mass messages");
    }

    @Override
    public void call(String[] args) {
        if (args.length == 0) {
            this.help();
            return;
        }
        switch (String.valueOf(args.length) + args[0].toLowerCase()) {
            case "1run": {
                this.sendMessage();
                Wolfram.getWolfram().addChatMessage("Started sending messages");
                break;
            }
            case "1stop": {
                this.running = false;
                Wolfram.getWolfram().addChatMessage("Stopped sending messages");
                break;
            }
            case "2delay": {
                try {
                    int delay = Integer.parseInt(args[1]);
                    Wolfram.getWolfram().storageManager.moduleSettings.set("mm_delay", delay);
                    Wolfram.getWolfram().addChatMessage("Delay set to " + delay + "ms");
                }
                catch (NumberFormatException e) {
                    Wolfram.getWolfram().addChatMessage("Not a number: " + args[1]);
                }
                break;
            }
            default: {
                this.setMessage(args);
            }
        }
    }

    @Override
    public void help() {
        Wolfram.getWolfram().addChatMessage("Syntax:");
        Wolfram.getWolfram().addChatMessage("Set message: 'mm [message]'");
        Wolfram.getWolfram().addChatMessage(ChatFormatting.GRAY + "Use '%u%' instead of the player's name");
        Wolfram.getWolfram().addChatMessage(ChatFormatting.GRAY + "Example: 'mm /tpa %u%' - sends a TPA request to all players");
        Wolfram.getWolfram().addChatMessage("Set message delay: 'mm delay [millis]'");
        Wolfram.getWolfram().addChatMessage("Start sending messages: 'mm run'");
        Wolfram.getWolfram().addChatMessage("Stop sending messages: 'mm stop'");
    }

    private void setMessage(String[] args) {
        String msg = String.join((CharSequence)" ", args);
        if (!msg.contains("%u%")) {
            Wolfram.getWolfram().addChatMessage(ChatFormatting.RED + "Error: Message does not contain '%u%'");
            this.help();
            return;
        }
        Wolfram.getWolfram().storageManager.moduleSettings.set("mm_command", msg);
        Wolfram.getWolfram().addChatMessage("Set message to: " + msg);
    }

    private void sendMessage() {
        String msg = (String)Wolfram.getWolfram().storageManager.moduleSettings.get("mm_command");
        int delay = Wolfram.getWolfram().storageManager.moduleSettings.getInt("mm_delay");
        if (msg != null) {
            this.running = true;
        }
        new Thread(() -> {
            long lastTime = 0L;
            long newTime = 0L;
            for (NetworkPlayerInfo info : WMinecraft.getConnection().getPlayerInfoMap()) {
                newTime = System.currentTimeMillis();
                while (newTime - lastTime < (long)delay) {
                    newTime = System.currentTimeMillis();
                }
                if (!this.running) {
                    return;
                }
                String name = info.getGameProfile().getName();
                if (name.equals(WMinecraft.getPlayer().getGameProfile().getName())) continue;
                WMinecraft.getPlayer().sendChatMessageBypass(msg.replaceAll("%u%", name));
                lastTime = newTime;
            }
        }).start();
    }
}

