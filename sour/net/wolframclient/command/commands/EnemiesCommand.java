/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.command.commands;

import net.wolframclient.Wolfram;
import net.wolframclient.command.Command;

public final class EnemiesCommand
extends Command {
    public EnemiesCommand() {
        super("Enemies", "Manage enemies");
    }

    @Override
    public void call(String[] args) {
        if (args.length < 1 || args.length > 2) {
            this.help();
            return;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                Wolfram.getWolfram().relationManager.listEnemies();
            } else if (args[0].equalsIgnoreCase("clear")) {
                Wolfram.getWolfram().relationManager.clearEnemies();
            } else {
                this.help();
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                Wolfram.getWolfram().relationManager.addEnemy(args[1]);
            } else if (args[0].equalsIgnoreCase("remove")) {
                Wolfram.getWolfram().relationManager.removeEnemy(args[1]);
            } else {
                this.help();
            }
        }
    }

    @Override
    public void help() {
        Wolfram.getWolfram().addChatMessage("Syntax:");
        Wolfram.getWolfram().addChatMessage("Add enemy: 'enemies add [enemy_name]'");
        Wolfram.getWolfram().addChatMessage("Remove enemy: 'enemies remove [enemy_name]'");
        Wolfram.getWolfram().addChatMessage("Remove all enemies: 'enemies clear'");
        Wolfram.getWolfram().addChatMessage("List enemies: 'enemies list'");
    }
}

