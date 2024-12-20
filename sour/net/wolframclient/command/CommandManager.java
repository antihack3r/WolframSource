/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.command;

import java.util.Arrays;
import net.wolframclient.Wolfram;
import net.wolframclient.command.Command;
import net.wolframclient.command.commands.BindCommand;
import net.wolframclient.command.commands.CSCommand;
import net.wolframclient.command.commands.ClearCommand;
import net.wolframclient.command.commands.DamageCommand;
import net.wolframclient.command.commands.EnemiesCommand;
import net.wolframclient.command.commands.FriendsCommand;
import net.wolframclient.command.commands.HClipCommand;
import net.wolframclient.command.commands.HelpCommand;
import net.wolframclient.command.commands.MMCommand;
import net.wolframclient.command.commands.NameprotectCommand;
import net.wolframclient.command.commands.PanicCommand;
import net.wolframclient.command.commands.PathfinderCommand;
import net.wolframclient.command.commands.SayCommand;
import net.wolframclient.command.commands.ScrapeCommand;
import net.wolframclient.command.commands.VClipCommand;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.SendChatMessageEvent;
import net.wolframclient.module.Module;
import net.wolframclient.utils.Manager;

public final class CommandManager
extends Manager<Command>
implements Listener {
    private String prefix = "";

    public CommandManager() {
        registry.registerListener(this);
    }

    public void loadCommands() {
        this.add(new BindCommand());
        this.add(new ClearCommand());
        this.add(new CSCommand());
        this.add(new DamageCommand());
        this.add(new FriendsCommand());
        this.add(new EnemiesCommand());
        this.add(new HClipCommand());
        this.add(new HelpCommand());
        this.add(new MMCommand());
        this.add(new NameprotectCommand());
        this.add(new PanicCommand());
        this.add(new PathfinderCommand());
        this.add(new SayCommand());
        this.add(new ScrapeCommand());
        this.add(new VClipCommand());
    }

    @EventTarget
    public void onMessageSend(SendChatMessageEvent event) {
        if (WMinecraft.getChatGui().currentTab != WMinecraft.getChatGui().commandChat) {
            return;
        }
        event.setCancelled(true);
        String[] stringArray = event.getMessage().split("&&");
        int n = stringArray.length;
        int n2 = 0;
        while (n2 < n) {
            String command = stringArray[n2];
            this.runCommand(command);
            ++n2;
        }
    }

    public void runCommand(String input) {
        input = input.trim().replaceAll("  +", " ");
        Wolfram.getWolfram().addChatMessage("$ " + input);
        String[] parts = input.split(" ");
        if (parts.length == 0) {
            return;
        }
        Module module = Wolfram.getWolfram().moduleManager.getModule(parts[0]);
        if (module != null) {
            module.toggleModule();
            return;
        }
        for (Command command : this.getList()) {
            if (!command.getName().equalsIgnoreCase(parts[0])) continue;
            command.call(Arrays.copyOfRange(parts, 1, parts.length));
            return;
        }
        Wolfram.getWolfram().addChatMessage("Unknown command: " + input);
        Wolfram.getWolfram().addChatMessage("Type " + this.prefix + "help for a list of available commands.");
    }
}

