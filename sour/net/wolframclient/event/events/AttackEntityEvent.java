/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.event.events;

import net.minecraft.entity.Entity;
import net.wolframclient.event.EventCancellable;

public final class AttackEntityEvent
extends EventCancellable {
    private final Entity target;

    public AttackEntityEvent(Entity target) {
        this.target = target;
    }

    public Entity getTarget() {
        return this.target;
    }
}

