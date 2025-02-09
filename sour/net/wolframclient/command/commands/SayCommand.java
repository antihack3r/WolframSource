/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.command.commands;

import net.wolframclient.Wolfram;
import net.wolframclient.command.Command;
import net.wolframclient.compatibility.WMinecraft;

public final class SayCommand
extends Command {
    public SayCommand() {
        super("Say", "Send messages ignoring Commands");
    }

    @Override
    public void call(String[] args) {
        if (args.length == 0) {
            this.help();
            return;
        }
        WMinecraft.getPlayer().sendChatMessageBypass(String.join((CharSequence)" ", args));
    }

    @Override
    public void help() {
        Wolfram.getWolfram().addChatMessage("Syntax: 'say [message]'");
    }
}

