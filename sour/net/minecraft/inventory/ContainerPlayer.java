/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.wolframclient.Wolfram;

public class ContainerPlayer
extends Container {
    private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public InventoryCraftResult craftResult = new InventoryCraftResult();
    public boolean isLocalWorld;
    private final EntityPlayer thePlayer;

    public ContainerPlayer(InventoryPlayer playerInventory, boolean localWorld, EntityPlayer player) {
        this.isLocalWorld = localWorld;
        this.thePlayer = player;
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 154, 28));
        int i = 0;
        while (i < 2) {
            int j = 0;
            while (j < 2) {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 98 + j * 18, 18 + i * 18));
                ++j;
            }
            ++i;
        }
        int k = 0;
        while (k < 4) {
            final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
            this.addSlotToContainer(new Slot(playerInventory, 36 + (3 - k), 8, 8 + k * 18){

                @Override
                public int getSlotStackLimit() {
                    return 1;
                }

                @Override
                public boolean isItemValid(ItemStack stack) {
                    return entityequipmentslot == EntityLiving.getSlotForItemStack(stack);
                }

                @Override
                public boolean canTakeStack(EntityPlayer playerIn) {
                    ItemStack itemstack = this.getStack();
                    return !itemstack.func_190926_b() && !playerIn.isCreative() && EnchantmentHelper.func_190938_b(itemstack) ? false : super.canTakeStack(playerIn);
                }

                @Override
                @Nullable
                public String getSlotTexture() {
                    return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
                }
            });
            ++k;
        }
        int l = 0;
        while (l < 3) {
            int j1 = 0;
            while (j1 < 9) {
                this.addSlotToContainer(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
                ++j1;
            }
            ++l;
        }
        int i1 = 0;
        while (i1 < 9) {
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
            ++i1;
        }
        this.addSlotToContainer(new Slot(playerInventory, 40, 77, 62){

            @Override
            @Nullable
            public String getSlotTexture() {
                return "minecraft:items/empty_armor_slot_shield";
            }
        });
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        this.func_192389_a(this.thePlayer.world, this.thePlayer, this.craftMatrix, this.craftResult);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        if (Wolfram.getWolfram().moduleManager.isEnabled("moreinventory")) {
            return;
        }
        super.onContainerClosed(playerIn);
        this.craftResult.clear();
        if (!playerIn.world.isRemote) {
            this.func_193327_a(playerIn, playerIn.world, this.craftMatrix);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            int i;
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
            if (index == 0) {
                if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index >= 1 && index < 5 ? !this.mergeItemStack(itemstack1, 9, 45, false) : (index >= 5 && index < 9 ? !this.mergeItemStack(itemstack1, 9, 45, false) : (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !((Slot)this.inventorySlots.get(8 - entityequipmentslot.getIndex())).getHasStack() ? !this.mergeItemStack(itemstack1, i = 8 - entityequipmentslot.getIndex(), i + 1, false) : (entityequipmentslot == EntityEquipmentSlot.OFFHAND && !((Slot)this.inventorySlots.get(45)).getHasStack() ? !this.mergeItemStack(itemstack1, 45, 46, false) : (index >= 9 && index < 36 ? !this.mergeItemStack(itemstack1, 36, 45, false) : (index >= 36 && index < 45 ? !this.mergeItemStack(itemstack1, 9, 36, false) : !this.mergeItemStack(itemstack1, 9, 45, false))))))) {
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

