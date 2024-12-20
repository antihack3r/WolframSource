/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.command.commands;

import net.wolframclient.Wolfram;
import net.wolframclient.command.Command;
import net.wolframclient.compatibility.WMinecraft;

public final class VClipCommand
extends Command {
    public VClipCommand() {
        super("VClip", "Teleport x blocks up or down.");
    }

    @Override
    public void call(String[] args) {
        if (args.length != 1) {
            this.help();
            return;
        }
        try {
            int height = Integer.parseInt(args[0]);
            WMinecraft.getPlayer().setPosition(WMinecraft.getPlayer().posX, WMinecraft.getPlayer().posY + (double)height, WMinecraft.getPlayer().posZ);
        }
        catch (NumberFormatException e) {
            Wolfram.getWolfram().addChatMessage("Not a number: " + args[0]);
        }
    }

    @Override
    public void help() {
        Wolfram.getWolfram().addChatMessage("Syntax: 'vclip [height]'");
    }
}

