/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIHarvestFarmland
extends EntityAIMoveToBlock {
    private final EntityVillager theVillager;
    private boolean hasFarmItem;
    private boolean wantsToReapStuff;
    private int currentTask;

    public EntityAIHarvestFarmland(EntityVillager theVillagerIn, double speedIn) {
        super(theVillagerIn, speedIn, 16);
        this.theVillager = theVillagerIn;
    }

    @Override
    public boolean shouldExecute() {
        if (this.runDelay <= 0) {
            if (!this.theVillager.world.getGameRules().getBoolean("mobGriefing")) {
                return false;
            }
            this.currentTask = -1;
            this.hasFarmItem = this.theVillager.isFarmItemInInventory();
            this.wantsToReapStuff = this.theVillager.wantsMoreFood();
        }
        return super.shouldExecute();
    }

    @Override
    public boolean continueExecuting() {
        return this.currentTask >= 0 && super.continueExecuting();
    }

    @Override
    public void updateTask() {
        super.updateTask();
        this.theVillager.getLookHelper().setLookPosition((double)this.destinationBlock.getX() + 0.5, this.destinationBlock.getY() + 1, (double)this.destinationBlock.getZ() + 0.5, 10.0f, this.theVillager.getVerticalFaceSpeed());
        if (this.getIsAboveDestination()) {
            World world = this.theVillager.world;
            BlockPos blockpos = this.destinationBlock.up();
            IBlockState iblockstate = world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            if (this.currentTask == 0 && block instanceof BlockCrops && ((BlockCrops)block).isMaxAge(iblockstate)) {
                world.destroyBlock(blockpos, true);
            } else if (this.currentTask == 1 && iblockstate.getMaterial() == Material.AIR) {
                InventoryBasic inventorybasic = this.theVillager.getVillagerInventory();
                int i = 0;
                while (i < inventorybasic.getSizeInventory()) {
                    ItemStack itemstack = inventorybasic.getStackInSlot(i);
                    boolean flag = false;
                    if (!itemstack.func_190926_b()) {
                        if (itemstack.getItem() == Items.WHEAT_SEEDS) {
                            world.setBlockState(blockpos, Blocks.WHEAT.getDefaultState(), 3);
                            flag = true;
                        } else if (itemstack.getItem() == Items.POTATO) {
                            world.setBlockState(blockpos, Blocks.POTATOES.getDefaultState(), 3);
                            flag = true;
                        } else if (itemstack.getItem() == Items.CARROT) {
                            world.setBlockState(blockpos, Blocks.CARROTS.getDefaultState(), 3);
                            flag = true;
                        } else if (itemstack.getItem() == Items.BEETROOT_SEEDS) {
                            world.setBlockState(blockpos, Blocks.BEETROOTS.getDefaultState(), 3);
                            flag = true;
                        }
                    }
                    if (flag) {
                        itemstack.func_190918_g(1);
                        if (!itemstack.func_190926_b()) break;
                        inventorybasic.setInventorySlotContents(i, ItemStack.EMPTY);
                        break;
                    }
                    ++i;
                }
            }
            this.currentTask = -1;
            this.runDelay = 10;
        }
    }

    @Override
    protected boolean shouldMoveTo(World worldIn, BlockPos pos) {
        Block block = worldIn.getBlockState(pos).getBlock();
        if (block == Blocks.FARMLAND) {
            IBlockState iblockstate = worldIn.getBlockState(pos = pos.up());
            block = iblockstate.getBlock();
            if (block instanceof BlockCrops && ((BlockCrops)block).isMaxAge(iblockstate) && this.wantsToReapStuff && (this.currentTask == 0 || this.currentTask < 0)) {
                this.currentTask = 0;
                return true;
            }
            if (iblockstate.getMaterial() == Material.AIR && this.hasFarmItem && (this.currentTask == 1 || this.currentTask < 0)) {
                this.currentTask = 1;
                return true;
            }
        }
        return false;
    }
}

