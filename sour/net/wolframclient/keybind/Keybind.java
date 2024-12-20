/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package net.wolframclient.keybind;

import org.lwjgl.input.Keyboard;

public final class Keybind {
    private final int key;
    private final String command;

    public Keybind(int key, String command) {
        this.key = key;
        this.command = command;
    }

    public int getKey() {
        return this.key;
    }

    public String getKeyName() {
        return Keyboard.getKeyName((int)this.key);
    }

    public String getCommand() {
        return this.command;
    }
}

