/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.event.events;

import net.wolframclient.event.Event;

public final class MovementEvent
implements Event {
    private double motionX;
    private double motionY;
    private double motionZ;

    public MovementEvent(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    public double getMotionX() {
        return this.motionX;
    }

    public void setMotionX(double motionX) {
        this.motionX = motionX;
    }

    public double getMotionY() {
        return this.motionY;
    }

    public void setMotionY(double motionY) {
        this.motionY = motionY;
    }

    public double getMotionZ() {
        return this.motionZ;
    }

    public void setMotionZ(double motionZ) {
        this.motionZ = motionZ;
    }
}

