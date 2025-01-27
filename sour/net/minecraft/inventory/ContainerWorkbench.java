/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerWorkbench
extends Container {
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public InventoryCraftResult craftResult = new InventoryCraftResult();
    private final World worldObj;
    private final BlockPos pos;
    private final EntityPlayer field_192390_i;

    public ContainerWorkbench(InventoryPlayer playerInventory, World worldIn, BlockPos posIn) {
        this.worldObj = worldIn;
        this.pos = posIn;
        this.field_192390_i = playerInventory.player;
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));
        int i = 0;
        while (i < 3) {
            int j = 0;
            while (j < 3) {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
                ++j;
            }
            ++i;
        }
        int k = 0;
        while (k < 3) {
            int i1 = 0;
            while (i1 < 9) {
                this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
                ++i1;
            }
            ++k;
        }
        int l = 0;
        while (l < 9) {
            this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
            ++l;
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        this.func_192389_a(this.worldObj, this.field_192390_i, this.craftMatrix, this.craftResult);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!this.worldObj.isRemote) {
            this.func_193327_a(playerIn, this.worldObj, this.craftMatrix);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        if (this.worldObj.getBlockState(this.pos).getBlock() != Blocks.CRAFTING_TABLE) {
            return false;
        }
        return playerIn.getDistanceSq((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 0) {
                itemstack1.getItem().onCreated(itemstack1, this.worldObj, playerIn);
                if (!this.mergeItemStack(itemstack1, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index >= 10 && index < 37 ? !this.mergeItemStack(itemstack1, 37, 46, false) : (index >= 37 && index < 46 ? !this.mergeItemStack(itemstack1, 10, 37, false) : !this.mergeItemStack(itemstack1, 10, 46, false))) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.func_190926_b()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (itemstack1.func_190916_E() == itemstack.func_190916_E()) {
                return ItemStack.EMPTY;
            }
            ItemStack itemstack2 = slot.func_190901_a(playerIn, itemstack1);
            if (index == 0) {
                playerIn.dropItem(itemstack2, false);
            }
        }
        return itemstack;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }
}

