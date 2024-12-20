/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.src;

import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.IOptionControl;

public class GuiOptionButtonOF
extends GuiOptionButton
implements IOptionControl {
    private GameSettings.Options option = null;

    public GuiOptionButtonOF(int p_i46_1_, int p_i46_2_, int p_i46_3_, GameSettings.Options p_i46_4_, String p_i46_5_) {
        super(p_i46_1_, p_i46_2_, p_i46_3_, p_i46_4_, p_i46_5_);
        this.option = p_i46_4_;
    }

    @Override
    public GameSettings.Options getOption() {
        return this.option;
    }
}

