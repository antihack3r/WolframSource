/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.analytics;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import net.wolframclient.analytics.AnalyticsConfigData;
import net.wolframclient.analytics.AnalyticsRequestData;
import net.wolframclient.analytics.GoogleAnalyticsV4_7_2;
import net.wolframclient.analytics.IGoogleAnalyticsURLBuilder;

public class JGoogleAnalyticsTracker {
    private static Logger logger = Logger.getLogger(JGoogleAnalyticsTracker.class.getName());
    private static final ThreadGroup asyncThreadGroup = new ThreadGroup("Async Google Analytics Threads");
    private static long asyncThreadsRunning = 0L;
    private static Proxy proxy = Proxy.NO_PROXY;
    private static LinkedList<String> fifo = new LinkedList();
    private static volatile Thread backgroundThread = null;
    private static boolean backgroundThreadMayRun = false;
    private GoogleAnalyticsVersion gaVersion;
    private AnalyticsConfigData configData;
    private IGoogleAnalyticsURLBuilder builder;
    private DispatchMode mode;
    private boolean enabled;

    static {
        asyncThreadGroup.setMaxPriority(1);
        asyncThreadGroup.setDaemon(true);
    }

    public JGoogleAnalyticsTracker(AnalyticsConfigData argConfigData, GoogleAnalyticsVersion argVersion) {
        this(argConfigData, argVersion, DispatchMode.SINGLE_THREAD);
    }

    public JGoogleAnalyticsTracker(AnalyticsConfigData argConfigData, GoogleAnalyticsVersion argVersion, DispatchMode argMode) {
        this.gaVersion = argVersion;
        this.configData = argConfigData;
        this.createBuilder();
        this.enabled = true;
        this.setDispatchMode(argMode);
    }

    public void setDispatchMode(DispatchMode argMode) {
        if (argMode == null) {
            argMode = DispatchMode.SINGLE_THREAD;
        }
        if (argMode == DispatchMode.SINGLE_THREAD) {
            this.startBackgroundThread();
        }
        this.mode = argMode;
    }

    public DispatchMode getDispatchMode() {
        return this.mode;
    }

    public boolean isSynchronous() {
        return this.mode == DispatchMode.SYNCHRONOUS;
    }

    public boolean isSingleThreaded() {
        return this.mode == DispatchMode.SINGLE_THREAD;
    }

    public boolean isMultiThreaded() {
        return this.mode == DispatchMode.MULTI_THREAD;
    }

    public void resetSession() {
        this.builder.resetSession();
    }

