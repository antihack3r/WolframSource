/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.event.events;

import net.wolframclient.event.Event;

public final class RenderEvent
implements Event {
    private final float partialTicks;

    public RenderEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}

