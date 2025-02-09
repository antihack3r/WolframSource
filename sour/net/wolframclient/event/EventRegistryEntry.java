/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.event;

import java.lang.reflect.Method;
import net.wolframclient.event.Listener;

public final class EventRegistryEntry {
    private final Listener listener;
    private final Method method;
    private final byte priority;

    public EventRegistryEntry(Listener listener, Method method, byte priority) {
        this.listener = listener;
        this.method = method;
        this.priority = priority;
    }

    public byte getPriority() {
        return this.priority;
    }

    public Listener getListener() {
        return this.listener;
    }

    public Method getMethod() {
        return this.method;
    }
}

