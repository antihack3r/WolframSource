/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.command.commands;

import net.wolframclient.Wolfram;
import net.wolframclient.command.Command;
import net.wolframclient.compatibility.WMinecraft;

public final class HClipCommand
extends Command {
    public HClipCommand() {
        super("HClip", "Teleport x blocks forward or backwards.");
    }

    @Override
    public void call(String[] args) {
        if (args.length != 1) {
            this.help();
            return;
        }
        try {
            int distance = Integer.parseInt(args[0]);
            double yaw = Math.toRadians(-WMinecraft.getPlayer().rotationYaw);
            WMinecraft.getPlayer().setPosition(WMinecraft.getPlayer().posX + (double)distance * Math.sin(yaw), WMinecraft.getPlayer().posY, WMinecraft.getPlayer().posZ + (double)distance * Math.cos(yaw));
        }
        catch (NumberFormatException e) {
            Wolfram.getWolfram().addChatMessage("Not a number: " + args[0]);
        }
    }

    @Override
    public void help() {
        Wolfram.getWolfram().addChatMessage("Syntax: 'hclip [distance]'");
    }
}

