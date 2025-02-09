/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.auto;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.wolframclient.compatibility.WItem;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class AutoEat
extends ModuleListener {
    private int oldSlot;
    private int bestSlot;

    public AutoEat() {
        super("AutoEat", Module.Category.PLAYER, "Eat food automatically");
    }

    @Override
    protected void onEnable2() {
        this.oldSlot = -1;
        this.bestSlot = -1;
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (this.oldSlot == -1) {
            if (!this.canEat()) {
                return;
            }
            float bestSaturation = 0.0f;
            int i = 0;
            while (i < 9) {
                ItemFood food;
                float saturation;
                ItemStack stack = WMinecraft.getPlayer().inventory.getStackInSlot(i);
                if (this.isFood(stack) && (saturation = (food = (ItemFood)stack.getItem()).getSaturationModifier(stack)) > bestSaturation) {
                    bestSaturation = saturation;
                    this.bestSlot = i;
                }
                ++i;
            }
            if (this.bestSlot != -1) {
                this.oldSlot = WMinecraft.getPlayer().inventory.currentItem;
            }
        } else {
            if (!this.canEat()) {
                this.stop();
                return;
            }
            if (!this.isFood(WMinecraft.getPlayer().inventory.getStackInSlot(this.bestSlot))) {
                this.stop();
                return;
            }
            WMinecraft.getPlayer().inventory.currentItem = this.bestSlot;
            Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed = true;
        }
    }

    private boolean canEat() {
        if (!WMinecraft.getPlayer().canEat(false)) {
            return false;
        }
        if (Minecraft.getMinecraft().objectMouseOver != null) {
            Block block;
            Entity entity = Minecraft.getMinecraft().objectMouseOver.entityHit;
            if (entity instanceof EntityVillager || entity instanceof EntityTameable) {
                return false;
            }
            BlockPos pos = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
            if (pos != null && ((block = WMinecraft.getWorld().getBlockState(pos).getBlock()) instanceof BlockContainer || block instanceof BlockWorkbench)) {
                return false;
            }
        }
        return true;
    }

    private boolean isFood(ItemStack stack) {
        return !WItem.isNullOrEmpty(stack) && stack.getItem() instanceof ItemFood;
    }

    private void stop() {
        Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed = false;
        WMinecraft.getPlayer().inventory.currentItem = this.oldSlot;
        this.oldSlot = -1;
    }
}

