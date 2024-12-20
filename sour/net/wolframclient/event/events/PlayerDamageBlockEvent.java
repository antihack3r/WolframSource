/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.event.events;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.wolframclient.event.Event;

public final class PlayerDamageBlockEvent
implements Event {
    private final BlockPos pos;
    private final EnumFacing direction;
    private float progress;

    public PlayerDamageBlockEvent(BlockPos pos, EnumFacing direction, float progress) {
        this.pos = pos;
        this.direction = direction;
        this.progress = progress;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public EnumFacing getDirection() {
        return this.direction;
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }
}

