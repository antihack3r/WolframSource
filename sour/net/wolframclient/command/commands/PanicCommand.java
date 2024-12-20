/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.command.commands;

import net.wolframclient.Wolfram;
import net.wolframclient.command.Command;

public final class PanicCommand
extends Command {
    public PanicCommand() {
        super("Panic", "Toggle all mods off.");
    }

    @Override
    public void call(String[] args) {
        if (args.length != 0) {
            this.help();
            return;
        }
        Wolfram.getWolfram().moduleManager.disableModules();
    }

    @Override
    public void help() {
        Wolfram.getWolfram().addChatMessage("Syntax: 'panic'");
    }
}

