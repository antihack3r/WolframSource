/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.analytics;

import net.wolframclient.analytics.AnalyticsConfigData;
import net.wolframclient.analytics.AnalyticsRequestData;
import net.wolframclient.analytics.JGoogleAnalyticsTracker;

public class Analytics {
    private final JGoogleAnalyticsTracker tracker;
    public final String ANALYTICS_CODE;
    public final String HOSTNAME;
    public long lastRequest;

    public Analytics(String analyticsCode, String hostName) {
        this.tracker = new JGoogleAnalyticsTracker(new AnalyticsConfigData(analyticsCode), JGoogleAnalyticsTracker.GoogleAnalyticsVersion.V_4_7_2);
        this.ANALYTICS_CODE = analyticsCode;
        this.HOSTNAME = hostName;
        this.lastRequest = System.currentTimeMillis();
        JGoogleAnalyticsTracker.setProxy(System.getenv("http_proxy"));
    }

    private boolean shouldTrack() {
        return true;
    }

    public void trackPageView(String url, String title) {
        if (!this.shouldTrack()) {
            return;
        }
        this.tracker.trackPageView(url, title, this.HOSTNAME);
        this.lastRequest = System.currentTimeMillis();
    }

    public void trackPageViewFromReferrer(String url, String title, String referrerSite, String referrerPage) {
        if (!this.shouldTrack()) {
            return;
        }
        this.tracker.trackPageViewFromReferrer(url, title, this.HOSTNAME, referrerSite, referrerPage);
        this.lastRequest = System.currentTimeMillis();
    }

    public void trackPageViewFromSearch(String url, String title, String searchSite, String keywords) {
        if (!this.shouldTrack()) {
            return;
        }
        this.tracker.trackPageViewFromSearch(url, title, this.HOSTNAME, searchSite, keywords);
        this.lastRequest = System.currentTimeMillis();
    }

    public void trackEvent(String category, String action) {
        if (!this.shouldTrack()) {
            return;
        }
        this.tracker.trackEvent(category, action);
        this.lastRequest = System.currentTimeMillis();
    }

    public void trackEvent(String category, String action, String label) {
        if (!this.shouldTrack()) {
            return;
        }
        this.tracker.trackEvent(category, action, label);
        this.lastRequest = System.currentTimeMillis();
    }

    public void trackEvent(String category, String action, String label, int value) {
        if (!this.shouldTrack()) {
            return;
        }
        this.tracker.trackEvent(category, action, label, new Integer(value));
        this.lastRequest = System.currentTimeMillis();
    }

    public void makeCustomRequest(AnalyticsRequestData data) {
        if (!this.shouldTrack()) {
            return;
        }
        this.tracker.makeCustomRequest(data);
        this.lastRequest = System.currentTimeMillis();
    }
}

