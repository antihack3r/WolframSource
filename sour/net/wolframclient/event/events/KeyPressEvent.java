/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.event.events;

import net.wolframclient.event.Event;

public final class KeyPressEvent
implements Event {
    private final int keyCode;

    public KeyPressEvent(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return this.keyCode;
    }
}

