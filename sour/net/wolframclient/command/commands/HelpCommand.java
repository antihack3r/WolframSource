/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package net.wolframclient.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.wolframclient.Wolfram;
import net.wolframclient.command.Command;

public final class HelpCommand
extends Command {
    public HelpCommand() {
        super("Help", "Show a list of commands");
    }

    @Override
    public void call(String[] args) {
        switch (args.length) {
            case 0: {
                Wolfram.getWolfram().addChatMessage(ChatFormatting.AQUA + "Help");
                for (Command command : Wolfram.getWolfram().commandManager.getList()) {
                    Wolfram.getWolfram().addChatMessage(String.valueOf(command.getName()) + " - " + command.getDescription());
                }
                Wolfram.getWolfram().addChatMessage("[module_name] - Toggle a module");
                break;
            }
            case 1: {
                for (Command command : Wolfram.getWolfram().commandManager.getList()) {
                    if (!command.getName().equalsIgnoreCase(args[0])) continue;
                    command.help();
                    return;
                }
                this.help();
                break;
            }
            default: {
                this.help();
            }
        }
    }

    @Override
    public void help() {
        Wolfram.getWolfram().addChatMessage("Syntax:");
        Wolfram.getWolfram().addChatMessage("Display help: 'help'");
        Wolfram.getWolfram().addChatMessage("Display a specific command's help section: 'help [command]'");
    }
}

