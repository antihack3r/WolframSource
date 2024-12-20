/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.utils;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.wolframclient.compatibility.WMinecraft;

public class InventoryUtils {
    private static final EntityPlayerSP player = WMinecraft.getPlayer();

    public static int getBestHotbarSword() {
        float best = -1.0f;
        int index = -1;
        int i = 0;
        while (i < 9) {
            ItemStack itemStack = InventoryUtils.player.inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() instanceof ItemSword) {
                ItemSword sword = (ItemSword)itemStack.getItem();
                float damage = sword.attackDamage + (float)EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) * 1.25f;
                if (damage > best) {
                    best = damage;
                    index = i;
                }
            }
            ++i;
        }
        return index;
    }
}

