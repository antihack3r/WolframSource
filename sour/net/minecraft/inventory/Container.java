/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class Container {
    public NonNullList<ItemStack> inventoryItemStacks = NonNullList.func_191196_a();
    public List<Slot> inventorySlots = Lists.newArrayList();
    public int windowId;
    private short transactionID;
    private int dragMode = -1;
    private int dragEvent;
    private final Set<Slot> dragSlots = Sets.newHashSet();
    protected List<IContainerListener> listeners = Lists.newArrayList();
    private final Set<EntityPlayer> playerList = Sets.newHashSet();

    protected Slot addSlotToContainer(Slot slotIn) {
        slotIn.slotNumber = this.inventorySlots.size();
        this.inventorySlots.add(slotIn);
        this.inventoryItemStacks.add(ItemStack.EMPTY);
        return slotIn;
    }

    public void addListener(IContainerListener listener) {
        if (this.listeners.contains(listener)) {
            throw new IllegalArgumentException("Listener already listening");
        }
        this.listeners.add(listener);
        listener.updateCraftingInventory(this, this.getInventory());
        this.detectAndSendChanges();
    }

    public void removeListener(IContainerListener listener) {
        this.listeners.remove(listener);
    }

    public NonNullList<ItemStack> getInventory() {
        NonNullList<ItemStack> nonnulllist = NonNullList.func_191196_a();
        int i = 0;
        while (i < this.inventorySlots.size()) {
            nonnulllist.add(this.inventorySlots.get(i).getStack());
            ++i;
        }
        return nonnulllist;
    }

    public void detectAndSendChanges() {
        int i = 0;
        while (i < this.inventorySlots.size()) {
            ItemStack itemstack = this.inventorySlots.get(i).getStack();
            ItemStack itemstack1 = this.inventoryItemStacks.get(i);
            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
                itemstack1 = itemstack.func_190926_b() ? ItemStack.EMPTY : itemstack.copy();
                this.inventoryItemStacks.set(i, itemstack1);
                int j = 0;
                while (j < this.listeners.size()) {
                    this.listeners.get(j).sendSlotContents(this, i, itemstack1);
                    ++j;
                }
            }
            ++i;
        }
    }

    public boolean enchantItem(EntityPlayer playerIn, int id) {
        return false;
    }

    @Nullable
    public Slot getSlotFromInventory(IInventory inv, int slotIn) {
        int i = 0;
        while (i < this.inventorySlots.size()) {
            Slot slot = this.inventorySlots.get(i);
            if (slot.isHere(inv, slotIn)) {
                return slot;
            }
            ++i;
        }
        return null;
    }

    public Slot getSlot(int slotId) {
        return this.inventorySlots.get(slotId);
    }

    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        Slot slot = this.inventorySlots.get(index);
        return slot != null ? slot.getStack() : ItemStack.EMPTY;
    }

    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        ItemStack itemstack = ItemStack.EMPTY;
        InventoryPlayer inventoryplayer = player.inventory;
        if (clickTypeIn == ClickType.QUICK_CRAFT) {
            int j1 = this.dragEvent;
            this.dragEvent = Container.getDragEvent(dragType);
            if ((j1 != 1 || this.dragEvent != 2) && j1 != this.dragEvent) {
                this.resetDrag();
            } else if (inventoryplayer.getItemStack().func_190926_b()) {
                this.resetDrag();
            } else if (this.dragEvent == 0) {
                this.dragMode = Container.extractDragMode(dragType);
                if (Container.isValidDragMode(this.dragMode, player)) {
                    this.dragEvent = 1;
                    this.dragSlots.clear();
                } else {
                    this.resetDrag();
                }
            } else if (this.dragEvent == 1) {
                Slot slot7 = this.inventorySlots.get(slotId);
                ItemStack itemstack12 = inventoryplayer.getItemStack();
                if (slot7 != null && Container.canAddItemToSlot(slot7, itemstack12, true) && slot7.isItemValid(itemstack12) && (this.dragMode == 2 || itemstack12.func_190916_E() > this.dragSlots.size()) && this.canDragIntoSlot(slot7)) {
                    this.dragSlots.add(slot7);
                }
            } else if (this.dragEvent == 2) {
                if (!this.dragSlots.isEmpty()) {
                    ItemStack itemstack9 = inventoryplayer.getItemStack().copy();
                    int k1 = inventoryplayer.getItemStack().func_190916_E();
                    for (Slot slot8 : this.dragSlots) {
                        ItemStack itemstack13 = inventoryplayer.getItemStack();
                        if (slot8 == null || !Container.canAddItemToSlot(slot8, itemstack13, true) || !slot8.isItemValid(itemstack13) || this.dragMode != 2 && itemstack13.func_190916_E() < this.dragSlots.size() || !this.canDragIntoSlot(slot8)) continue;
                        ItemStack itemstack14 = itemstack9.copy();
                        int j3 = slot8.getHasStack() ? slot8.getStack().func_190916_E() : 0;
                        Container.computeStackSize(this.dragSlots, this.dragMode, itemstack14, j3);
                        int k3 = Math.min(itemstack14.getMaxStackSize(), slot8.getItemStackLimit(itemstack14));
                        if (itemstack14.func_190916_E() > k3) {
                            itemstack14.func_190920_e(k3);
                        }
                        k1 -= itemstack14.func_190916_E() - j3;
                        slot8.putStack(itemstack14);
                    }
                    itemstack9.func_190920_e(k1);
                    inventoryplayer.setItemStack(itemstack9);
                }
                this.resetDrag();
            } else {
                this.resetDrag();
            }
        } else if (this.dragEvent != 0) {
            this.resetDrag();
        } else if (!(clickTypeIn != ClickType.PICKUP && clickTypeIn != ClickType.QUICK_MOVE || dragType != 0 && dragType != 1)) {
            if (slotId == -999) {
                if (!inventoryplayer.getItemStack().func_190926_b()) {
                    if (dragType == 0) {
                        player.dropItem(inventoryplayer.getItemStack(), true);
                        inventoryplayer.setItemStack(ItemStack.EMPTY);
                    }
                    if (dragType == 1) {
                        player.dropItem(inventoryplayer.getItemStack().splitStack(1), true);
                    }
                }
            } else if (clickTypeIn == ClickType.QUICK_MOVE) {
                if (slotId < 0) {
                    return ItemStack.EMPTY;
                }
                Slot slot5 = this.inventorySlots.get(slotId);
                if (slot5 == null || !slot5.canTakeStack(player)) {
                    return ItemStack.EMPTY;
                }
                ItemStack itemstack7 = this.transferStackInSlot(player, slotId);
                while (!itemstack7.func_190926_b() && ItemStack.areItemsEqual(slot5.getStack(), itemstack7)) {
                    itemstack = itemstack7.copy();
                    itemstack7 = this.transferStackInSlot(player, slotId);
                }
            } else {
                if (slotId < 0) {
                    return ItemStack.EMPTY;
                }
                Slot slot6 = this.inventorySlots.get(slotId);
                if (slot6 != null) {
                    ItemStack itemstack8 = slot6.getStack();
                    ItemStack itemstack11 = inventoryplayer.getItemStack();
                    if (!itemstack8.func_190926_b()) {
                        itemstack = itemstack8.copy();
                    }
                    if (itemstack8.func_190926_b()) {
                        if (!itemstack11.func_190926_b() && slot6.isItemValid(itemstack11)) {
                            int i3;
                            int n = i3 = dragType == 0 ? itemstack11.func_190916_E() : 1;
                            if (i3 > slot6.getItemStackLimit(itemstack11)) {
                                i3 = slot6.getItemStackLimit(itemstack11);
                            }
                            slot6.putStack(itemstack11.splitStack(i3));
                        }
                    } else if (slot6.canTakeStack(player)) {
                        int j2;
                        if (itemstack11.func_190926_b()) {
                            if (itemstack8.func_190926_b()) {
                                slot6.putStack(ItemStack.EMPTY);
                                inventoryplayer.setItemStack(ItemStack.EMPTY);
                            } else {
                                int l2 = dragType == 0 ? itemstack8.func_190916_E() : (itemstack8.func_190916_E() + 1) / 2;
                                inventoryplayer.setItemStack(slot6.decrStackSize(l2));
                                if (itemstack8.func_190926_b()) {
                                    slot6.putStack(ItemStack.EMPTY);
                                }
                                slot6.func_190901_a(player, inventoryplayer.getItemStack());
                            }
                        } else if (slot6.isItemValid(itemstack11)) {
                            if (itemstack8.getItem() == itemstack11.getItem() && itemstack8.getMetadata() == itemstack11.getMetadata() && ItemStack.areItemStackTagsEqual(itemstack8, itemstack11)) {
                                int k2;
                                int n = k2 = dragType == 0 ? itemstack11.func_190916_E() : 1;
                                if (k2 > slot6.getItemStackLimit(itemstack11) - itemstack8.func_190916_E()) {
                                    k2 = slot6.getItemStackLimit(itemstack11) - itemstack8.func_190916_E();
                                }
                                if (k2 > itemstack11.getMaxStackSize() - itemstack8.func_190916_E()) {
                                    k2 = itemstack11.getMaxStackSize() - itemstack8.func_190916_E();
                                }
                                itemstack11.func_190918_g(k2);
                                itemstack8.func_190917_f(k2);
                            } else if (itemstack11.func_190916_E() <= slot6.getItemStackLimit(itemstack11)) {
                                slot6.putStack(itemstack11);
                                inventoryplayer.setItemStack(itemstack8);
                            }
                        } else if (!(itemstack8.getItem() != itemstack11.getItem() || itemstack11.getMaxStackSize() <= 1 || itemstack8.getHasSubtypes() && itemstack8.getMetadata() != itemstack11.getMetadata() || !ItemStack.areItemStackTagsEqual(itemstack8, itemstack11) || itemstack8.func_190926_b() || (j2 = itemstack8.func_190916_E()) + itemstack11.func_190916_E() > itemstack11.getMaxStackSize())) {
                            itemstack11.func_190917_f(j2);
                            itemstack8 = slot6.decrStackSize(j2);
                            if (itemstack8.func_190926_b()) {
                                slot6.putStack(ItemStack.EMPTY);
                            }
                            slot6.func_190901_a(player, inventoryplayer.getItemStack());
                        }
                    }
                    slot6.onSlotChanged();
                }
            }
        } else if (clickTypeIn == ClickType.SWAP && dragType >= 0 && dragType < 9) {
            Slot slot4 = this.inventorySlots.get(slotId);
            ItemStack itemstack6 = inventoryplayer.getStackInSlot(dragType);
            ItemStack itemstack10 = slot4.getStack();
            if (!itemstack6.func_190926_b() || !itemstack10.func_190926_b()) {
                if (itemstack6.func_190926_b()) {
                    if (slot4.canTakeStack(player)) {
                        inventoryplayer.setInventorySlotContents(dragType, itemstack10);
                        slot4.func_190900_b(itemstack10.func_190916_E());
                        slot4.putStack(ItemStack.EMPTY);
                        slot4.func_190901_a(player, itemstack10);
                    }
                } else if (itemstack10.func_190926_b()) {
                    if (slot4.isItemValid(itemstack6)) {
                        int l1 = slot4.getItemStackLimit(itemstack6);
                        if (itemstack6.func_190916_E() > l1) {
                            slot4.putStack(itemstack6.splitStack(l1));
                        } else {
                            slot4.putStack(itemstack6);
                            inventoryplayer.setInventorySlotContents(dragType, ItemStack.EMPTY);
                        }
                    }
                } else if (slot4.canTakeStack(player) && slot4.isItemValid(itemstack6)) {
                    int i2 = slot4.getItemStackLimit(itemstack6);
                    if (itemstack6.func_190916_E() > i2) {
                        slot4.putStack(itemstack6.splitStack(i2));
                        slot4.func_190901_a(player, itemstack10);
                        if (!inventoryplayer.addItemStackToInventory(itemstack10)) {
                            player.dropItem(itemstack10, true);
                        }
                    } else {
                        slot4.putStack(itemstack6);
                        inventoryplayer.setInventorySlotContents(dragType, itemstack10);
                        slot4.func_190901_a(player, itemstack10);
                    }
                }
            }
        } else if (clickTypeIn == ClickType.CLONE && player.capabilities.isCreativeMode && inventoryplayer.getItemStack().func_190926_b() && slotId >= 0) {
            Slot slot3 = this.inventorySlots.get(slotId);
            if (slot3 != null && slot3.getHasStack()) {
                ItemStack itemstack5 = slot3.getStack().copy();
                itemstack5.func_190920_e(itemstack5.getMaxStackSize());
                inventoryplayer.setItemStack(itemstack5);
            }
        } else if (clickTypeIn == ClickType.THROW && inventoryplayer.getItemStack().func_190926_b() && slotId >= 0) {
            Slot slot2 = this.inventorySlots.get(slotId);
            if (slot2 != null && slot2.getHasStack() && slot2.canTakeStack(player)) {
                ItemStack itemstack4 = slot2.decrStackSize(dragType == 0 ? 1 : slot2.getStack().func_190916_E());
                slot2.func_190901_a(player, itemstack4);
                player.dropItem(itemstack4, true);
            }
        } else if (clickTypeIn == ClickType.PICKUP_ALL && slotId >= 0) {
            Slot slot = this.inventorySlots.get(slotId);
            ItemStack itemstack1 = inventoryplayer.getItemStack();
            if (!(itemstack1.func_190926_b() || slot != null && slot.getHasStack() && slot.canTakeStack(player))) {
                int i = dragType == 0 ? 0 : this.inventorySlots.size() - 1;
                int j = dragType == 0 ? 1 : -1;
                int k = 0;
                while (k < 2) {
                    int l = i;
                    while (l >= 0 && l < this.inventorySlots.size() && itemstack1.func_190916_E() < itemstack1.getMaxStackSize()) {
                        Slot slot1 = this.inventorySlots.get(l);
                        if (slot1.getHasStack() && Container.canAddItemToSlot(slot1, itemstack1, true) && slot1.canTakeStack(player) && this.canMergeSlot(itemstack1, slot1)) {
                            ItemStack itemstack2 = slot1.getStack();
                            if (k != 0 || itemstack2.func_190916_E() != itemstack2.getMaxStackSize()) {
                                int i1 = Math.min(itemstack1.getMaxStackSize() - itemstack1.func_190916_E(), itemstack2.func_190916_E());
                                ItemStack itemstack3 = slot1.decrStackSize(i1);
                                itemstack1.func_190917_f(i1);
                                if (itemstack3.func_190926_b()) {
                                    slot1.putStack(ItemStack.EMPTY);
                                }
                                slot1.func_190901_a(player, itemstack3);
                            }
                        }
                        l += j;
                    }
                    ++k;
                }
            }
            this.detectAndSendChanges();
        }
        return itemstack;
    }

    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return true;
    }

    public void onContainerClosed(EntityPlayer playerIn) {
        InventoryPlayer inventoryplayer = playerIn.inventory;
        if (!inventoryplayer.getItemStack().func_190926_b()) {
            playerIn.dropItem(inventoryplayer.getItemStack(), false);
            inventoryplayer.setItemStack(ItemStack.EMPTY);
        }
    }

    protected void func_193327_a(EntityPlayer p_193327_1_, World p_193327_2_, IInventory p_193327_3_) {
        if (!p_193327_1_.isEntityAlive() || p_193327_1_ instanceof EntityPlayerMP && ((EntityPlayerMP)p_193327_1_).func_193105_t()) {
            int j = 0;
            while (j < p_193327_3_.getSizeInventory()) {
                p_193327_1_.dropItem(p_193327_3_.removeStackFromSlot(j), false);
                ++j;
            }
        } else {
            int i = 0;
            while (i < p_193327_3_.getSizeInventory()) {
                p_193327_1_.inventory.func_191975_a(p_193327_2_, p_193327_3_.removeStackFromSlot(i));
                ++i;
            }
        }
    }

    public void onCraftMatrixChanged(IInventory inventoryIn) {
        this.detectAndSendChanges();
    }

    public void putStackInSlot(int slotID, ItemStack stack) {
        this.getSlot(slotID).putStack(stack);
    }

    public void func_190896_a(List<ItemStack> p_190896_1_) {
        int i = 0;
        while (i < p_190896_1_.size()) {
            this.getSlot(i).putStack(p_190896_1_.get(i));
            ++i;
        }
    }

    public void func_192388_b(int p_192388_1_, ItemStack p_192388_2_) {
        ItemStack itemstack = this.getSlot(p_192388_1_).getStack();
        if (itemstack.func_190926_b()) {
            this.putStackInSlot(p_192388_1_, p_192388_2_);
        } else if (itemstack.getUnlocalizedName().equals(p_192388_2_.getUnlocalizedName()) && itemstack.func_190916_E() < itemstack.getMaxStackSize()) {
            itemstack.func_190917_f(p_192388_2_.func_190916_E());
        }
    }

    public void updateProgressBar(int id, int data) {
    }

    public short getNextTransactionID(InventoryPlayer invPlayer) {
        this.transactionID = (short)(this.transactionID + 1);
        return this.transactionID;
    }

    public boolean getCanCraft(EntityPlayer player) {
        return !this.playerList.contains(player);
    }

    public void setCanCraft(EntityPlayer player, boolean canCraft) {
        if (canCraft) {
            this.playerList.remove(player);
        } else {
            this.playerList.add(player);
        }
    }

    public abstract boolean canInteractWith(EntityPlayer var1);

    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }
        if (stack.isStackable()) {
            while (!stack.func_190926_b()) {
                if (!reverseDirection ? i >= endIndex : i < startIndex) break;
                Slot slot = this.inventorySlots.get(i);
                ItemStack itemstack = slot.getStack();
                if (!(itemstack.func_190926_b() || itemstack.getItem() != stack.getItem() || stack.getHasSubtypes() && stack.getMetadata() != itemstack.getMetadata() || !ItemStack.areItemStackTagsEqual(stack, itemstack))) {
                    int j = itemstack.func_190916_E() + stack.func_190916_E();
                    if (j <= stack.getMaxStackSize()) {
                        stack.func_190920_e(0);
                        itemstack.func_190920_e(j);
                        slot.onSlotChanged();
                        flag = true;
                    } else if (itemstack.func_190916_E() < stack.getMaxStackSize()) {
                        stack.func_190918_g(stack.getMaxStackSize() - itemstack.func_190916_E());
                        itemstack.func_190920_e(stack.getMaxStackSize());
                        slot.onSlotChanged();
                        flag = true;
                    }
                }
                if (reverseDirection) {
                    --i;
                    continue;
                }
                ++i;
            }
        }
        if (!stack.func_190926_b()) {
            i = reverseDirection ? endIndex - 1 : startIndex;
            while (!(!reverseDirection ? i >= endIndex : i < startIndex)) {
                Slot slot1 = this.inventorySlots.get(i);
                ItemStack itemstack1 = slot1.getStack();
                if (itemstack1.func_190926_b() && slot1.isItemValid(stack)) {
                    if (stack.func_190916_E() > slot1.getSlotStackLimit()) {
                        slot1.putStack(stack.splitStack(slot1.getSlotStackLimit()));
                    } else {
                        slot1.putStack(stack.splitStack(stack.func_190916_E()));
                    }
                    slot1.onSlotChanged();
                    flag = true;
                    break;
                }
                if (reverseDirection) {
                    --i;
                    continue;
                }
                ++i;
            }
        }
        return flag;
    }

    public static int extractDragMode(int eventButton) {
        return eventButton >> 2 & 3;
    }

    public static int getDragEvent(int clickedButton) {
        return clickedButton & 3;
    }

    public static int getQuickcraftMask(int p_94534_0_, int p_94534_1_) {
        return p_94534_0_ & 3 | (p_94534_1_ & 3) << 2;
    }

    public static boolean isValidDragMode(int dragModeIn, EntityPlayer player) {
        if (dragModeIn == 0) {
            return true;
        }
        if (dragModeIn == 1) {
            return true;
        }
        return dragModeIn == 2 && player.capabilities.isCreativeMode;
    }

    protected void resetDrag() {
        this.dragEvent = 0;
        this.dragSlots.clear();
    }

    public static boolean canAddItemToSlot(@Nullable Slot slotIn, ItemStack stack, boolean stackSizeMatters) {
        boolean flag;
        boolean bl = flag = slotIn == null || !slotIn.getHasStack();
        if (!flag && stack.isItemEqual(slotIn.getStack()) && ItemStack.areItemStackTagsEqual(slotIn.getStack(), stack)) {
            return slotIn.getStack().func_190916_E() + (stackSizeMatters ? 0 : stack.func_190916_E()) <= stack.getMaxStackSize();
        }
        return flag;
    }

    public static void computeStackSize(Set<Slot> dragSlotsIn, int dragModeIn, ItemStack stack, int slotStackSize) {
        switch (dragModeIn) {
            case 0: {
                stack.func_190920_e(MathHelper.floor((float)stack.func_190916_E() / (float)dragSlotsIn.size()));
                break;
            }
            case 1: {
                stack.func_190920_e(1);
                break;
            }
            case 2: {
                stack.func_190920_e(stack.getItem().getItemStackLimit());
            }
        }
        stack.func_190917_f(slotStackSize);
    }

    public boolean canDragIntoSlot(Slot slotIn) {
        return true;
    }

    public static int calcRedstone(@Nullable TileEntity te) {
        return te instanceof IInventory ? Container.calcRedstoneFromInventory((IInventory)((Object)te)) : 0;
    }

    public static int calcRedstoneFromInventory(@Nullable IInventory inv) {
        if (inv == null) {
            return 0;
        }
        int i = 0;
        float f = 0.0f;
        int j = 0;
        while (j < inv.getSizeInventory()) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.func_190926_b()) {
                f += (float)itemstack.func_190916_E() / (float)Math.min(inv.getInventoryStackLimit(), itemstack.getMaxStackSize());
                ++i;
            }
            ++j;
        }
        return MathHelper.floor((f /= (float)inv.getSizeInventory()) * 14.0f) + (i > 0 ? 1 : 0);
    }

    protected void func_192389_a(World p_192389_1_, EntityPlayer p_192389_2_, InventoryCrafting p_192389_3_, InventoryCraftResult p_192389_4_) {
        if (!p_192389_1_.isRemote) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)p_192389_2_;
            ItemStack itemstack = ItemStack.EMPTY;
            IRecipe irecipe = CraftingManager.func_192413_b(p_192389_3_, p_192389_1_);
            if (irecipe != null && (irecipe.func_192399_d() || !p_192389_1_.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.func_192037_E().func_193830_f(irecipe))) {
                p_192389_4_.func_193056_a(irecipe);
                itemstack = irecipe.getCraftingResult(p_192389_3_);
            }
            p_192389_4_.setInventorySlotContents(0, itemstack);
            entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, itemstack));
        }
    }
}

