/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class RecipeRepairItem
implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ArrayList list = Lists.newArrayList();
        int i = 0;
        while (i < inv.getSizeInventory()) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.func_190926_b()) {
                list.add(itemstack);
                if (list.size() > 1) {
                    ItemStack itemstack1 = (ItemStack)list.get(0);
                    if (itemstack.getItem() != itemstack1.getItem() || itemstack1.func_190916_E() != 1 || itemstack.func_190916_E() != 1 || !itemstack1.getItem().isDamageable()) {
                        return false;
                    }
                }
            }
            ++i;
        }
        return list.size() == 2;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ArrayList list = Lists.newArrayList();
        int i = 0;
        while (i < inv.getSizeInventory()) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.func_190926_b()) {
                list.add(itemstack);
                if (list.size() > 1) {
                    ItemStack itemstack1 = (ItemStack)list.get(0);
                    if (itemstack.getItem() != itemstack1.getItem() || itemstack1.func_190916_E() != 1 || itemstack.func_190916_E() != 1 || !itemstack1.getItem().isDamageable()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            ++i;
        }
        if (list.size() == 2) {
            ItemStack itemstack2 = (ItemStack)list.get(0);
            ItemStack itemstack3 = (ItemStack)list.get(1);
            if (itemstack2.getItem() == itemstack3.getItem() && itemstack2.func_190916_E() == 1 && itemstack3.func_190916_E() == 1 && itemstack2.getItem().isDamageable()) {
                Item item = itemstack2.getItem();
                int j = item.getMaxDamage() - itemstack2.getItemDamage();
                int k = item.getMaxDamage() - itemstack3.getItemDamage();
                int l = j + k + item.getMaxDamage() * 5 / 100;
                int i1 = item.getMaxDamage() - l;
                if (i1 < 0) {
                    i1 = 0;
                }
                return new ItemStack(itemstack2.getItem(), 1, i1);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.func_191197_a(inv.getSizeInventory(), ItemStack.EMPTY);
        int i = 0;
        while (i < nonnulllist.size()) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (itemstack.getItem().hasContainerItem()) {
                nonnulllist.set(i, new ItemStack(itemstack.getItem().getContainerItem()));
            }
            ++i;
        }
        return nonnulllist;
    }

    @Override
    public boolean func_192399_d() {
        return true;
    }

    @Override
    public boolean func_194133_a(int p_194133_1_, int p_194133_2_) {
        return p_194133_1_ * p_194133_2_ >= 2;
    }
}

