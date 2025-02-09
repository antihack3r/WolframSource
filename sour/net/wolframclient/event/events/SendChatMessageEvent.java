/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.event.events;

import net.wolframclient.event.EventCancellable;

public final class SendChatMessageEvent
extends EventCancellable {
    private String message;

    public SendChatMessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

