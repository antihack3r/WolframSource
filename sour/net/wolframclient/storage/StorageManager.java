/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.storage;

import java.util.Arrays;
import net.wolframclient.Wolfram;
import net.wolframclient.storage.ArrayStorage;
import net.wolframclient.storage.MapStorage;
import net.wolframclient.storage.Storage;

public final class StorageManager {
    public final MapStorage clientSettings = new MapStorage(Wolfram.getSaveFile("preferences"), "Client Settings");
    public final MapStorage moduleStates = new MapStorage(Wolfram.getSaveFile("moduleStates"), "Module States");
    public final MapStorage moduleSettings = new MapStorage(Wolfram.getSaveFile("moduleSettings"), "Module Settings");
    public final MapStorage altAccounts = new MapStorage(Wolfram.getSaveFile("accounts"), "Alt Accounts");
    public final MapStorage keyBinds = new MapStorage(Wolfram.getSaveFile("keybinds"), "Key Bindings");
    public final MapStorage nameProtect = new MapStorage(Wolfram.getSaveFile("nameprotect"), "Name Protect");
    public final MapStorage wolframAccount = new MapStorage(Wolfram.getSaveFile("wolframAccount"), "Wolfram Account");
    public final ArrayStorage friends = new ArrayStorage(Wolfram.getSaveFile("friends"));
    public final ArrayStorage enemies = new ArrayStorage(Wolfram.getSaveFile("enemies"));
    public final ArrayStorage xrayBlocks = new ArrayStorage(Wolfram.getSaveFile("xray")){

        @Override
        protected void loadDefaults() {
            this.addAll(Arrays.asList("8", "9", "10", "11", "14", "15", "16", "21", "22", "41", "42", "46", "48", "52", "56", "57", "58", "61", "62", "89", "90", "116", "117", "120", "129", "130", "133", "137", "152", "153", "154", "158", "173", "49", "73", "74", "210", "211", "54", "146"));
        }
    };
    public final ArrayStorage chatSpamMessages = new ArrayStorage(Wolfram.getSaveFile("chatSpamMessages"));

    public void loadAll() {
        this.clientSettings.load();
        this.moduleStates.load();
        this.moduleSettings.load();
        this.altAccounts.load();
        this.keyBinds.load();
        this.nameProtect.load();
        this.wolframAccount.load();
        this.friends.load();
        this.enemies.load();
        this.xrayBlocks.load();
        this.chatSpamMessages.load();
    }

    public void saveAll() {
        this.clientSettings.save();
        this.moduleStates.save();
        this.moduleSettings.save();
        this.altAccounts.save();
        this.keyBinds.save();
        this.nameProtect.save();
        this.wolframAccount.save();
        this.friends.save();
        this.enemies.save();
        this.xrayBlocks.save();
        this.chatSpamMessages.save();
        Wolfram.getWolfram().guiManager.gui.save();
    }

    public Storage getByName(String name) {
        switch (name) {
            case "Client Settings": {
                return this.clientSettings;
            }
            case "Module States": {
                return this.moduleStates;
            }
            case "Module Settings": {
                return this.moduleSettings;
            }
            case "Alt Accounts": {
                return this.altAccounts;
            }
            case "Key Bindings": {
                return this.keyBinds;
            }
            case "Name Protect": {
                return this.nameProtect;
            }
            case "Wolfram Account": {
                return this.wolframAccount;
            }
            case "Friends": {
                return this.friends;
            }
            case "Enemies": {
                return this.enemies;
            }
            case "XRay Blocks": {
                return this.xrayBlocks;
            }
            case "Chat Spam Messages": {
                return this.chatSpamMessages;
            }
        }
        return null;
    }
}

