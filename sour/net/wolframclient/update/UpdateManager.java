/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package net.wolframclient.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import net.minecraft.client.Minecraft;
import net.wolframclient.update.Version;

public class UpdateManager {
    private boolean outdated;
    private boolean updateForced;
    private String latestVersionString;

    public void checkForUpdates() {
        Version currentVersion = new Version("9.8.1");
        Version latestVersion = null;
        try {
            JsonArray json = this.fetchJson("https://api.github.com/repos/Wurst-Imperium/Wolfram-MCX2/releases").getAsJsonArray();
            for (JsonElement element : json) {
                JsonObject release = element.getAsJsonObject();
                if (!currentVersion.isPreRelease() && release.get("prerelease").getAsBoolean() || !this.containsCompatibleAsset(release.get("assets").getAsJsonArray())) continue;
                this.latestVersionString = release.get("tag_name").getAsString().substring(1);
                latestVersion = new Version(this.latestVersionString);
                break;
            }
            if (latestVersion == null) {
                throw new NullPointerException("Latest version is missing!");
            }
        }
        catch (Exception e) {
            System.err.println("[Updater] An error occurred!");
            e.printStackTrace();
            return;
        }
        System.out.println("[Updater] Current version: " + currentVersion);
        System.out.println("[Updater] Latest version: " + latestVersion);
        this.outdated = currentVersion.shouldUpdateTo(latestVersion);
        this.updateForced = false;
    }

    private boolean containsCompatibleAsset(JsonArray assets) {
        for (JsonElement asset : assets) {
            if (!asset.getAsJsonObject().get("name").getAsString().endsWith("MC1.12.jar")) continue;
            return true;
        }
        return false;
    }

    private JsonElement fetchJson(String url) throws IOException {
        URI u = URI.create(url);
        Throwable throwable = null;
        Object var4_5 = null;
        try (InputStream in = u.toURL().openStream();){
            return new JsonParser().parse((Reader)new BufferedReader(new InputStreamReader(in)));
        }
        catch (Throwable throwable2) {
            if (throwable == null) {
                throwable = throwable2;
            } else if (throwable != throwable2) {
                throwable.addSuppressed(throwable2);
            }
            throw throwable;
        }
    }

    public void openUpdateLink() {
        URI u = URI.create("https://www.wolframclient.net/download/minecraft-" + "1.12".replace(".", "-") + "/");
        try {
            Desktop.getDesktop().browse(u);
            Minecraft.getMinecraft().shutdown();
        }
        catch (IOException e) {
            System.err.println("Couldn't open link: " + u);
            e.printStackTrace();
        }
    }

    public boolean isOutdated() {
        return this.outdated;
    }

    public boolean isUpdateForced() {
        return this.updateForced;
    }

    public String getLatestVersion() {
        return this.latestVersionString;
    }
}

