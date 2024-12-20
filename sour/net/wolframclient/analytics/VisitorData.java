/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.analytics;

import java.security.SecureRandom;
import net.wolframclient.Wolfram;

public class VisitorData {
    private int visitorId;
    private long timestampFirst;
    private long timestampPrevious;
    private long timestampCurrent;
    private int visits;

    VisitorData(int visitorId, long timestampFirst, long timestampPrevious, long timestampCurrent, int visits) {
        this.visitorId = visitorId;
        this.timestampFirst = timestampFirst;
        this.timestampPrevious = timestampPrevious;
        this.timestampCurrent = timestampCurrent;
        this.visits = visits;
    }

    public static VisitorData newVisitor() {
        int visitorId = new SecureRandom().nextInt() & Integer.MAX_VALUE;
        long now = VisitorData.now();
        return new VisitorData(visitorId, now, now, now, 1);
    }

    public static VisitorData newSession(int visitorId, long timestampfirst, long timestamplast, int visits) {
        long now = VisitorData.now();
        Wolfram.getWolfram().getClientSettings().set("analytics_last_launch", now);
        Wolfram.getWolfram().getClientSettings().set("analytics_launch_count", visits + 1);
        Wolfram.getWolfram().getClientSettings().save();
        return new VisitorData(visitorId, timestampfirst, timestamplast, now, visits + 1);
    }

    public void resetSession() {
        long now = VisitorData.now();
        this.timestampPrevious = this.timestampCurrent;
        this.timestampCurrent = now;
        ++this.visits;
    }

    private static long now() {
        long now = System.currentTimeMillis() / 1000L;
        return now;
    }

    public int getVisitorId() {
        return this.visitorId;
    }

    public long getTimestampFirst() {
        return this.timestampFirst;
    }

    public long getTimestampPrevious() {
        return this.timestampPrevious;
    }

    public long getTimestampCurrent() {
        return this.timestampCurrent;
    }

    public int getVisits() {
        return this.visits;
    }
}

