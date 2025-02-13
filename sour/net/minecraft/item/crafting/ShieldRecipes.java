/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ShieldRecipes {

    public static class Decoration
    implements IRecipe {
        @Override
        public boolean matches(InventoryCrafting inv, World worldIn) {
            ItemStack itemstack = ItemStack.EMPTY;
            ItemStack itemstack1 = ItemStack.EMPTY;
            int i = 0;
            while (i < inv.getSizeInventory()) {
                ItemStack itemstack2 = inv.getStackInSlot(i);
                if (!itemstack2.func_190926_b()) {
                    if (itemstack2.getItem() == Items.BANNER) {
                        if (!itemstack1.func_190926_b()) {
                            return false;
                        }
                        itemstack1 = itemstack2;
                    } else {
                        if (itemstack2.getItem() != Items.SHIELD) {
                            return false;
                        }
                        if (!itemstack.func_190926_b()) {
                            return false;
                        }
                        if (itemstack2.getSubCompound("BlockEntityTag") != null) {
                            return false;
                        }
                        itemstack = itemstack2;
                    }
                }
                ++i;
            }
            return !itemstack.func_190926_b() && !itemstack1.func_190926_b();
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            ItemStack itemstack = ItemStack.EMPTY;
            ItemStack itemstack1 = ItemStack.EMPTY;
            int i = 0;
            while (i < inv.getSizeInventory()) {
                ItemStack itemstack2 = inv.getStackInSlot(i);
                if (!itemstack2.func_190926_b()) {
                    if (itemstack2.getItem() == Items.BANNER) {
                        itemstack = itemstack2;
                    } else if (itemstack2.getItem() == Items.SHIELD) {
                        itemstack1 = itemstack2.copy();
                    }
                }
                ++i;
            }
            if (itemstack1.func_190926_b()) {
                return itemstack1;
            }
            NBTTagCompound nbttagcompound = itemstack.getSubCompound("BlockEntityTag");
            NBTTagCompound nbttagcompound1 = nbttagcompound == null ? new NBTTagCompound() : nbttagcompound.copy();
            nbttagcompound1.setInteger("Base", itemstack.getMetadata() & 0xF);
            itemstack1.setTagInfo("BlockEntityTag", nbttagcompound1);
            return itemstack1;
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
}

