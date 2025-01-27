/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class RecipesMapCloning
implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int i = 0;
        ItemStack itemstack = ItemStack.EMPTY;
        int j = 0;
        while (j < inv.getSizeInventory()) {
            ItemStack itemstack1 = inv.getStackInSlot(j);
            if (!itemstack1.func_190926_b()) {
                if (itemstack1.getItem() == Items.FILLED_MAP) {
                    if (!itemstack.func_190926_b()) {
                        return false;
                    }
                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.MAP) {
                        return false;
                    }
                    ++i;
                }
            }
            ++j;
        }
        return !itemstack.func_190926_b() && i > 0;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        int i = 0;
        ItemStack itemstack = ItemStack.EMPTY;
        int j = 0;
        while (j < inv.getSizeInventory()) {
            ItemStack itemstack1 = inv.getStackInSlot(j);
            if (!itemstack1.func_190926_b()) {
                if (itemstack1.getItem() == Items.FILLED_MAP) {
                    if (!itemstack.func_190926_b()) {
                        return ItemStack.EMPTY;
                    }
                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.MAP) {
                        return ItemStack.EMPTY;
                    }
                    ++i;
                }
            }
            ++j;
        }
        if (!itemstack.func_190926_b() && i >= 1) {
            ItemStack itemstack2 = new ItemStack(Items.FILLED_MAP, i + 1, itemstack.getMetadata());
            if (itemstack.hasDisplayName()) {
                itemstack2.setStackDisplayName(itemstack.getDisplayName());
            }
            if (itemstack.hasTagCompound()) {
                itemstack2.setTagCompound(itemstack.getTagCompound());
            }
            return itemstack2;
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
        return p_194133_1_ >= 3 && p_194133_2_ >= 3;
    }
}

