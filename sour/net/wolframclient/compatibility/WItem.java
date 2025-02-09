/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.compatibility;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public final class WItem {
    public static boolean isNullOrEmpty(Item item) {
        return item == null || item instanceof ItemAir;
    }

    public static boolean isNullOrEmpty(ItemStack stack) {
        return stack == null || stack.func_190926_b();
    }

    public static int getArmorType(ItemArmor armor) {
        return armor.armorType.ordinal() - 2;
    }

    public static float getArmorToughness(ItemArmor armor) {
        return armor.toughness;
    }

    public static float getDestroySpeed(ItemStack stack, IBlockState state) {
        return WItem.isNullOrEmpty(stack) ? 1.0f : stack.getStrVsBlock(state);
    }
}

