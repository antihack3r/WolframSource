/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
 */
package net.minecraft.client.gui;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ITabCompleter;
import net.minecraft.util.TabCompleter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.wolframclient.Wolfram;
import net.wolframclient.gui.screen.chat.WolframChatLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiChat
extends GuiScreen
implements ITabCompleter {
    private static final Logger LOGGER = LogManager.getLogger();
    private String historyBuffer = "";
    protected int sentHistoryCursor = -1;
    protected TabCompleter tabCompleter;
    protected GuiTextField inputField;
    protected String defaultInputFieldText = "";

    public GuiChat() {
    }

    public GuiChat(String defaultText) {
        this.defaultInputFieldText = defaultText;
    }

    @Override
    public void initGui() {
        this.mc.displayGuiScreen(new WolframChatLine(this.defaultInputFieldText));
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents((boolean)false);
        this.mc.ingameGUI.getChatGUI().resetScroll();
    }

    @Override
    public void updateScreen() {
        this.inputField.updateCursorCounter();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.tabCompleter.resetRequested();
        if (keyCode == 15) {
            this.tabCompleter.complete();
        } else {
            this.tabCompleter.resetDidComplete();
        }
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        } else if (keyCode != 28 && keyCode != 156) {
            if (keyCode == 200) {
                this.getSentHistory(-1);
            } else if (keyCode == 208) {
                this.getSentHistory(1);
            } else if (keyCode == 201) {
                this.mc.ingameGUI.getChatGUI().scroll(this.mc.ingameGUI.getChatGUI().getLineCount() - 1);
            } else if (keyCode == 209) {
                this.mc.ingameGUI.getChatGUI().scroll(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1);
            } else {
                this.inputField.textboxKeyTyped(typedChar, keyCode);
            }
        } else {
            String s = this.inputField.getText().trim();
            if (!s.isEmpty()) {
                this.sendChatMessage(s);
            }
            if (Wolfram.getWolfram().getClientSettings().getBoolean("chat_autoclose")) {
                this.mc.displayGuiScreen(null);
            } else {
                this.inputField.setText("");
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();
        if (i != 0) {
            if (i > 1) {
                i = 1;
            }
            if (i < -1) {
                i = -1;
            }
            if (!GuiChat.isShiftKeyDown()) {
                i *= 7;
            }
            this.mc.ingameGUI.getChatGUI().scroll(i);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ITextComponent itextcomponent;
        if (mouseButton == 0 && (itextcomponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY())) != null && this.handleComponentClick(itextcomponent)) {
            return;
        }
        this.mc.ingameGUI.getChatGUI().mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void setText(String newChatText, boolean shouldOverwrite) {
        if (shouldOverwrite) {
            this.inputField.setText(newChatText);
        } else {
            this.inputField.writeText(newChatText);
        }
    }

    public void getSentHistory(int msgPos) {
        int i = this.sentHistoryCursor + msgPos;
        int j = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        if ((i = MathHelper.clamp(i, 0, j)) != this.sentHistoryCursor) {
            if (i == j) {
                this.sentHistoryCursor = j;
                this.inputField.setText(this.historyBuffer);
            } else {
                if (this.sentHistoryCursor == j) {
                    this.historyBuffer = this.inputField.getText();
                }
                this.inputField.setText(this.mc.ingameGUI.getChatGUI().getSentMessages().get(i));
                this.sentHistoryCursor = i;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void setCompletions(String ... newCompletions) {
        this.tabCompleter.setCompletions(newCompletions);
    }

    public static class ChatTabCompleter
    extends TabCompleter {
        private final Minecraft clientInstance = Minecraft.getMinecraft();

        public ChatTabCompleter(GuiTextField p_i46749_1_) {
            super(p_i46749_1_, false);
        }

        @Override
        public void complete() {
            super.complete();
            if (this.completions.size() > 1) {
                StringBuilder stringbuilder = new StringBuilder();
                for (String s : this.completions) {
                    if (stringbuilder.length() > 0) {
                        stringbuilder.append(", ");
                    }
                    stringbuilder.append(s);
                }
                this.clientInstance.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(stringbuilder.toString()), 1);
            }
        }

        @Override
        @Nullable
        public BlockPos getTargetBlockPos() {
            BlockPos blockpos = null;
            if (this.clientInstance.objectMouseOver != null && this.clientInstance.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                blockpos = this.clientInstance.objectMouseOver.getBlockPos();
            }
            return blockpos;
        }
    }
}

