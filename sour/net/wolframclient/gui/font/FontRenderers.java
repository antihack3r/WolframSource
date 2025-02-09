/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.gui.font;

import java.awt.Font;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.wolframclient.Wolfram;
import net.wolframclient.gui.font.WolframFontRenderer;

public final class FontRenderers {
    private static final Minecraft mc;
    public static final FontRenderer MINECRAFT;
    private static final Font BASEFONT;
    public static final WolframFontRenderer DEFAULT;
    public static final WolframFontRenderer TITLE;
    public static final WolframFontRenderer SMALL;
    public static final WolframFontRenderer BIG;

    static {
        Font font;
        mc = Minecraft.getMinecraft();
        MINECRAFT = new FontRenderer(FontRenderers.mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.getTextureManager(), false);
        try {
            Throwable throwable = null;
            Object var2_3 = null;
            try (InputStream in = mc.getResourceManager().getResource(new ResourceLocation("wolfram/URWGothicLBook.ttf")).getInputStream();){
                font = Font.createFont(0, in);
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
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Font could not be loaded. Using serif font.");
            font = new Font("default", 0, 1);
        }
        BASEFONT = font;
        DEFAULT = new WolframFontRenderer(FontRenderers.getFont(17), true, 8);
        TITLE = new WolframFontRenderer(FontRenderers.getFont(25), true, 8);
        SMALL = new WolframFontRenderer(FontRenderers.getFont(16), true, 8);
        BIG = new WolframFontRenderer(FontRenderers.getFont(19), true, 8);
    }

    public static FontRenderer getFontRenderer() {
        if (Wolfram.getWolfram().getClientSettings().getBoolean("use_default_font")) {
            return MINECRAFT;
        }
        return DEFAULT;
    }

    private static Font getFont(int size) {
        return BASEFONT.deriveFont(0, size);
    }
}

