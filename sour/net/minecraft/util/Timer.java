/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.client.Minecraft;

public class Timer {
    public int elapsedTicks;
    public float field_194147_b;
    public float renderPartialTicks;
    private long lastSyncSysClock;
    private float field_194149_e;
    public float timerSpeed = 1.0f;

    public Timer(float tps) {
        this.field_194149_e = 1000.0f / tps;
        this.lastSyncSysClock = Minecraft.getSystemTime();
    }

    public void updateTimer() {
        long i = Minecraft.getSystemTime();
        this.renderPartialTicks = (float)(i - this.lastSyncSysClock) / this.field_194149_e * this.timerSpeed;
        this.lastSyncSysClock = i;
        this.field_194147_b += this.renderPartialTicks;
        this.elapsedTicks = (int)this.field_194147_b;
        this.field_194147_b -= (float)this.elapsedTicks;
    }
}

