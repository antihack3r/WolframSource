/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Iterables
 *  org.apache.commons.io.Charsets
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.src;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Lang {
    private static final Splitter splitter = Splitter.on((char)'=').limit(2);
    private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");

    public static void resourcesReloaded() {
        Map map = I18n.getLocaleProperties();
        ArrayList<String> list = new ArrayList<String>();
        String s = "optifine/lang/";
        String s1 = "en_us";
        String s2 = ".lang";
        list.add(String.valueOf(s) + s1 + s2);
        if (!Config.getGameSettings().language.equals(s1)) {
            list.add(String.valueOf(s) + Config.getGameSettings().language + s2);
        }
        String[] astring = list.toArray(new String[list.size()]);
        Lang.loadResources(Config.getDefaultResourcePack(), astring, map);
        IResourcePack[] airesourcepack = Config.getResourcePacks();
        int i = 0;
        while (i < airesourcepack.length) {
            IResourcePack iresourcepack = airesourcepack[i];
            Lang.loadResources(iresourcepack, astring, map);
            ++i;
        }
    }

    private static void loadResources(IResourcePack p_loadResources_0_, String[] p_loadResources_1_, Map p_loadResources_2_) {
        try {
            int i = 0;
            while (i < p_loadResources_1_.length) {
                InputStream inputstream;
                String s = p_loadResources_1_[i];
                ResourceLocation resourcelocation = new ResourceLocation(s);
                if (p_loadResources_0_.resourceExists(resourcelocation) && (inputstream = p_loadResources_0_.getInputStream(resourcelocation)) != null) {
                    Lang.loadLocaleData(inputstream, p_loadResources_2_);
                }
                ++i;
            }
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    public static void loadLocaleData(InputStream p_loadLocaleData_0_, Map p_loadLocaleData_1_) throws IOException {
        for (String s : IOUtils.readLines((InputStream)p_loadLocaleData_0_, (Charset)Charsets.UTF_8)) {
            String[] astring;
            if (s.isEmpty() || s.charAt(0) == '#' || (astring = (String[])Iterables.toArray((Iterable)splitter.split((CharSequence)s), String.class)) == null || astring.length != 2) continue;
            String s1 = astring[0];
            String s2 = pattern.matcher(astring[1]).replaceAll("%$1s");
            p_loadLocaleData_1_.put(s1, s2);
        }
    }

    public static String get(String p_get_0_) {
        return I18n.format(p_get_0_, new Object[0]);
    }

    public static String get(String p_get_0_, String p_get_1_) {
        String s = I18n.format(p_get_0_, new Object[0]);
        return s != null && !s.equals(p_get_0_) ? s : p_get_1_;
    }

    public static String getOn() {
        return I18n.format("options.on", new Object[0]);
    }

    public static String getOff() {
        return I18n.format("options.off", new Object[0]);
    }

    public static String getFast() {
        return I18n.format("options.graphics.fast", new Object[0]);
    }

    public static String getFancy() {
        return I18n.format("options.graphics.fancy", new Object[0]);
    }

    public static String getDefault() {
        return I18n.format("generator.default", new Object[0]);
    }
}

