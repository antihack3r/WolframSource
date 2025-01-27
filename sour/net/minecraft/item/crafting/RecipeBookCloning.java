/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class RecipeBookCloning
implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int i = 0;
        ItemStack itemstack = ItemStack.EMPTY;
        int j = 0;
        while (j < inv.getSizeInventory()) {
            ItemStack itemstack1 = inv.getStackInSlot(j);
            if (!itemstack1.func_190926_b()) {
                if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
                    if (!itemstack.func_190926_b()) {
                        return false;
                    }
                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                        return false;
                    }
                    ++i;
                }
            }
            ++j;
        }
        return !itemstack.func_190926_b() && itemstack.hasTagCompound() && i > 0;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        int i = 0;
        ItemStack itemstack = ItemStack.EMPTY;
        int j = 0;
        while (j < inv.getSizeInventory()) {
            ItemStack itemstack1 = inv.getStackInSlot(j);
            if (!itemstack1.func_190926_b()) {
                if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
                    if (!itemstack.func_190926_b()) {
                        return ItemStack.EMPTY;
                    }
                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                        return ItemStack.EMPTY;
                    }
                    ++i;
                }
            }
            ++j;
        }
        if (!itemstack.func_190926_b() && itemstack.hasTagCompound() && i >= 1 && ItemWrittenBook.getGeneration(itemstack) < 2) {
            ItemStack itemstack2 = new ItemStack(Items.WRITTEN_BOOK, i);
            itemstack2.setTagCompound(itemstack.getTagCompound().copy());
            itemstack2.getTagCompound().setInteger("generation", ItemWrittenBook.getGeneration(itemstack) + 1);
            if (itemstack.hasDisplayName()) {
                itemstack2.setStackDisplayName(itemstack.getDisplayName());
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
            if (itemstack.getItem() instanceof ItemWrittenBook) {
                ItemStack itemstack1 = itemstack.copy();
                itemstack1.func_190920_e(1);
                nonnulllist.set(i, itemstack1);
                break;
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

