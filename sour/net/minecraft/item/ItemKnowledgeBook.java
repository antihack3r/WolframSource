/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemKnowledgeBook
extends Item {
    private static final Logger field_194126_a = LogManager.getLogger();

    public ItemKnowledgeBook() {
        this.setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World itemStackIn, EntityPlayer worldIn, EnumHand playerIn) {
        ItemStack itemstack = worldIn.getHeldItem(playerIn);
        NBTTagCompound nbttagcompound = itemstack.getTagCompound();
        if (!worldIn.capabilities.isCreativeMode) {
            worldIn.setHeldItem(playerIn, ItemStack.EMPTY);
        }
        if (nbttagcompound != null && nbttagcompound.hasKey("Recipes", 9)) {
            if (!itemStackIn.isRemote) {
                NBTTagList nbttaglist = nbttagcompound.getTagList("Recipes", 8);
                ArrayList list = Lists.newArrayList();
                int i = 0;
                while (i < nbttaglist.tagCount()) {
                    String s = nbttaglist.getStringTagAt(i);
                    IRecipe irecipe = CraftingManager.func_193373_a(new ResourceLocation(s));
                    if (irecipe == null) {
                        field_194126_a.error("Invalid recipe: " + s);
                        return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
                    }
                    list.add(irecipe);
                    ++i;
                }
                worldIn.func_192021_a(list);
                worldIn.addStat(StatList.getObjectUseStats(this));
            }
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        }
        field_194126_a.error("Tag not valid: " + nbttagcompound);
        return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
    }
}

