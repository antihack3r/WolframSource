/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.command.commands;

import net.wolframclient.Wolfram;
import net.wolframclient.command.Command;
import net.wolframclient.compatibility.WChat;

public final class ClearCommand
extends Command {
    public ClearCommand() {
        super("Clear", "Clears the chat");
    }

    @Override
    public void call(String[] args) {
        if (args.length != 0) {
            this.help();
            return;
        }
        WChat.clearMessages();
    }

    @Override
    public void help() {
        Wolfram.getWolfram().addChatMessage("Syntax: 'clear'");
    }
}

