/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
 */
package net.wolframclient.gui.screen.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.ITextComponent;
import net.wolframclient.compatibility.WMath;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.gui.GuiManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class WolframChatLine
extends GuiChat {
    public WolframChatLine(String string) {
        super(string);
    }

    public WolframChatLine() {
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents((boolean)true);
        this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        this.inputField = new GuiTextField(0, this.fontRendererObj, 23, this.height - 17, this.getChatWidth() - 20, 12);
        this.inputField.setMaxStringLength(100);
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputField.setFocused(true);
        this.inputField.setText(this.defaultInputFieldText);
        this.inputField.setCanLoseFocus(false);
        this.tabCompleter = new GuiChat.ChatTabCompleter(this.inputField);
        this.inputField.setTextColor(0);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        WolframChatLine.drawRect(5, this.height - 21, 6 + this.getChatWidth(), this.height - 5, GuiManager.getHexMainColor() + -1073741824);
        Minecraft.getMinecraft().getTextureManager().bindTexture(WMinecraft.getPlayer().getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(7, this.height - 19, 8.0f, 8.0f, 8, 8, 12, 12, 64.0f, 64.0f);
        this.inputField.drawTextBox();
        ITextComponent var4 = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
        if (var4 != null && var4.getStyle().getHoverEvent() != null) {
            this.handleComponentHover(var4, mouseX, mouseY);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public float getChatScale() {
        return this.mc.gameSettings.chatScale;
    }

    public int getChatWidth() {
        return WMath.ceil((float)WolframChatLine.calculateChatboxWidth(this.mc.gameSettings.chatWidth) / this.getChatScale());
    }

    public static int calculateChatboxWidth(float p_146233_0_) {
        int var1 = 320;
        int var2 = 40;
        return WMath.floor(p_146233_0_ * 280.0f + 40.0f);
    }
}

