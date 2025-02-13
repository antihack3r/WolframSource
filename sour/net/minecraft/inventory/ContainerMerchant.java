/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotMerchantResult;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerMerchant
extends Container {
    private final IMerchant theMerchant;
    private final InventoryMerchant merchantInventory;
    private final World theWorld;

    public ContainerMerchant(InventoryPlayer playerInventory, IMerchant merchant, World worldIn) {
        this.theMerchant = merchant;
        this.theWorld = worldIn;
        this.merchantInventory = new InventoryMerchant(playerInventory.player, merchant);
        this.addSlotToContainer(new Slot(this.merchantInventory, 0, 36, 53));
        this.addSlotToContainer(new Slot(this.merchantInventory, 1, 62, 53));
        this.addSlotToContainer(new SlotMerchantResult(playerInventory.player, merchant, this.merchantInventory, 2, 120, 53));
        int i = 0;
        while (i < 3) {
            int j = 0;
            while (j < 9) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
                ++j;
            }
            ++i;
        }
        int k = 0;
        while (k < 9) {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
            ++k;
        }
    }

    public InventoryMerchant getMerchantInventory() {
        return this.merchantInventory;
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        this.merchantInventory.resetRecipeAndSlots();
        super.onCraftMatrixChanged(inventoryIn);
    }

    public void setCurrentRecipeIndex(int currentRecipeIndex) {
        this.merchantInventory.setCurrentRecipeIndex(currentRecipeIndex);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.theMerchant.getCustomer() == playerIn;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 2) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 0 && index != 1 ? (index >= 3 && index < 30 ? !this.mergeItemStack(itemstack1, 30, 39, false) : index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) : !this.mergeItemStack(itemstack1, 3, 39, false)) {
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
            slot.func_190901_a(playerIn, itemstack1);
        }
        return itemstack;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        this.theMerchant.setCustomer(null);
        super.onContainerClosed(playerIn);
        if (!this.theWorld.isRemote) {
            ItemStack itemstack = this.merchantInventory.removeStackFromSlot(0);
            if (!itemstack.func_190926_b()) {
                playerIn.dropItem(itemstack, false);
            }
            if (!(itemstack = this.merchantInventory.removeStackFromSlot(1)).func_190926_b()) {
                playerIn.dropItem(itemstack, false);
            }
        }
    }
}

