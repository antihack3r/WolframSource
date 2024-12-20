/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.analytics;

import net.wolframclient.Wolfram;
import net.wolframclient.analytics.VisitorData;

public class AnalyticsConfigData {
    private final String trackingCode;
    private String encoding = "UTF-8";
    private String screenResolution = null;
    private String colorDepth = null;
    private String userLanguage = null;
    private String flashVersion = null;
    private String userAgent = null;
    private VisitorData visitorData;

    public AnalyticsConfigData(String argTrackingCode) {
        this(argTrackingCode, VisitorData.newSession(Wolfram.getWolfram().getClientSettings().getInt("analytics_id"), Wolfram.getWolfram().getClientSettings().getInt("analytics_first_launch"), Wolfram.getWolfram().getClientSettings().getInt("analytics_last_launch"), Wolfram.getWolfram().getClientSettings().getInt("analytics_launch_count")));
    }

    public AnalyticsConfigData(String argTrackingCode, VisitorData visitorData) {
        if (argTrackingCode == null) {
            throw new RuntimeException("Tracking code cannot be null");
        }
        this.trackingCode = argTrackingCode;
        this.visitorData = visitorData;
    }

    public String getColorDepth() {
        return this.colorDepth;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public String getFlashVersion() {
        return this.flashVersion;
    }

    public String getScreenResolution() {
        return this.screenResolution;
    }

    public String getTrackingCode() {
        return this.trackingCode;
    }

    public String getUserLanguage() {
        return this.userLanguage;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public VisitorData getVisitorData() {
        return this.visitorData;
    }

    public void setColorDepth(String argColorDepth) {
        this.colorDepth = argColorDepth;
    }

    public void setEncoding(String argEncoding) {
        this.encoding = argEncoding;
    }

    public void setFlashVersion(String argFlashVersion) {
        this.flashVersion = argFlashVersion;
    }

    public void setScreenResolution(String argScreenResolution) {
        this.screenResolution = argScreenResolution;
    }

    public void setUserLanguage(String argUserLanguage) {
        this.userLanguage = argUserLanguage;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}