    public void setEnabled(boolean argEnabled) {
        this.enabled = argEnabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public static void setProxy(Proxy argProxy) {
        proxy = argProxy != null ? argProxy : Proxy.NO_PROXY;
    }

    public static void setProxy(String proxyAddr) {
        if (proxyAddr != null) {
            Scanner s = new Scanner(proxyAddr);
            proxyAddr = null;
            int proxyPort = 8080;
            try {
                s.findInLine("(http://|)([^:/]+)(:|)([0-9]*)(/|)");
                MatchResult m = s.match();
                if (m.groupCount() >= 2) {
                    proxyAddr = m.group(2);
                }
                if (m.groupCount() >= 4 && m.group(4).length() != 0) {
                    proxyPort = Integer.parseInt(m.group(4));
                }
            }
            finally {
                s.close();
            }
            if (proxyAddr != null) {
                InetSocketAddress sa = new InetSocketAddress(proxyAddr, proxyPort);
                JGoogleAnalyticsTracker.setProxy(new Proxy(Proxy.Type.HTTP, sa));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public static void completeBackgroundTasks(long timeoutMillis) {
        boolean fifoEmpty = false;
        boolean asyncThreadsCompleted = false;
        long absTimeout = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < absTimeout) {
            Object object = fifo;
            // MONITORENTER : object
            fifoEmpty = fifo.size() == 0;
            // MONITOREXIT : object
            object = JGoogleAnalyticsTracker.class;
            // MONITORENTER : net.wolframclient.analytics.JGoogleAnalyticsTracker.class
            asyncThreadsCompleted = asyncThreadsRunning == 0L;
            // MONITOREXIT : object
            if (fifoEmpty && asyncThreadsCompleted) {
                return;
            }
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException e) {
                return;
            }
        }
    }

    public void trackPageView(String argPageURL, String argPageTitle, String argHostName) {
        if (argPageURL == null) {
            throw new IllegalArgumentException("Page URL cannot be null, Google will not track the data.");
        }
        AnalyticsRequestData data = new AnalyticsRequestData();
        data.setHostName(argHostName);
        data.setPageTitle(argPageTitle);
        data.setPageURL(argPageURL);
        this.makeCustomRequest(data);
    }

    public void trackPageViewFromReferrer(String argPageURL, String argPageTitle, String argHostName, String argReferrerSite, String argReferrerPage) {
        if (argPageURL == null) {
            throw new IllegalArgumentException("Page URL cannot be null, Google will not track the data.");
        }
        AnalyticsRequestData data = new AnalyticsRequestData();
        data.setHostName(argHostName);
        data.setPageTitle(argPageTitle);
        data.setPageURL(argPageURL);
        data.setReferrer(argReferrerSite, argReferrerPage);
        this.makeCustomRequest(data);
    }

    public void trackPageViewFromSearch(String argPageURL, String argPageTitle, String argHostName, String argSearchSource, String argSearchKeywords) {
        if (argPageURL == null) {
            throw new IllegalArgumentException("Page URL cannot be null, Google will not track the data.");
        }
        AnalyticsRequestData data = new AnalyticsRequestData();
        data.setHostName(argHostName);
        data.setPageTitle(argPageTitle);
        data.setPageURL(argPageURL);
        data.setSearchReferrer(argSearchSource, argSearchKeywords);
        this.makeCustomRequest(data);
    }

    public void trackEvent(String argCategory, String argAction) {
        this.trackEvent(argCategory, argAction, null, null);
    }

    public void trackEvent(String argCategory, String argAction, String argLabel) {
        this.trackEvent(argCategory, argAction, argLabel, null);
    }

    public void trackEvent(String argCategory, String argAction, String argLabel, Integer argValue) {
        AnalyticsRequestData data = new AnalyticsRequestData();
        data.setEventCategory(argCategory);
        data.setEventAction(argAction);
        data.setEventLabel(argLabel);
        data.setEventValue(argValue);
        this.makeCustomRequest(data);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void makeCustomRequest(AnalyticsRequestData argData) {
        if (!this.enabled) {
            logger.log(Level.CONFIG, "Ignoring tracking request, enabled is false");
            return;
        }
        if (argData == null) {
            throw new NullPointerException("Data cannot be null");
        }
        if (this.builder == null) {
            throw new NullPointerException("Class was not initialized");
        }
        final String url = this.builder.buildURL(argData);
        final String userAgent = this.configData.getUserAgent();
        switch (this.mode) {
            case MULTI_THREAD: {
                Thread t = new Thread(asyncThreadGroup, "AnalyticsThread-" + asyncThreadGroup.activeCount()){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    @Override
                    public void run() {
                        Class<JGoogleAnalyticsTracker> clazz = JGoogleAnalyticsTracker.class;
                        synchronized (JGoogleAnalyticsTracker.class) {
                            asyncThreadsRunning = asyncThreadsRunning + 1L;
                            // ** MonitorExit[var1_1] (shouldn't be in output)
                            try {
                                JGoogleAnalyticsTracker.dispatchRequest(url, userAgent);
                            }
                            catch (Throwable throwable) {
                                Class<JGoogleAnalyticsTracker> clazz2 = JGoogleAnalyticsTracker.class;
                                synchronized (JGoogleAnalyticsTracker.class) {
                                    asyncThreadsRunning = asyncThreadsRunning - 1L;
                                    // ** MonitorExit[var2_3] (shouldn't be in output)
                                    throw throwable;
                                }
                            }
                            Class<JGoogleAnalyticsTracker> clazz3 = JGoogleAnalyticsTracker.class;
                            synchronized (JGoogleAnalyticsTracker.class) {
                                asyncThreadsRunning = asyncThreadsRunning - 1L;
                                // ** MonitorExit[var2_4] (shouldn't be in output)
                                return;
                            }
                        }
                    }
                };
                t.setDaemon(true);
                t.start();
                break;
            }
            case SYNCHRONOUS: {
                JGoogleAnalyticsTracker.dispatchRequest(url, userAgent);
                break;
            }
            default: {
                LinkedList<String> linkedList = fifo;
                synchronized (linkedList) {
                    fifo.addLast(url);
                    fifo.notify();
                }
                if (backgroundThreadMayRun) break;
                logger.log(Level.SEVERE, "A tracker request has been added to the queue but the background thread isn't running.", url);
            }
        }
    }

    private static void dispatchRequest(String argURL, String userAgent) {
        try {
            URL url = new URL(argURL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection(proxy);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            if (userAgent != null) {
                connection.addRequestProperty("User-Agent", userAgent);
            }
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                logger.log(Level.SEVERE, "JGoogleAnalyticsTracker: Error requesting url '" + argURL + "', received response code " + responseCode);
            } else {
                logger.log(Level.CONFIG, "JGoogleAnalyticsTracker: Tracking success for url '" + argURL + "'");
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error making tracking request", e);
        }
    }

    private void createBuilder() {
        switch (this.gaVersion) {
            case V_4_7_2: {
                this.builder = new GoogleAnalyticsV4_7_2(this.configData);
                break;
            }
            default: {
                this.builder = new GoogleAnalyticsV4_7_2(this.configData);
            }
        }
    }

    private synchronized void startBackgroundThread() {
        if (backgroundThread == null) {
            backgroundThreadMayRun = true;
            backgroundThread = new Thread(asyncThreadGroup, "AnalyticsBackgroundThread"){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void run() {
                    logger.log(Level.CONFIG, "AnalyticsBackgroundThread started");
                    while (backgroundThreadMayRun) {
                        try {
                            LinkedList linkedList;
                            String url = null;
                            LinkedList linkedList2 = fifo;
                            synchronized (linkedList2) {
                                if (fifo.isEmpty()) {
                                    fifo.wait();
                                }
                                if (!fifo.isEmpty()) {
                                    url = (String)fifo.getFirst();
                                }
                            }
                            if (url == null) continue;
                            try {
                                JGoogleAnalyticsTracker.dispatchRequest(url, JGoogleAnalyticsTracker.this.configData.getUserAgent());
                            }
                            catch (Throwable throwable) {
                                linkedList = fifo;
                                synchronized (linkedList) {
                                    fifo.removeFirst();
                                }
                                throw throwable;
                            }
                            linkedList = fifo;
                            synchronized (linkedList) {
                                fifo.removeFirst();
                            }
                        }
                        catch (Exception e) {
                            logger.log(Level.SEVERE, "Got exception from dispatch thread", e);
                        }
                    }
                }
            };
            backgroundThread.setDaemon(true);
            backgroundThread.start();
        }
    }

    public static synchronized void stopBackgroundThread(long timeoutMillis) {
        backgroundThreadMayRun = false;
        fifo.notify();
        if (backgroundThread != null && timeoutMillis > 0L) {
            try {
                backgroundThread.join(timeoutMillis);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            backgroundThread = null;
        }
    }

    public static enum DispatchMode {
        SYNCHRONOUS,
        MULTI_THREAD,
        SINGLE_THREAD;

    }

    public static enum GoogleAnalyticsVersion {
        V_4_7_2;

    }
}

