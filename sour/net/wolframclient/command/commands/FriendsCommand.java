/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.command.commands;

import net.minecraft.entity.player.EntityPlayer;
import net.wolframclient.Wolfram;
import net.wolframclient.command.Command;
import net.wolframclient.compatibility.WMinecraft;

public final class FriendsCommand
extends Command {
    public FriendsCommand() {
        super("Friends", "Manage friends");
    }

    @Override
    public void call(String[] args) {
        if (args.length < 1 || args.length > 2) {
            this.help();
            return;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                Wolfram.getWolfram().relationManager.listFriends();
            } else if (args[0].equalsIgnoreCase("clear")) {
                Wolfram.getWolfram().relationManager.clearFriends();
            } else {
                this.help();
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                Wolfram.getWolfram().relationManager.addFriend(args[1]);
            } else if (args[0].equalsIgnoreCase("radius")) {
                this.addFriendsInRange(args);
            } else if (args[0].equalsIgnoreCase("remove")) {
                Wolfram.getWolfram().relationManager.removeFriend(args[1]);
            } else {
                this.help();
            }
        }
    }

    private void addFriendsInRange(String[] args) {
        try {
            int radius = Integer.parseInt(args[1]);
            for (EntityPlayer player : WMinecraft.getWorld().playerEntities) {
                if (player == WMinecraft.getPlayer() || WMinecraft.getPlayer().getDistanceToEntity(player) > (float)radius) continue;
                Wolfram.getWolfram().relationManager.addFriend(player.getGameProfile().getName().toLowerCase());
            }
        }
        catch (NumberFormatException e) {
            Wolfram.getWolfram().addChatMessage("Not a number: " + args[1]);
        }
    }

    @Override
    public void help() {
        Wolfram.getWolfram().addChatMessage("Syntax:");
        Wolfram.getWolfram().addChatMessage("Add friend: 'friends add [friend_name]'");
        Wolfram.getWolfram().addChatMessage("Add all players in a certain radius: 'friends radius [distance]'");
        Wolfram.getWolfram().addChatMessage("Remove friend: 'friends remove [friend_name]'");
        Wolfram.getWolfram().addChatMessage("Remove all friends: 'friends clear'");
        Wolfram.getWolfram().addChatMessage("List friends: 'friends list'");
    }
}

