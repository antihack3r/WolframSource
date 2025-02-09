/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.gui;

import net.minecraft.client.Minecraft;
import net.wolframclient.Wolfram;
import net.wolframclient.clickgui.WolframGui;
import net.wolframclient.event.Dispatcher;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.GuiRenderEvent;
import net.wolframclient.event.events.KeyPressEvent;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.module.modules.render.PotionEffects;
import net.wolframclient.storage.MapStorage;
import net.wolframclient.utils.HSLColor;

public final class GuiManager
implements Listener {
    public WolframGui gui;

    public static int getHexMainColor() {
        MapStorage settings = Wolfram.getWolfram().storageManager.clientSettings;
        float hue = settings.getFloat("color_hue") * 360.0f;
        float saturation = settings.getFloat("color_saturation") * 100.0f;
        float luminosity = settings.getFloat("color_luminosity") * 100.0f;
        if (settings.getBoolean("rainbow")) {
            float speed = 20000.0f - settings.getFloat("rainbow_speed") * 19000.0f;
            float x = (float)(System.currentTimeMillis() % (long)((int)speed)) / speed;
            hue = x * 360.0f;
        }
        HSLColor hsl = new HSLColor(hue, saturation, luminosity);
        return hsl.getRGB().getRGB();
    }

    public static int getNegativeHexColor() {
        return 0xFFFFFF - GuiManager.getHexMainColor();
    }

    public void loadGui() {
        this.gui = new WolframGui();
        registry.registerListener(this);
    }

    public void render() {
        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            this.renderTextUtils(2);
            if (Wolfram.getWolfram().getClientSettings().getBoolean("watermark")) {
                FontRenderers.DEFAULT.drawStringWithShadow(Wolfram.getClientName(), 2.0f, 2.0f, -1325400065);
            }
        }
        this.gui.update();
        this.gui.render();
        Dispatcher.call(new GuiRenderEvent());
    }

    private void renderTextUtils(int y) {
        if (Wolfram.getWolfram().storageManager.moduleStates.getBoolean("PotionEffects")) {
            PotionEffects.getInstance().render(y);
        }
    }

    @EventTarget
    public void onKeyPress(KeyPressEvent event) {
        if (event.getKeyCode() == Wolfram.getWolfram().storageManager.clientSettings.getInt("clickgui_key")) {
            Minecraft.getMinecraft().displayGuiScreen(this.gui);
        }
    }
}

