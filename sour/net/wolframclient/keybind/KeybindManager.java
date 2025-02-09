/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.keybind;

import net.wolframclient.Wolfram;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.KeyPressEvent;
import net.wolframclient.keybind.Keybind;
import net.wolframclient.utils.Manager;

public final class KeybindManager
extends Manager<Keybind>
implements Listener {
    public KeybindManager() {
        registry.registerListener(this);
    }

    @EventTarget
    public void onKeyPress(KeyPressEvent event) {
        for (Keybind keybind : this.getList()) {
            if (keybind.getKey() != event.getKeyCode()) continue;
            String[] stringArray = keybind.getCommand().split("&&");
            int n = stringArray.length;
            int n2 = 0;
            while (n2 < n) {
                String command = stringArray[n2];
                Wolfram.getWolfram().commandManager.runCommand(command);
                ++n2;
            }
        }
    }

    public void addKeybind(int key, String command) {
        for (Keybind keybind : this.getList()) {
            if (keybind.getCommand() != command) continue;
            this.remove(keybind);
        }
        this.add(new Keybind(key, command));
        this.saveKeybinds();
    }

    @Override
    public void remove(Keybind keybind) {
        super.remove(keybind);
        Wolfram.getWolfram().storageManager.keyBinds.remove(keybind.getCommand());
    }

    public void loadKeybinds() {
        try {
            if (Wolfram.getWolfram().getClientSettings().getBoolean("newKeybinds")) {
                for (String command : Wolfram.getWolfram().storageManager.keyBinds.keySet()) {
                    this.add(new Keybind(Wolfram.getWolfram().storageManager.keyBinds.getInt(command), command));
                }
            } else {
                for (String bind : Wolfram.getWolfram().storageManager.keyBinds.keySet()) {
                    this.add(new Keybind(Integer.parseInt(bind), (String)Wolfram.getWolfram().storageManager.keyBinds.get(bind)));
                }
                Wolfram.getWolfram().storageManager.keyBinds.clear();
                this.saveKeybinds();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Wolfram.getWolfram().storageManager.keyBinds.clear();
        }
    }

    public void saveKeybinds() {
        for (Keybind bind : this.getList()) {
            Wolfram.getWolfram().storageManager.keyBinds.set(bind.getCommand(), bind.getKey());
        }
        Wolfram.getWolfram().storageManager.clientSettings.set("newKeybinds", true);
    }
}

