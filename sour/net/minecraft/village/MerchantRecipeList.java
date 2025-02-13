/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.village;

import java.io.IOException;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.village.MerchantRecipe;

public class MerchantRecipeList
extends ArrayList<MerchantRecipe> {
    public MerchantRecipeList() {
    }

    public MerchantRecipeList(NBTTagCompound compound) {
        this.readRecipiesFromTags(compound);
    }

    @Nullable
    public MerchantRecipe canRecipeBeUsed(ItemStack p_77203_1_, ItemStack p_77203_2_, int p_77203_3_) {
        if (p_77203_3_ > 0 && p_77203_3_ < this.size()) {
            MerchantRecipe merchantrecipe1 = (MerchantRecipe)this.get(p_77203_3_);
            return !this.areItemStacksExactlyEqual(p_77203_1_, merchantrecipe1.getItemToBuy()) || (!p_77203_2_.func_190926_b() || merchantrecipe1.hasSecondItemToBuy()) && (!merchantrecipe1.hasSecondItemToBuy() || !this.areItemStacksExactlyEqual(p_77203_2_, merchantrecipe1.getSecondItemToBuy())) || p_77203_1_.func_190916_E() < merchantrecipe1.getItemToBuy().func_190916_E() || merchantrecipe1.hasSecondItemToBuy() && p_77203_2_.func_190916_E() < merchantrecipe1.getSecondItemToBuy().func_190916_E() ? null : merchantrecipe1;
        }
        int i = 0;
        while (i < this.size()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe)this.get(i);
            if (this.areItemStacksExactlyEqual(p_77203_1_, merchantrecipe.getItemToBuy()) && p_77203_1_.func_190916_E() >= merchantrecipe.getItemToBuy().func_190916_E() && (!merchantrecipe.hasSecondItemToBuy() && p_77203_2_.func_190926_b() || merchantrecipe.hasSecondItemToBuy() && this.areItemStacksExactlyEqual(p_77203_2_, merchantrecipe.getSecondItemToBuy()) && p_77203_2_.func_190916_E() >= merchantrecipe.getSecondItemToBuy().func_190916_E())) {
                return merchantrecipe;
            }
            ++i;
        }
        return null;
    }

    private boolean areItemStacksExactlyEqual(ItemStack stack1, ItemStack stack2) {
        return ItemStack.areItemsEqual(stack1, stack2) && (!stack2.hasTagCompound() || stack1.hasTagCompound() && NBTUtil.areNBTEquals(stack2.getTagCompound(), stack1.getTagCompound(), false));
    }

    public void writeToBuf(PacketBuffer buffer) {
        buffer.writeByte((byte)(this.size() & 0xFF));
        int i = 0;
        while (i < this.size()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe)this.get(i);
            buffer.writeItemStackToBuffer(merchantrecipe.getItemToBuy());
            buffer.writeItemStackToBuffer(merchantrecipe.getItemToSell());
            ItemStack itemstack = merchantrecipe.getSecondItemToBuy();
            buffer.writeBoolean(!itemstack.func_190926_b());
            if (!itemstack.func_190926_b()) {
                buffer.writeItemStackToBuffer(itemstack);
            }
            buffer.writeBoolean(merchantrecipe.isRecipeDisabled());
            buffer.writeInt(merchantrecipe.getToolUses());
            buffer.writeInt(merchantrecipe.getMaxTradeUses());
            ++i;
        }
    }

    public static MerchantRecipeList readFromBuf(PacketBuffer buffer) throws IOException {
        MerchantRecipeList merchantrecipelist = new MerchantRecipeList();
        int i = buffer.readByte() & 0xFF;
        int j = 0;
        while (j < i) {
            ItemStack itemstack = buffer.readItemStackFromBuffer();
            ItemStack itemstack1 = buffer.readItemStackFromBuffer();
            ItemStack itemstack2 = ItemStack.EMPTY;
            if (buffer.readBoolean()) {
                itemstack2 = buffer.readItemStackFromBuffer();
            }
            boolean flag = buffer.readBoolean();
            int k = buffer.readInt();
            int l = buffer.readInt();
            MerchantRecipe merchantrecipe = new MerchantRecipe(itemstack, itemstack2, itemstack1, k, l);
            if (flag) {
                merchantrecipe.compensateToolUses();
            }
            merchantrecipelist.add(merchantrecipe);
            ++j;
        }
        return merchantrecipelist;
    }

    public void readRecipiesFromTags(NBTTagCompound compound) {
        NBTTagList nbttaglist = compound.getTagList("Recipes", 10);
        int i = 0;
        while (i < nbttaglist.tagCount()) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            this.add(new MerchantRecipe(nbttagcompound));
            ++i;
        }
    }

    public NBTTagCompound getRecipiesAsTags() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();
        int i = 0;
        while (i < this.size()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe)this.get(i);
            nbttaglist.appendTag(merchantrecipe.writeToTags());
            ++i;
        }
        nbttagcompound.setTag("Recipes", nbttaglist);
        return nbttagcompound;
    }
}

