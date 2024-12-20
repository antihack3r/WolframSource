/*
 * Decompiled with CFR 0.152.
 */
package shadersmod.client;

import net.minecraft.src.Config;
import net.minecraft.util.Util;

public class ShaderMacros {
    private static String PREFIX_MACRO = "MC_";
    public static final String MC_VERSION = "MC_VERSION";
    public static final String MC_GL_VERSION = "MC_GL_VERSION";
    public static final String MC_GLSL_VERSION = "MC_GLSL_VERSION";
    public static final String MC_OS_WINDOWS = "MC_OS_WINDOWS";
    public static final String MC_OS_MAC = "MC_OS_MAC";
    public static final String MC_OS_LINUX = "MC_OS_LINUX";
    public static final String MC_OS_OTHER = "MC_OS_OTHER";
    public static final String MC_GL_VENDOR_ATI = "MC_GL_VENDOR_ATI";
    public static final String MC_GL_VENDOR_INTEL = "MC_GL_VENDOR_INTEL";
    public static final String MC_GL_VENDOR_NVIDIA = "MC_GL_VENDOR_NVIDIA";
    public static final String MC_GL_VENDOR_XORG = "MC_GL_VENDOR_XORG";
    public static final String MC_GL_VENDOR_OTHER = "MC_GL_VENDOR_OTHER";
    public static final String MC_GL_RENDERER_RADEON = "MC_GL_RENDERER_RADEON";
    public static final String MC_GL_RENDERER_GEFORCE = "MC_GL_RENDERER_GEFORCE";
    public static final String MC_GL_RENDERER_QUADRO = "MC_GL_RENDERER_QUADRO";
    public static final String MC_GL_RENDERER_INTEL = "MC_GL_RENDERER_INTEL";
    public static final String MC_GL_RENDERER_GALLIUM = "MC_GL_RENDERER_GALLIUM";
    public static final String MC_GL_RENDERER_MESA = "MC_GL_RENDERER_MESA";
    public static final String MC_GL_RENDERER_OTHER = "MC_GL_RENDERER_OTHER";
    private static String[] extensionMacros;

    public static String getOs() {
        Util.EnumOS util$enumos = Util.getOSType();
        switch (util$enumos) {
            case WINDOWS: {
                return MC_OS_WINDOWS;
            }
            case OSX: {
                return MC_OS_MAC;
            }
            case LINUX: {
                return MC_OS_LINUX;
            }
        }
        return MC_OS_OTHER;
    }

    public static String getVendor() {
        String s = Config.openGlVendor;
        if (s == null) {
            return MC_GL_VENDOR_OTHER;
        }
        if ((s = s.toLowerCase()).startsWith("ati")) {
            return MC_GL_VENDOR_ATI;
        }
        if (s.startsWith("intel")) {
            return MC_GL_VENDOR_INTEL;
        }
        if (s.startsWith("nvidia")) {
            return MC_GL_VENDOR_NVIDIA;
        }
        return s.startsWith("x.org") ? MC_GL_VENDOR_XORG : MC_GL_VENDOR_OTHER;
    }

    public static String getRenderer() {
        String s = Config.openGlRenderer;
        if (s == null) {
            return MC_GL_RENDERER_OTHER;
        }
        if ((s = s.toLowerCase()).startsWith("amd")) {
            return MC_GL_RENDERER_RADEON;
        }
        if (s.startsWith("ati")) {
            return MC_GL_RENDERER_RADEON;
        }
        if (s.startsWith("radeon")) {
            return MC_GL_RENDERER_RADEON;
        }
        if (s.startsWith("gallium")) {
            return MC_GL_RENDERER_GALLIUM;
        }
        if (s.startsWith("intel")) {
            return MC_GL_RENDERER_INTEL;
        }
        if (s.startsWith("geforce")) {
            return MC_GL_RENDERER_GEFORCE;
        }
        if (s.startsWith("nvidia")) {
            return MC_GL_RENDERER_GEFORCE;
        }
        if (s.startsWith("quadro")) {
            return MC_GL_RENDERER_QUADRO;
        }
        if (s.startsWith("nvs")) {
            return MC_GL_RENDERER_QUADRO;
        }
        return s.startsWith("mesa") ? MC_GL_RENDERER_MESA : MC_GL_RENDERER_OTHER;
    }

    public static String getPrefixMacro() {
        return PREFIX_MACRO;
    }

    public static String[] getExtensions() {
        if (extensionMacros == null) {
            String[] astring = Config.getOpenGlExtensions();
            String[] astring1 = new String[astring.length];
            int i = 0;
            while (i < astring.length) {
                astring1[i] = String.valueOf(PREFIX_MACRO) + astring[i];
                ++i;
            }
            extensionMacros = astring1;
        }
        return extensionMacros;
    }
}

