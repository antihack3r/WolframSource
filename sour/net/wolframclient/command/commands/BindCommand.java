/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package net.wolframclient.command.commands;

import java.util.Arrays;
import net.wolframclient.Wolfram;
import net.wolframclient.command.Command;
import net.wolframclient.keybind.Keybind;
import org.lwjgl.input.Keyboard;

public final class BindCommand
extends Command {
    public BindCommand() {
        super("Bind", "Bind commands to keys");
    }

    @Override
    public void call(String[] args) {
        if (args.length < 1) {
            this.help();
            return;
        }
        switch (args[0].toLowerCase()) {
            case "list": {
                this.listBinds();
                break;
            }
            case "remove": {
                this.removeBind(args);
                break;
            }
            default: {
                this.addBind(args);
            }
        }
    }

    private void addBind(String[] args) {
        if (args.length < 2) {
            this.help();
            return;
        }
        int keyCode = Keyboard.getKeyIndex((String)args[0].toUpperCase());
        if (keyCode == 0) {
            Wolfram.getWolfram().addChatMessage("Unknown key: " + args[0]);
            return;
        }
        String command = String.join((CharSequence)" ", Arrays.copyOfRange(args, 1, args.length));
        Wolfram.getWolfram().keybindManager.addKeybind(keyCode, command);
        Wolfram.getWolfram().keybindManager.saveKeybinds();
        Wolfram.getWolfram().addChatMessage("Added '" + Keyboard.getKeyName((int)keyCode) + " - " + command + "'");
    }

    private void removeBind(String[] args) {
        if (args.length < 2) {
            this.help();
            return;
        }
        int keyCode = Keyboard.getKeyIndex((String)args[1].toUpperCase());
        if (keyCode == 0) {
            Wolfram.getWolfram().addChatMessage("Unknown key: " + args[1]);
            return;
        }
        for (Keybind keybind : Wolfram.getWolfram().keybindManager.getList()) {
            if (keybind.getKey() != keyCode) continue;
            Wolfram.getWolfram().keybindManager.remove(keybind);
            Wolfram.getWolfram().keybindManager.saveKeybinds();
            Wolfram.getWolfram().addChatMessage("Removed '" + keybind.getKeyName() + " - " + keybind.getCommand() + "'");
            return;
        }
        Wolfram.getWolfram().addChatMessage("Could not find any keybind for '" + Keyboard.getKeyName((int)keyCode) + "'");
    }

    private void listBinds() {
        for (Keybind keybind : Wolfram.getWolfram().keybindManager.getList()) {
            Wolfram.getWolfram().addChatMessage(String.valueOf(keybind.getKeyName()) + " - " + keybind.getCommand());
        }
    }

    @Override
    public void help() {
        Wolfram.getWolfram().addChatMessage("Syntax: 'bind [key_name] [command]'");
        Wolfram.getWolfram().addChatMessage("Remove: 'bind remove [key_name]'");
        Wolfram.getWolfram().addChatMessage("List: 'bind list'");
    }
}

