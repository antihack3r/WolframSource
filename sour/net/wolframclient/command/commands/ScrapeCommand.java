/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.command.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.wolframclient.Wolfram;
import net.wolframclient.command.Command;
import net.wolframclient.compatibility.WMinecraft;

public final class ScrapeCommand
extends Command {
    private final Path saveFile = Wolfram.getSaveDirectory().toPath().resolve("usernames.txt");

    public ScrapeCommand() {
        super("Scrape", "Scrape usernames");
    }

    @Override
    public void call(String[] args) {
        if (args.length != 0) {
            this.help();
            return;
        }
        try {
            this.scrapeUsernames();
            Wolfram.getWolfram().addChatMessage("Saved usernames to: " + this.saveFile.toAbsolutePath());
        }
        catch (IOException e) {
            e.printStackTrace();
            Wolfram.getWolfram().addChatMessage("File could not be saved");
        }
    }

    @Override
    public void help() {
        Wolfram.getWolfram().addChatMessage("Syntax: 'scrape'");
    }

    private void scrapeUsernames() throws IOException {
        ArrayList<String> usernames = new ArrayList<String>();
        for (EntityPlayer player : WMinecraft.getWorld().playerEntities) {
            if (player == WMinecraft.getPlayer()) continue;
            usernames.add(player.getGameProfile().getName());
        }
        Files.write(this.saveFile, usernames, new OpenOption[0]);
    }
}

