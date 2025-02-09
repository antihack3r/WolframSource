/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.utils;

public class AnimationTimer {
    private final int delay;
    private int bottom;
    private int top;
    private int timer;
    private boolean wasRising;

    public AnimationTimer(int delay) {
        this.delay = delay;
        this.top = delay;
        this.bottom = 0;
    }

    public void update(boolean increment) {
        if (increment) {
            if (this.timer < this.delay) {
                if (!this.wasRising) {
                    this.bottom = this.timer;
                }
                ++this.timer;
            }
            this.wasRising = true;
        } else {
            if (this.timer > 0) {
                if (this.wasRising) {
                    this.top = this.timer;
                }
                --this.timer;
            }
            this.wasRising = false;
        }
    }

    public double getValue() {
        if (this.wasRising) {
            return Math.sin((double)(this.timer - this.bottom) / (double)(this.delay - this.bottom) * Math.PI / 2.0);
        }
        return 1.0 - Math.cos((double)this.timer / (double)this.top * Math.PI / 2.0);
    }
}

