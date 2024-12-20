/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.command.commands;

import net.wolframclient.Wolfram;
import net.wolframclient.command.Command;

public final class NameprotectCommand
extends Command {
    public NameprotectCommand() {
        super("NameProtect", "Hide your name in the chat");
    }

    @Override
    public void call(String[] args) {
        if (args.length != 2) {
            this.help();
            return;
        }
        if (args[0].equalsIgnoreCase("remove")) {
            Wolfram.getWolfram().nameprotectManager.removeName(args[1]);
        } else {
            Wolfram.getWolfram().nameprotectManager.setName(args[0], args[1]);
        }
    }

    @Override
    public void help() {
        Wolfram.getWolfram().addChatMessage("Syntax:");
        Wolfram.getWolfram().addChatMessage("Add name: 'nameprotect [name] [alias]'");
        Wolfram.getWolfram().addChatMessage("Remove name: 'nameprotect remove [name]'");
    }
}

