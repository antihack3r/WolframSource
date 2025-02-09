/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.event.events;

import net.minecraft.entity.Entity;
import net.wolframclient.event.EventCancellable;

public final class NametagRenderEvent
extends EventCancellable {
    private final Entity entity;
    private String name;

    public NametagRenderEvent(Entity entity, String name) {
        this.entity = entity;
        this.name = name;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

