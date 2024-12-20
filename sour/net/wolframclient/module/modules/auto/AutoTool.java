/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.auto;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.BlockPos;
import net.wolframclient.compatibility.WBlock;
import net.wolframclient.compatibility.WEnchantments;
import net.wolframclient.compatibility.WItem;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerDamageBlockEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class AutoTool
extends ModuleListener {
    public AutoTool() {
        super("AutoTool", Module.Category.AUTO, "Always use the best tool when breaking blocks");
    }

    @EventTarget
    public void onPlayerDamageBlock(PlayerDamageBlockEvent event) {
        this.equipBestTool(event.getPos(), true, true);
    }

    public void equipBestTool(BlockPos pos, boolean useSwords, boolean useHands) {
        EntityPlayerSP player = WMinecraft.getPlayer();
        if (player.capabilities.isCreativeMode) {
            return;
        }
        IBlockState state = WBlock.getState(pos);
        ItemStack heldItem = player.getHeldItemMainhand();
        float bestSpeed = this.getDestroySpeed(heldItem, state);
        int bestSlot = -1;
        boolean useFallback = useHands && this.isDamageable(heldItem);
        int fallbackSlot = -1;
        int slot = 0;
        while (slot < 9) {
            if (slot != player.inventory.currentItem) {
                float speed;
                ItemStack stack = player.inventory.getStackInSlot(slot);
                if (fallbackSlot == -1 && !this.isDamageable(stack)) {
                    fallbackSlot = slot;
                }
                if (!((speed = this.getDestroySpeed(stack, state)) <= bestSpeed || !useSwords && stack.getItem() instanceof ItemSword)) {
                    bestSpeed = speed;
                    bestSlot = slot;
                }
            }
            ++slot;
        }
        if (bestSlot != -1) {
            player.inventory.currentItem = bestSlot;
        } else if (useFallback && bestSpeed <= 1.0f && fallbackSlot != -1) {
            player.inventory.currentItem = fallbackSlot;
        }
    }

    private float getDestroySpeed(ItemStack stack, IBlockState state) {
        int efficiency;
        float speed = WItem.getDestroySpeed(stack, state);
        if (speed > 1.0f && (efficiency = WEnchantments.getEnchantmentLevel(WEnchantments.EFFICIENCY, stack)) > 0 && !WItem.isNullOrEmpty(stack)) {
            speed += (float)(efficiency * efficiency + 1);
        }
        return speed;
    }

    private boolean isDamageable(ItemStack stack) {
        return !WItem.isNullOrEmpty(stack) && stack.getItem().isDamageable();
    }
}

