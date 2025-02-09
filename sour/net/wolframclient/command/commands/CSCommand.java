/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.command.commands;

import java.util.Arrays;
import java.util.Iterator;
import net.wolframclient.Wolfram;
import net.wolframclient.command.Command;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.RenderEvent;
import net.wolframclient.utils.MillisTimer;

public final class CSCommand
extends Command
implements Listener {
    private boolean running = false;
    private MillisTimer timer = new MillisTimer();
    private int index;

    public CSCommand() {
        super("CS", "Spam the chat");
        registry.registerListener(this);
    }

    @Override
    public void call(String[] args) {
        if (args.length == 0) {
            this.help();
            return;
        }
        switch (args[0].toLowerCase()) {
            case "add": {
                this.addMessage(args);
                break;
            }
            case "remove": {
                this.removeMessage(args);
                break;
            }
            case "list": {
                this.listMessages();
                break;
            }
            case "delay": {
                this.setDelay(args);
                break;
            }
            case "run": {
                this.running = true;
                Wolfram.getWolfram().addChatMessage("Started sending messages");
                break;
            }
            case "stop": {
                this.running = false;
                Wolfram.getWolfram().addChatMessage("Stopped sending messages");
                break;
            }
            default: {
                this.help();
            }
        }
    }

    private void setDelay(String[] args) {
        if (args.length < 2) {
            this.help();
            return;
        }
        try {
            int delay = Integer.parseInt(args[1]);
            Wolfram.getWolfram().storageManager.moduleSettings.set("cs_delay", delay);
            Wolfram.getWolfram().addChatMessage("Set the delay to " + delay + " milliseconds");
        }
        catch (NumberFormatException e) {
            Wolfram.getWolfram().addChatMessage("Not a number: " + args[1]);
        }
    }

    private void listMessages() {
        Wolfram.getWolfram().addChatMessage("Chat spam messages:");
        int i = 0;
        while (i < Wolfram.getWolfram().storageManager.chatSpamMessages.size()) {
            Wolfram.getWolfram().addChatMessage(String.valueOf(i + 1) + ": " + (String)Wolfram.getWolfram().storageManager.chatSpamMessages.get(i));
            ++i;
        }
    }

    private void addMessage(String[] args) {
        if (args.length < 2) {
            this.help();
            return;
        }
        String message = String.join((CharSequence)" ", Arrays.copyOfRange(args, 1, args.length));
        Wolfram.getWolfram().storageManager.chatSpamMessages.add(message);
        Wolfram.getWolfram().addChatMessage("Added chat spam message: " + message);
    }

    private void removeMessage(String[] args) {
        if (args.length < 2) {
            this.help();
            return;
        }
        String partOfMsg = String.join((CharSequence)" ", Arrays.copyOfRange(args, 1, args.length)).toLowerCase();
        Iterator itr = Wolfram.getWolfram().storageManager.chatSpamMessages.iterator();
        while (itr.hasNext()) {
            String msg = (String)itr.next();
            if (!msg.toLowerCase().contains(partOfMsg)) continue;
            itr.remove();
            Wolfram.getWolfram().addChatMessage("Removed chat spam message: " + msg);
        }
    }

    @Override
    public void help() {
        Wolfram.getWolfram().addChatMessage("Syntax:");
        Wolfram.getWolfram().addChatMessage("Add message: 'cs add [message]'");
        Wolfram.getWolfram().addChatMessage("Remove message: 'cs remove [part_of_message]'");
        Wolfram.getWolfram().addChatMessage("List messages: 'cs list'");
        Wolfram.getWolfram().addChatMessage("Set message delay: 'cs delay [millis]'");
        Wolfram.getWolfram().addChatMessage("Start spamming: 'cs run'");
        Wolfram.getWolfram().addChatMessage("Stop spamming: 'cs stop'");
    }

    @EventTarget
    public void onUpdate(RenderEvent event) {
        if (!this.running) {
            return;
        }
        if (!this.timer.check(Wolfram.getWolfram().storageManager.moduleSettings.getInt("cs_delay"))) {
            return;
        }
        if (this.index >= Wolfram.getWolfram().storageManager.chatSpamMessages.size()) {
            this.index = 0;
        }
        String msg = (String)Wolfram.getWolfram().storageManager.chatSpamMessages.get(this.index);
        WMinecraft.getPlayer().sendChatMessageBypass(msg);
        this.timer.reset();
        ++this.index;
    }
}

