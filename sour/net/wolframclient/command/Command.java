/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.command;

public abstract class Command {
    private final String name;
    private final String description;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public final String getName() {
        return this.name;
    }

    public final String getDescription() {
        return this.description;
    }

    public abstract void call(String[] var1);

    public abstract void help();
}

