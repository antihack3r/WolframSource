/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.utils.RenderUtils;

public class GuiScreenOptionsSounds
extends GuiScreen {
    private final GuiScreen parent;
    private final GameSettings game_settings_4;
    protected String title = "Options";
    private String offDisplayString;

    public GuiScreenOptionsSounds(GuiScreen parentIn, GameSettings settingsIn) {
        this.parent = parentIn;
        this.game_settings_4 = settingsIn;
    }

    @Override
    public void initGui() {
        this.title = I18n.format("options.sounds.title", new Object[0]);
        this.offDisplayString = I18n.format("options.off", new Object[0]);
        int i = 0;
        this.buttonList.add(new Button(SoundCategory.MASTER.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.MASTER, true));
        i += 2;
        SoundCategory[] soundCategoryArray = SoundCategory.values();
        int n = soundCategoryArray.length;
        int n2 = 0;
        while (n2 < n) {
            SoundCategory soundcategory = soundCategoryArray[n2];
            if (soundcategory != SoundCategory.MASTER) {
                this.buttonList.add(new Button(soundcategory.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundcategory, false));
                ++i;
            }
            ++n2;
        }
        int j = this.width / 2 - 75;
        int k = this.height / 6 - 12;
        this.buttonList.add(new GuiOptionButton(201, j, k + 24 * (++i >> 1), GameSettings.Options.SHOW_SUBTITLES, this.game_settings_4.getKeyBinding(GameSettings.Options.SHOW_SUBTITLES)));
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done", new Object[0])));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.gameSettings.saveOptions();
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parent);
            } else if (button.id == 201) {
                this.mc.gameSettings.setOptionValue(GameSettings.Options.SHOW_SUBTITLES, 1);
                button.displayString = this.mc.gameSettings.getKeyBinding(GameSettings.Options.SHOW_SUBTITLES);
                this.mc.gameSettings.saveOptions();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected String getDisplayString(SoundCategory category) {
        float f = this.game_settings_4.getSoundLevel(category);
        return f == 0.0f ? this.offDisplayString : String.valueOf((int)(f * 100.0f)) + "%";
    }

    class Button
    extends GuiButton {
        private final SoundCategory category;
        private final String categoryName;
        public float volume;
        public boolean pressed;

        public Button(int p_i46744_2_, int x, int y, SoundCategory categoryIn, boolean master) {
            super(p_i46744_2_, x, y, master ? 310 : 150, 20, "");
            this.volume = 1.0f;
            this.category = categoryIn;
            this.categoryName = I18n.format("soundCategory." + categoryIn.getName(), new Object[0]);
            this.displayString = String.valueOf(this.categoryName) + ": " + GuiScreenOptionsSounds.this.getDisplayString(categoryIn);
            this.volume = GuiScreenOptionsSounds.this.game_settings_4.getSoundLevel(categoryIn);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int var5 = this.getHoverState(this.hovered);
            this.anim.update(this.hovered);
            int color = this.enabled ? -263693982 : 1883789666;
            this.mouseDragged(mc, mouseX, mouseY);
            FontRenderers.BIG.drawCenteredString(this.displayString, this.xPosition + this.width / 2, this.yPosition + 5, color, false);
            if (this.enabled) {
                RenderUtils.drawLine2D((double)(this.xPosition + this.width / 2) - this.anim.getValue() * (double)this.width / 2.0, this.yPosition, (double)(this.xPosition + this.width / 2) + this.anim.getValue() * (double)this.width / 2.0, this.yPosition, 1.0f, 1346918754);
                RenderUtils.drawLine2D((double)(this.xPosition + this.width / 2) - this.anim.getValue() * (double)this.width / 2.0, this.yPosition + this.height, (double)(this.xPosition + this.width / 2) + this.anim.getValue() * (double)this.width / 2.0, this.yPosition + this.height, 1.0f, 1346918754);
                RenderUtils.drawLine2D((double)(this.xPosition + this.width / 2) - this.anim.getValue() * (double)this.width / 2.0 + (double)((int)((double)this.volume * (this.anim.getValue() * (double)this.width - 8.0))), this.yPosition, (double)(this.xPosition + this.width / 2) - this.anim.getValue() * (double)this.width / 2.0 + (double)((int)((double)this.volume * (this.anim.getValue() * (double)this.width - 8.0))) + 8.0, this.yPosition, 1.0f, 21518690 + 0x1000000 * (int)(239.0 * this.anim.getValue()));
                RenderUtils.drawLine2D((double)(this.xPosition + this.width / 2) - this.anim.getValue() * (double)this.width / 2.0 + (double)((int)((double)this.volume * (this.anim.getValue() * (double)this.width - 8.0))), this.yPosition + this.height, (double)(this.xPosition + this.width / 2) - this.anim.getValue() * (double)this.width / 2.0 + (double)((int)((double)this.volume * (this.anim.getValue() * (double)this.width - 8.0))) + 8.0, this.yPosition + this.height, 1.0f, 21518690 + 0x1000000 * (int)(239.0 * this.anim.getValue()));
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        }

        @Override
        protected int getHoverState(boolean mouseOver) {
            return 0;
        }

        @Override
        protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible && this.pressed) {
                this.volume = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                this.volume = MathHelper.clamp(this.volume, 0.0f, 1.0f);
                mc.gameSettings.setSoundLevel(this.category, this.volume);
                mc.gameSettings.saveOptions();
                this.displayString = String.valueOf(this.categoryName) + ": " + GuiScreenOptionsSounds.this.getDisplayString(this.category);
            }
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            if (super.mousePressed(mc, mouseX, mouseY)) {
                this.volume = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                this.volume = MathHelper.clamp(this.volume, 0.0f, 1.0f);
                mc.gameSettings.setSoundLevel(this.category, this.volume);
                mc.gameSettings.saveOptions();
                this.displayString = String.valueOf(this.categoryName) + ": " + GuiScreenOptionsSounds.this.getDisplayString(this.category);
                this.pressed = true;
                return true;
            }
            return false;
        }

        @Override
        public void playPressSound(SoundHandler soundHandlerIn) {
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY) {
            if (this.pressed) {
                GuiScreenOptionsSounds.this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            this.pressed = false;
        }
    }
}

