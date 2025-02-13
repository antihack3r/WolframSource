/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai;

import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class EntityAIVillagerInteract
extends EntityAIWatchClosest2 {
    private int interactionDelay;
    private final EntityVillager villager;

    public EntityAIVillagerInteract(EntityVillager villagerIn) {
        super(villagerIn, EntityVillager.class, 3.0f, 0.02f);
        this.villager = villagerIn;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        this.interactionDelay = this.villager.canAbondonItems() && this.closestEntity instanceof EntityVillager && ((EntityVillager)this.closestEntity).wantsMoreFood() ? 10 : 0;
    }

    @Override
    public void updateTask() {
        super.updateTask();
        if (this.interactionDelay > 0) {
            --this.interactionDelay;
            if (this.interactionDelay == 0) {
                InventoryBasic inventorybasic = this.villager.getVillagerInventory();
                int i = 0;
                while (i < inventorybasic.getSizeInventory()) {
                    ItemStack itemstack = inventorybasic.getStackInSlot(i);
                    ItemStack itemstack1 = ItemStack.EMPTY;
                    if (!itemstack.func_190926_b()) {
                        Item item = itemstack.getItem();
                        if ((item == Items.BREAD || item == Items.POTATO || item == Items.CARROT || item == Items.BEETROOT) && itemstack.func_190916_E() > 3) {
                            int l = itemstack.func_190916_E() / 2;
                            itemstack.func_190918_g(l);
                            itemstack1 = new ItemStack(item, l, itemstack.getMetadata());
                        } else if (item == Items.WHEAT && itemstack.func_190916_E() > 5) {
                            int j = itemstack.func_190916_E() / 2 / 3 * 3;
                            int k = j / 3;
                            itemstack.func_190918_g(j);
                            itemstack1 = new ItemStack(Items.BREAD, k, 0);
                        }
                        if (itemstack.func_190926_b()) {
                            inventorybasic.setInventorySlotContents(i, ItemStack.EMPTY);
                        }
                    }
                    if (!itemstack1.func_190926_b()) {
                        double d0 = this.villager.posY - (double)0.3f + (double)this.villager.getEyeHeight();
                        EntityItem entityitem = new EntityItem(this.villager.world, this.villager.posX, d0, this.villager.posZ, itemstack1);
                        float f = 0.3f;
                        float f1 = this.villager.rotationYawHead;
                        float f2 = this.villager.rotationPitch;
                        entityitem.motionX = -MathHelper.sin(f1 * ((float)Math.PI / 180)) * MathHelper.cos(f2 * ((float)Math.PI / 180)) * 0.3f;
                        entityitem.motionZ = MathHelper.cos(f1 * ((float)Math.PI / 180)) * MathHelper.cos(f2 * ((float)Math.PI / 180)) * 0.3f;
                        entityitem.motionY = -MathHelper.sin(f2 * ((float)Math.PI / 180)) * 0.3f + 0.1f;
                        entityitem.setDefaultPickupDelay();
                        this.villager.world.spawnEntityInWorld(entityitem);
                        break;
                    }
                    ++i;
                }
            }
        }
    }
}

