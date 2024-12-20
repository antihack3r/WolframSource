/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.analytics;

import net.wolframclient.analytics.AnalyticsRequestData;

public interface IGoogleAnalyticsURLBuilder {
    public void resetSession();

    public String getGoogleAnalyticsVersion();

    public String buildURL(AnalyticsRequestData var1);
}

