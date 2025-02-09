/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.command.commands;

import net.minecraft.entity.player.EntityPlayer;
import net.wolframclient.Wolfram;
import net.wolframclient.command.Command;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.module.modules.world.Pathfinder;

public final class PathfinderCommand
extends Command {
    public PathfinderCommand() {
        super("Path", "Manage the pathfinder");
    }

    @Override
    public void call(String[] args) {
        block26: {
            block24: {
                if (args.length < 1 || args.length > 3) {
                    this.help();
                    return;
                }
                if (args.length != 1) break block24;
                switch (args[0].toLowerCase()) {
                    case "set": {
                        Pathfinder.getInstance().setDestination(WMinecraft.getPlayer().posX, WMinecraft.getPlayer().posY, WMinecraft.getPlayer().posZ);
                        Pathfinder.getInstance().refresh();
                        break;
                    }
                    case "clear": {
                        Pathfinder.getInstance().clear();
                        break;
                    }
                    case "refresh": {
                        Pathfinder.getInstance().refresh();
                        break;
                    }
                    default: {
                        for (EntityPlayer player : WMinecraft.getWorld().playerEntities) {
                            if (!player.getGameProfile().getName().equalsIgnoreCase(args[0])) continue;
                            Pathfinder.getInstance().setDestination(player.posX, player.posY, player.posZ);
                            Pathfinder.getInstance().refresh();
                        }
                        break block26;
                    }
                }
                break block26;
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("refresh")) {
                    try {
                        int delay = Integer.parseInt(args[1]);
                        Wolfram.getWolfram().storageManager.moduleSettings.set("pathfinder_delay", delay);
                        Wolfram.getWolfram().addChatMessage("Set the refresh delay to " + delay + " milliseconds");
                    }
                    catch (NumberFormatException e) {
                        Wolfram.getWolfram().addChatMessage("Not a number: " + args[1]);
                    }
                } else {
                    this.help();
                }
            } else if (args.length == 3) {
                try {
                    int x = Integer.parseInt(args[0]);
                    int y = Integer.parseInt(args[1]);
                    int z = Integer.parseInt(args[2]);
                    Pathfinder.getInstance().setDestination(x, y, z);
                    Pathfinder.getInstance().refresh();
                }
                catch (NumberFormatException e) {
                    Wolfram.getWolfram().addChatMessage("Invalid position: " + String.join((CharSequence)" ", args));
                }
            }
        }
    }

    @Override
    public void help() {
        Wolfram.getWolfram().addChatMessage("Syntax:");
        Wolfram.getWolfram().addChatMessage("Set destination to your current location: 'path set'");
        Wolfram.getWolfram().addChatMessage("Clear path: 'path clear'");
        Wolfram.getWolfram().addChatMessage("Refresh path: 'path refresh'");
        Wolfram.getWolfram().addChatMessage("Set destination: 'path [x] [y] [z]'");
        Wolfram.getWolfram().addChatMessage("Set refresh rate (seconds): 'path refresh [delay]'");
        Wolfram.getWolfram().addChatMessage("Set destination to player: 'path [player_name]'");
    }
}

