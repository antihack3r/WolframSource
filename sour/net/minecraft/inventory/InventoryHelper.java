/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InventoryHelper {
    private static final Random RANDOM = new Random();

    public static void dropInventoryItems(World worldIn, BlockPos pos, IInventory inventory) {
        InventoryHelper.dropInventoryItems(worldIn, pos.getX(), pos.getY(), pos.getZ(), inventory);
    }

    public static void dropInventoryItems(World worldIn, Entity entityAt, IInventory inventory) {
        InventoryHelper.dropInventoryItems(worldIn, entityAt.posX, entityAt.posY, entityAt.posZ, inventory);
    }

    private static void dropInventoryItems(World worldIn, double x, double y, double z, IInventory inventory) {
        int i = 0;
        while (i < inventory.getSizeInventory()) {
            ItemStack itemstack = inventory.getStackInSlot(i);
            if (!itemstack.func_190926_b()) {
                InventoryHelper.spawnItemStack(worldIn, x, y, z, itemstack);
            }
            ++i;
        }
    }

    public static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack) {
        float f = RANDOM.nextFloat() * 0.8f + 0.1f;
        float f1 = RANDOM.nextFloat() * 0.8f + 0.1f;
        float f2 = RANDOM.nextFloat() * 0.8f + 0.1f;
        while (!stack.func_190926_b()) {
            EntityItem entityitem = new EntityItem(worldIn, x + (double)f, y + (double)f1, z + (double)f2, stack.splitStack(RANDOM.nextInt(21) + 10));
            float f3 = 0.05f;
            entityitem.motionX = RANDOM.nextGaussian() * (double)0.05f;
            entityitem.motionY = RANDOM.nextGaussian() * (double)0.05f + (double)0.2f;
            entityitem.motionZ = RANDOM.nextGaussian() * (double)0.05f;
            worldIn.spawnEntityInWorld(entityitem);
        }
    }
}
