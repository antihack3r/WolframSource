/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.command.commands;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wolframclient.Wolfram;
import net.wolframclient.command.Command;
import net.wolframclient.compatibility.WConnection;
import net.wolframclient.compatibility.WMinecraft;

public final class DamageCommand
extends Command {
    public DamageCommand() {
        super("Damage", "Damage yourself");
    }

    @Override
    public void call(String[] args) {
        if (args.length == 0) {
            DamageCommand.damage(1);
        } else if (args.length == 1) {
            try {
                DamageCommand.damage(Integer.parseInt(args[0]));
            }
            catch (NumberFormatException e) {
                Wolfram.getWolfram().addChatMessage("Not a number: " + args[0]);
            }
        } else {
            this.help();
        }
    }

    @Override
    public void help() {
        Wolfram.getWolfram().addChatMessage("Syntax: 'damage [amount]'");
    }

    public static void damage(int damage) {
        boolean bypass = true;
        EntityPlayerSP player = WMinecraft.getPlayer();
        if (player == null) {
            return;
        }
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;
        if (bypass) {
            double dist = 0.0;
            while (dist < (double)(damage + 2)) {
                WConnection.sendPacket(new CPacketPlayer.Position(x, y + 1.25, z, false));
                WConnection.sendPacket(new CPacketPlayer.Position(x, y, z, false));
                dist += 1.25;
            }
        } else {
            WConnection.sendPacket(new CPacketPlayer.Position(x, y + 0.1, z, false));
            WConnection.sendPacket(new CPacketPlayer.Position(x, y - (double)damage - 3.0, z, false));
        }
    }
}

