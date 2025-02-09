/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.event;

import net.wolframclient.event.Event;
import net.wolframclient.event.EventRegistry;

public final class Dispatcher {
    public static void call(Event event) {
        EventRegistry.getInstance().fire(event);
    }
}

