/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.analytics;

public class AnalyticsRequestData {
    private String pageTitle = null;
    private String hostName = null;
    private String pageURL = null;
    private String eventCategory = null;
    private String eventAction = null;
    private String eventLabel = null;
    private Integer eventValue = null;
    private String utmcsr = "(direct)";
    private String utmccn = "(direct)";
    private String utmctr = null;
    private String utmcmd = "(none)";
    private String utmcct = null;

    public void setReferrer(String argSite, String argPage) {
        this.utmcmd = "referral";
        this.utmcct = argPage;
        this.utmccn = "(referral)";
        this.utmcsr = argSite;
        this.utmctr = null;
    }

    public void setSearchReferrer(String argSearchSource, String argSearchKeywords) {
        this.utmcsr = argSearchSource;
        this.utmctr = argSearchKeywords;
        this.utmcmd = "organic";
        this.utmccn = "(organic)";
        this.utmcct = null;
    }

    public String getUtmcsr() {
        return this.utmcsr;
    }

    public String getUtmccn() {
        return this.utmccn;
    }

    public String getUtmctr() {
        return this.utmctr;
    }

    public String getUtmcmd() {
        return this.utmcmd;
    }

    public String getUtmcct() {
        return this.utmcct;
    }

    public String getEventAction() {
        return this.eventAction;
    }

    public String getEventCategory() {
        return this.eventCategory;
    }

    public String getEventLabel() {
        return this.eventLabel;
    }

    public Integer getEventValue() {
        return this.eventValue;
    }

    public String getHostName() {
        return this.hostName;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }

    public String getPageURL() {
        return this.pageURL;
    }

    public void setEventAction(String argEventAction) {
        this.eventAction = argEventAction;
    }

    public void setEventCategory(String argEventCategory) {
        this.eventCategory = argEventCategory;
    }

    public void setEventLabel(String argEventLabel) {
        this.eventLabel = argEventLabel;
    }

    public void setEventValue(Integer argEventValue) {
        this.eventValue = argEventValue;
    }

    public void setHostName(String argHostName) {
        this.hostName = argHostName;
    }

    public void setPageTitle(String argContentTitle) {
        this.pageTitle = argContentTitle;
    }

    public void setPageURL(String argPageURL) {
        this.pageURL = argPageURL;
    }
}

