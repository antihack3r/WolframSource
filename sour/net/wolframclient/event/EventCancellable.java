/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.event;

import net.wolframclient.event.Event;

public abstract class EventCancellable
implements Event {
    private boolean cancelled;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

