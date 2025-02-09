/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package net.wolframclient.gui.screen.wolfram;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.wolframclient.Wolfram;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.gui.screen.minecraft.WolframOptionSlider;
import org.lwjgl.input.Keyboard;

public class WolframPreferences
extends GuiScreen {
    private final GuiScreen parentScreen;
    private boolean keybindButtonSelected = false;
    GuiButton keybindButton;

    public WolframPreferences(GuiScreen parent) {
        this.parentScreen = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new WolframOptionSlider(-98, this.width / 2 - 152, 40, 150, 20, "color_hue", "Hue"));
        this.buttonList.add(new WolframOptionSlider(-98, this.width / 2 - 152, 65, 150, 20, "color_saturation", "Saturation"));
        this.buttonList.add(new WolframOptionSlider(-98, this.width / 2 - 152, 90, 150, 20, "color_luminance", "Luminance"));
        this.buttonList.add(new GuiButton(-97, this.width / 2 + 2, 65, 150, 20, "Rainbow mode: " + (Wolfram.getWolfram().storageManager.clientSettings.getBoolean("rainbow") ? "On" : "Off")));
        this.buttonList.add(new WolframOptionSlider(-98, this.width / 2 + 2, 90, 150, 20, "rainbow_speed", "Rainbow speed"));
        this.buttonList.add(new GuiButton(-92, this.width / 2 - 152, 115, 150, 20, "Use Minecraft font: " + (Wolfram.getWolfram().storageManager.clientSettings.getBoolean("use_default_font") ? "Yes" : "No")));
        this.keybindButton = new GuiButton(-91, this.width / 2 - 152, 140, 150, 20, "ClickGUI key: " + Keyboard.getKeyName((int)Wolfram.getWolfram().storageManager.clientSettings.getInt("clickgui_key")));
        this.buttonList.add(new GuiButton(-90, this.width / 2 + 2, 40, 150, 20, "Block Overlay: " + (Wolfram.getWolfram().storageManager.clientSettings.getBoolean("block_overlay") ? "On" : "Off")));
        this.buttonList.add(this.keybindButton);
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done", new Object[0])));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(FontRenderers.TITLE, "Preferences", this.width / 2, 15, -263693982);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        Wolfram.getWolfram().storageManager.clientSettings.save();
        switch (button.id) {
            case -97: {
                Wolfram.getWolfram().storageManager.clientSettings.set("rainbow", !Wolfram.getWolfram().storageManager.clientSettings.getBoolean("rainbow"));
                button.displayString = "Rainbow mode " + (Wolfram.getWolfram().storageManager.clientSettings.getBoolean("rainbow") ? "On" : "Off");
                break;
            }
            case -94: {
                Wolfram.getWolfram().storageManager.clientSettings.set("tabgui", !Wolfram.getWolfram().storageManager.clientSettings.getBoolean("tabgui"));
                button.displayString = "TabGUI " + (Wolfram.getWolfram().storageManager.clientSettings.getBoolean("tabgui") ? "On" : "Off");
                break;
            }
            case -92: {
                Wolfram.getWolfram().storageManager.clientSettings.set("use_default_font", !Wolfram.getWolfram().storageManager.clientSettings.getBoolean("use_default_font"));
                button.displayString = "Use Minecraft font " + (Wolfram.getWolfram().storageManager.clientSettings.getBoolean("use_default_font") ? "Yes" : "No");
                Minecraft.getMinecraft().fontRendererObj = FontRenderers.getFontRenderer();
                break;
            }
            case -91: {
                this.keybindButtonSelected = true;
                button.displayString = "> " + button.displayString + " <";
                break;
            }
            case -90: {
                Wolfram.getWolfram().storageManager.clientSettings.set("block_overlay", !Wolfram.getWolfram().storageManager.clientSettings.getBoolean("block_overlay"));
                button.displayString = "Block Overlay:  " + (Wolfram.getWolfram().storageManager.clientSettings.getBoolean("block_overlay") ? "On" : "Off");
                break;
            }
            case 200: {
                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.keybindButtonSelected) {
            this.keybindButtonSelected = false;
            if (keyCode == 1) {
                return;
            }
            Wolfram.getWolfram().storageManager.clientSettings.set("clickgui_key", keyCode);
            this.keybindButton.displayString = "ClickGUI key: " + Keyboard.getKeyName((int)keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }
}

