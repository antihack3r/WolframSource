/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.toasts;

import com.google.common.collect.Queues;
import java.util.Arrays;
import java.util.Deque;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.MathHelper;

public class GuiToast
extends Gui {
    private final Minecraft field_191790_f;
    private final ToastInstance<?>[] field_191791_g = new ToastInstance[5];
    private final Deque<IToast> field_191792_h = Queues.newArrayDeque();

    public GuiToast(Minecraft p_i47388_1_) {
        this.field_191790_f = p_i47388_1_;
    }

    public void func_191783_a(ScaledResolution p_191783_1_) {
        if (!this.field_191790_f.gameSettings.hideGUI) {
            RenderHelper.disableStandardItemLighting();
            int i = 0;
            while (i < this.field_191791_g.length) {
                ToastInstance<?> toastinstance = this.field_191791_g[i];
                if (toastinstance != null && toastinstance.func_193684_a(p_191783_1_.getScaledWidth(), i)) {
                    this.field_191791_g[i] = null;
                }
                if (this.field_191791_g[i] == null && !this.field_191792_h.isEmpty()) {
                    this.field_191791_g[i] = new ToastInstance(this, this.field_191792_h.removeFirst());
                }
                ++i;
            }
        }
    }

    @Nullable
    public <T extends IToast> T func_192990_a(Class<? extends T> p_192990_1_, Object p_192990_2_) {
        ToastInstance<?>[] toastInstanceArray = this.field_191791_g;
        int n = this.field_191791_g.length;
        int n2 = 0;
        while (n2 < n) {
            ToastInstance<?> toastinstance = toastInstanceArray[n2];
            if (toastinstance != null && p_192990_1_.isAssignableFrom(toastinstance.func_193685_a().getClass()) && ((IToast)toastinstance.func_193685_a()).func_193652_b().equals(p_192990_2_)) {
                return (T)toastinstance.func_193685_a();
            }
            ++n2;
        }
        for (IToast itoast : this.field_191792_h) {
            if (!p_192990_1_.isAssignableFrom(itoast.getClass()) || !itoast.func_193652_b().equals(p_192990_2_)) continue;
            return (T)itoast;
        }
        return null;
    }

    public void func_191788_b() {
        Arrays.fill(this.field_191791_g, null);
        this.field_191792_h.clear();
    }

    public void func_192988_a(IToast p_192988_1_) {
        this.field_191792_h.add(p_192988_1_);
    }

    public Minecraft func_192989_b() {
        return this.field_191790_f;
    }

    static class ToastInstance<T extends IToast> {
        private final T field_193688_b;
        private long field_193689_c = -1L;
        private long field_193690_d = -1L;
        private IToast.Visibility field_193691_e = IToast.Visibility.SHOW;
        final /* synthetic */ GuiToast this$0;

        private ToastInstance(T p_i47483_2_) {
            this.this$0 = var1_1;
            this.field_193688_b = p_i47483_2_;
        }

        public T func_193685_a() {
            return this.field_193688_b;
        }

        private float func_193686_a(long p_193686_1_) {
            float f = MathHelper.clamp((float)(p_193686_1_ - this.field_193689_c) / 600.0f, 0.0f, 1.0f);
            f *= f;
            return this.field_193691_e == IToast.Visibility.HIDE ? 1.0f - f : f;
        }

        public boolean func_193684_a(int p_193684_1_, int p_193684_2_) {
            long i = Minecraft.getSystemTime();
            if (this.field_193689_c == -1L) {
                this.field_193689_c = i;
                this.field_193691_e.func_194169_a(this.this$0.field_191790_f.getSoundHandler());
            }
            if (this.field_193691_e == IToast.Visibility.SHOW && i - this.field_193689_c <= 600L) {
                this.field_193690_d = i;
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)p_193684_1_ - 160.0f * this.func_193686_a(i), p_193684_2_ * 32, 500 + p_193684_2_);
            IToast.Visibility itoast$visibility = this.field_193688_b.func_193653_a(this.this$0, i - this.field_193690_d);
            GlStateManager.popMatrix();
            if (itoast$visibility != this.field_193691_e) {
                this.field_193689_c = i - (long)((int)((1.0f - this.func_193686_a(i)) * 600.0f));
                this.field_193691_e = itoast$visibility;
                this.field_193691_e.func_194169_a(this.this$0.field_191790_f.getSoundHandler());
            }
            return this.field_193691_e == IToast.Visibility.HIDE && i - this.field_193689_c > 600L;
        }
    }
}

