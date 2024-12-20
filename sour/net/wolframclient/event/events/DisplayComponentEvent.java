/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.event.events;

import net.minecraft.util.text.ITextComponent;
import net.wolframclient.event.Event;

public final class DisplayComponentEvent
implements Event {
    private ITextComponent component;

    public DisplayComponentEvent(ITextComponent component) {
        this.component = component;
    }

    public ITextComponent getComponent() {
        return this.component;
    }

    public void setComponent(ITextComponent component) {
        this.component = component;
    }
}

