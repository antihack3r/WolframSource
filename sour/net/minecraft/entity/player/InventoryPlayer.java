/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.player;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ReportedException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class InventoryPlayer
implements IInventory {
    public final NonNullList<ItemStack> mainInventory = NonNullList.func_191197_a(36, ItemStack.EMPTY);
    public final NonNullList<ItemStack> armorInventory = NonNullList.func_191197_a(4, ItemStack.EMPTY);
    public final NonNullList<ItemStack> offHandInventory = NonNullList.func_191197_a(1, ItemStack.EMPTY);
    private final List<NonNullList<ItemStack>> allInventories = Arrays.asList(this.mainInventory, this.armorInventory, this.offHandInventory);
    public int currentItem;
    public EntityPlayer player;
    private ItemStack itemStack = ItemStack.EMPTY;
    private int field_194017_h;

    public InventoryPlayer(EntityPlayer playerIn) {
        this.player = playerIn;
    }

    public ItemStack getCurrentItem() {
        return InventoryPlayer.isHotbar(this.currentItem) ? this.mainInventory.get(this.currentItem) : ItemStack.EMPTY;
    }

    public static int getHotbarSize() {
        return 9;
    }

    private boolean canMergeStacks(ItemStack stack1, ItemStack stack2) {
        return !stack1.func_190926_b() && this.stackEqualExact(stack1, stack2) && stack1.isStackable() && stack1.func_190916_E() < stack1.getMaxStackSize() && stack1.func_190916_E() < this.getInventoryStackLimit();
    }

    private boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    public int getFirstEmptyStack() {
        int i = 0;
        while (i < this.mainInventory.size()) {
            if (this.mainInventory.get(i).func_190926_b()) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public void setPickedItemStack(ItemStack stack) {
        int i = this.getSlotFor(stack);
        if (InventoryPlayer.isHotbar(i)) {
            this.currentItem = i;
        } else if (i == -1) {
            int j;
            this.currentItem = this.getBestHotbarSlot();
            if (!this.mainInventory.get(this.currentItem).func_190926_b() && (j = this.getFirstEmptyStack()) != -1) {
                this.mainInventory.set(j, this.mainInventory.get(this.currentItem));
            }
            this.mainInventory.set(this.currentItem, stack);
        } else {
            this.pickItem(i);
        }
    }

    public void pickItem(int index) {
        this.currentItem = this.getBestHotbarSlot();
        ItemStack itemstack = this.mainInventory.get(this.currentItem);
        this.mainInventory.set(this.currentItem, this.mainInventory.get(index));
        this.mainInventory.set(index, itemstack);
    }

    public static boolean isHotbar(int index) {
        return index >= 0 && index < 9;
    }

    public int getSlotFor(ItemStack stack) {
        int i = 0;
        while (i < this.mainInventory.size()) {
            if (!this.mainInventory.get(i).func_190926_b() && this.stackEqualExact(stack, this.mainInventory.get(i))) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public int func_194014_c(ItemStack p_194014_1_) {
        int i = 0;
        while (i < this.mainInventory.size()) {
            ItemStack itemstack = this.mainInventory.get(i);
            if (!(this.mainInventory.get(i).func_190926_b() || !this.stackEqualExact(p_194014_1_, this.mainInventory.get(i)) || this.mainInventory.get(i).isItemDamaged() || itemstack.isItemEnchanted() || itemstack.hasDisplayName())) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public int getBestHotbarSlot() {
        int i = 0;
        while (i < 9) {
            int j = (this.currentItem + i) % 9;
            if (this.mainInventory.get(j).func_190926_b()) {
                return j;
            }
            ++i;
        }
        int k = 0;
        while (k < 9) {
            int l = (this.currentItem + k) % 9;
            if (!this.mainInventory.get(l).isItemEnchanted()) {
                return l;
            }
            ++k;
        }
        return this.currentItem;
    }

    public void changeCurrentItem(int direction) {
        if (direction > 0) {
            direction = 1;
        }
        if (direction < 0) {
            direction = -1;
        }
        this.currentItem -= direction;
        while (this.currentItem < 0) {
            this.currentItem += 9;
        }
        while (this.currentItem >= 9) {
            this.currentItem -= 9;
        }
    }

    public int clearMatchingItems(@Nullable Item itemIn, int metadataIn, int removeCount, @Nullable NBTTagCompound itemNBT) {
        int i = 0;
        int j = 0;
        while (j < this.getSizeInventory()) {
            ItemStack itemstack = this.getStackInSlot(j);
            if (!(itemstack.func_190926_b() || itemIn != null && itemstack.getItem() != itemIn || metadataIn > -1 && itemstack.getMetadata() != metadataIn || itemNBT != null && !NBTUtil.areNBTEquals(itemNBT, itemstack.getTagCompound(), true))) {
                int k = removeCount <= 0 ? itemstack.func_190916_E() : Math.min(removeCount - i, itemstack.func_190916_E());
                i += k;
                if (removeCount != 0) {
                    itemstack.func_190918_g(k);
                    if (itemstack.func_190926_b()) {
                        this.setInventorySlotContents(j, ItemStack.EMPTY);
                    }
                    if (removeCount > 0 && i >= removeCount) {
                        return i;
                    }
                }
            }
            ++j;
        }
        if (!this.itemStack.func_190926_b()) {
            if (itemIn != null && this.itemStack.getItem() != itemIn) {
                return i;
            }
            if (metadataIn > -1 && this.itemStack.getMetadata() != metadataIn) {
                return i;
            }
            if (itemNBT != null && !NBTUtil.areNBTEquals(itemNBT, this.itemStack.getTagCompound(), true)) {
                return i;
            }
            int l = removeCount <= 0 ? this.itemStack.func_190916_E() : Math.min(removeCount - i, this.itemStack.func_190916_E());
            i += l;
            if (removeCount != 0) {
                this.itemStack.func_190918_g(l);
                if (this.itemStack.func_190926_b()) {
                    this.itemStack = ItemStack.EMPTY;
                }
                if (removeCount > 0 && i >= removeCount) {
                    return i;
                }
            }
        }
        return i;
    }

    private int storePartialItemStack(ItemStack itemStackIn) {
        int i = this.storeItemStack(itemStackIn);
        if (i == -1) {
            i = this.getFirstEmptyStack();
        }
        return i == -1 ? itemStackIn.func_190916_E() : this.func_191973_d(i, itemStackIn);
    }

    private int func_191973_d(int p_191973_1_, ItemStack p_191973_2_) {
        Item item = p_191973_2_.getItem();
        int i = p_191973_2_.func_190916_E();
        ItemStack itemstack = this.getStackInSlot(p_191973_1_);
        if (itemstack.func_190926_b()) {
            itemstack = new ItemStack(item, 0, p_191973_2_.getMetadata());
            if (p_191973_2_.hasTagCompound()) {
                itemstack.setTagCompound(p_191973_2_.getTagCompound().copy());
            }
            this.setInventorySlotContents(p_191973_1_, itemstack);
        }
        int j = i;
        if (i > itemstack.getMaxStackSize() - itemstack.func_190916_E()) {
            j = itemstack.getMaxStackSize() - itemstack.func_190916_E();
        }
        if (j > this.getInventoryStackLimit() - itemstack.func_190916_E()) {
            j = this.getInventoryStackLimit() - itemstack.func_190916_E();
        }
        if (j == 0) {
            return i;
        }
        itemstack.func_190917_f(j);
        itemstack.func_190915_d(5);
        return i -= j;
    }

    public int storeItemStack(ItemStack itemStackIn) {
        if (this.canMergeStacks(this.getStackInSlot(this.currentItem), itemStackIn)) {
            return this.currentItem;
        }
        if (this.canMergeStacks(this.getStackInSlot(40), itemStackIn)) {
            return 40;
        }
        int i = 0;
        while (i < this.mainInventory.size()) {
            if (this.canMergeStacks(this.mainInventory.get(i), itemStackIn)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public void decrementAnimations() {
        for (NonNullList<ItemStack> nonnulllist : this.allInventories) {
            int i = 0;
            while (i < nonnulllist.size()) {
                if (!nonnulllist.get(i).func_190926_b()) {
                    nonnulllist.get(i).updateAnimation(this.player.world, this.player, i, this.currentItem == i);
                }
                ++i;
            }
        }
    }

    public boolean addItemStackToInventory(ItemStack itemStackIn) {
        return this.func_191971_c(-1, itemStackIn);
    }

    public boolean func_191971_c(int p_191971_1_, final ItemStack p_191971_2_) {
        int i;
        block12: {
            block10: {
                block11: {
                    if (p_191971_2_.func_190926_b()) {
                        return false;
                    }
                    if (!p_191971_2_.isItemDamaged()) break block10;
                    if (p_191971_1_ == -1) {
                        p_191971_1_ = this.getFirstEmptyStack();
                    }
                    if (p_191971_1_ < 0) break block11;
                    this.mainInventory.set(p_191971_1_, p_191971_2_.copy());
                    this.mainInventory.get(p_191971_1_).func_190915_d(5);
                    p_191971_2_.func_190920_e(0);
                    return true;
                }
                if (this.player.capabilities.isCreativeMode) {
                    p_191971_2_.func_190920_e(0);
                    return true;
                }
                return false;
            }
            try {
                do {
                    i = p_191971_2_.func_190916_E();
                    if (p_191971_1_ == -1) {
                        p_191971_2_.func_190920_e(this.storePartialItemStack(p_191971_2_));
                        continue;
                    }
                    p_191971_2_.func_190920_e(this.func_191973_d(p_191971_1_, p_191971_2_));
                } while (!p_191971_2_.func_190926_b() && p_191971_2_.func_190916_E() < i);
                if (p_191971_2_.func_190916_E() != i || !this.player.capabilities.isCreativeMode) break block12;
                p_191971_2_.func_190920_e(0);
                return true;
            }
            catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                crashreportcategory.addCrashSection("Item ID", Item.getIdFromItem(p_191971_2_.getItem()));
                crashreportcategory.addCrashSection("Item data", p_191971_2_.getMetadata());
                crashreportcategory.setDetail("Item name", new ICrashReportDetail<String>(){

                    @Override
                    public String call() throws Exception {
                        return p_191971_2_.getDisplayName();
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
        return p_191971_2_.func_190916_E() < i;
    }

    public void func_191975_a(World p_191975_1_, ItemStack p_191975_2_) {
        if (!p_191975_1_.isRemote) {
            while (!p_191975_2_.func_190926_b()) {
                int i = this.storeItemStack(p_191975_2_);
                if (i == -1) {
                    i = this.getFirstEmptyStack();
                }
                if (i == -1) {
                    this.player.dropItem(p_191975_2_, false);
                    break;
                }
                int j = p_191975_2_.getMaxStackSize() - this.getStackInSlot(i).func_190916_E();
                if (!this.func_191971_c(i, p_191975_2_.splitStack(j))) continue;
                ((EntityPlayerMP)this.player).connection.sendPacket(new SPacketSetSlot(-2, i, this.getStackInSlot(i)));
            }
        }
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        NonNullList<ItemStack> list = null;
        for (NonNullList<ItemStack> nonnulllist : this.allInventories) {
            if (index < nonnulllist.size()) {
                list = nonnulllist;
                break;
            }
            index -= nonnulllist.size();
        }
        return list != null && !((ItemStack)list.get(index)).func_190926_b() ? ItemStackHelper.getAndSplit(list, index, count) : ItemStack.EMPTY;
    }

    public void deleteStack(ItemStack stack) {
        block0: for (NonNullList<ItemStack> nonnulllist : this.allInventories) {
            int i = 0;
            while (i < nonnulllist.size()) {
                if (nonnulllist.get(i) == stack) {
                    nonnulllist.set(i, ItemStack.EMPTY);
                    continue block0;
                }
                ++i;
            }
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        NonNullList<ItemStack> nonnulllist = null;
        for (NonNullList<ItemStack> nonnulllist1 : this.allInventories) {
            if (index < nonnulllist1.size()) {
                nonnulllist = nonnulllist1;
                break;
            }
            index -= nonnulllist1.size();
        }
        if (nonnulllist != null && !((ItemStack)nonnulllist.get(index)).func_190926_b()) {
            ItemStack itemstack = nonnulllist.get(index);
            nonnulllist.set(index, ItemStack.EMPTY);
            return itemstack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        NonNullList<ItemStack> nonnulllist = null;
        for (NonNullList<ItemStack> nonnulllist1 : this.allInventories) {
            if (index < nonnulllist1.size()) {
                nonnulllist = nonnulllist1;
                break;
            }
            index -= nonnulllist1.size();
        }
        if (nonnulllist != null) {
            nonnulllist.set(index, stack);
        }
    }

    public float getStrVsBlock(IBlockState state) {
        float f = 1.0f;
        if (!this.mainInventory.get(this.currentItem).func_190926_b()) {
            f *= this.mainInventory.get(this.currentItem).getStrVsBlock(state);
        }
        return f;
    }

    public NBTTagList writeToNBT(NBTTagList nbtTagListIn) {
        int i = 0;
        while (i < this.mainInventory.size()) {
            if (!this.mainInventory.get(i).func_190926_b()) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                this.mainInventory.get(i).writeToNBT(nbttagcompound);
                nbtTagListIn.appendTag(nbttagcompound);
            }
            ++i;
        }
        int j = 0;
        while (j < this.armorInventory.size()) {
            if (!this.armorInventory.get(j).func_190926_b()) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)(j + 100));
                this.armorInventory.get(j).writeToNBT(nbttagcompound1);
                nbtTagListIn.appendTag(nbttagcompound1);
            }
            ++j;
        }
        int k = 0;
        while (k < this.offHandInventory.size()) {
            if (!this.offHandInventory.get(k).func_190926_b()) {
                NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                nbttagcompound2.setByte("Slot", (byte)(k + 150));
                this.offHandInventory.get(k).writeToNBT(nbttagcompound2);
                nbtTagListIn.appendTag(nbttagcompound2);
            }
            ++k;
        }
        return nbtTagListIn;
    }

    public void readFromNBT(NBTTagList nbtTagListIn) {
        this.mainInventory.clear();
        this.armorInventory.clear();
        this.offHandInventory.clear();
        int i = 0;
        while (i < nbtTagListIn.tagCount()) {
            NBTTagCompound nbttagcompound = nbtTagListIn.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 0xFF;
            ItemStack itemstack = new ItemStack(nbttagcompound);
            if (!itemstack.func_190926_b()) {
                if (j >= 0 && j < this.mainInventory.size()) {
                    this.mainInventory.set(j, itemstack);
                } else if (j >= 100 && j < this.armorInventory.size() + 100) {
                    this.armorInventory.set(j - 100, itemstack);
                } else if (j >= 150 && j < this.offHandInventory.size() + 150) {
                    this.offHandInventory.set(j - 150, itemstack);
                }
            }
            ++i;
        }
    }

    @Override
    public int getSizeInventory() {
        return this.mainInventory.size() + this.armorInventory.size() + this.offHandInventory.size();
    }

    @Override
    public boolean func_191420_l() {
        for (ItemStack itemstack : this.mainInventory) {
            if (itemstack.func_190926_b()) continue;
            return false;
        }
        for (ItemStack itemstack1 : this.armorInventory) {
            if (itemstack1.func_190926_b()) continue;
            return false;
        }
        for (ItemStack itemstack2 : this.offHandInventory) {
            if (itemstack2.func_190926_b()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        NonNullList<ItemStack> list = null;
        for (NonNullList<ItemStack> nonnulllist : this.allInventories) {
            if (index < nonnulllist.size()) {
                list = nonnulllist;
                break;
            }
            index -= nonnulllist.size();
        }
        return list == null ? ItemStack.EMPTY : (ItemStack)list.get(index);
    }

    @Override
    public String getName() {
        return "container.inventory";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean canHarvestBlock(IBlockState state) {
        if (state.getMaterial().isToolNotRequired()) {
            return true;
        }
        ItemStack itemstack = this.getStackInSlot(this.currentItem);
        return !itemstack.func_190926_b() ? itemstack.canHarvestBlock(state) : false;
    }

    public ItemStack armorItemInSlot(int slotIn) {
        return this.armorInventory.get(slotIn);
    }

    public void damageArmor(float damage) {
        if ((damage /= 4.0f) < 1.0f) {
            damage = 1.0f;
        }
        int i = 0;
        while (i < this.armorInventory.size()) {
            ItemStack itemstack = this.armorInventory.get(i);
            if (itemstack.getItem() instanceof ItemArmor) {
                itemstack.damageItem((int)damage, this.player);
            }
            ++i;
        }
    }

    public void dropAllItems() {
        for (List list : this.allInventories) {
            int i = 0;
            while (i < list.size()) {
                ItemStack itemstack = (ItemStack)list.get(i);
                if (!itemstack.func_190926_b()) {
                    this.player.dropItem(itemstack, true, false);
                    list.set(i, ItemStack.EMPTY);
                }
                ++i;
            }
        }
    }

    @Override
    public void markDirty() {
        ++this.field_194017_h;
    }

    public int func_194015_p() {
        return this.field_194017_h;
    }

    public void setItemStack(ItemStack itemStackIn) {
        this.itemStack = itemStackIn;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.player.isDead) {
            return false;
        }
        return player.getDistanceSqToEntity(this.player) <= 64.0;
    }

    public boolean hasItemStack(ItemStack itemStackIn) {
        for (List list : this.allInventories) {
            for (ItemStack itemstack : list) {
                if (itemstack.func_190926_b() || !itemstack.isItemEqual(itemStackIn)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    public void copyInventory(InventoryPlayer playerInventory) {
        int i = 0;
        while (i < this.getSizeInventory()) {
            this.setInventorySlotContents(i, playerInventory.getStackInSlot(i));
            ++i;
        }
        this.currentItem = playerInventory.currentItem;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (List list : this.allInventories) {
            list.clear();
        }
    }

    public void func_194016_a(RecipeItemHelper p_194016_1_, boolean p_194016_2_) {
        for (ItemStack itemstack : this.mainInventory) {
            p_194016_1_.func_194112_a(itemstack);
        }
        if (p_194016_2_) {
            p_194016_1_.func_194112_a(this.offHandInventory.get(0));
        }
    }
}

