/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.combat;

import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.wolframclient.compatibility.WConnection;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.compatibility.WPlayerController;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.event.events.PostMotionUpdateEvent;
import net.wolframclient.module.Module;

public class AutoSoup
extends Module
implements Listener {
    int soupCount = 0;
    int previous = -1;

    public AutoSoup() {
        super("AutoSoup", Module.Category.COMBAT, "Automatically eat soup");
    }

    @Override
    public void onEnable() {
        registry.registerListener(this);
    }

    @Override
    public void onDisable() {
        registry.unregisterListener(this);
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        this.updateSoupCount();
        if (WMinecraft.getPlayer().getHealth() <= 14.0f && this.soupCount > 0 && !WMinecraft.getPlayer().isPotionActive(MobEffects.REGENERATION)) {
            ItemStack currentItem;
            this.previous = -1;
            int soupSlot = -1;
            int inventorySlot = 0;
            while (inventorySlot < 9) {
                currentItem = WMinecraft.getPlayer().inventory.getStackInSlot(inventorySlot);
                if (currentItem != null && currentItem.getItem() == Item.getItemById(282)) {
                    soupSlot = inventorySlot;
                    break;
                }
                ++inventorySlot;
            }
            if (soupSlot == -1) {
                inventorySlot = 9;
                while (inventorySlot < 36) {
                    if (WMinecraft.getPlayer().inventoryContainer.getSlot(inventorySlot).getHasStack() && (currentItem = WMinecraft.getPlayer().inventoryContainer.getSlot(inventorySlot).getStack()) != null && currentItem.getItem() == Item.getItemById(282)) {
                        soupSlot = inventorySlot;
                        break;
                    }
                    ++inventorySlot;
                }
                if (soupSlot == -1) {
                    this.soupCount = 0;
                    return;
                }
                this.cleanUp();
                WPlayerController.windowClick_QUICK_MOVE(soupSlot);
                return;
            }
            this.previous = WMinecraft.getPlayer().inventory.currentItem;
            WMinecraft.getPlayer().inventory.currentItem = soupSlot;
            WPlayerController.processRightClick();
            WConnection.sendPacket(new CPacketCloseWindow());
        }
    }

    @EventTarget
    public void onPostUpdate(PostMotionUpdateEvent event) {
        if (this.previous != -1) {
            WMinecraft.getPlayer().inventory.currentItem = this.previous;
            this.previous = -1;
            this.cleanUp();
        }
    }

    public void cleanUp() {
        ItemStack currentItem;
        boolean hasRoom = false;
        int inventorySlot = 9;
        while (inventorySlot < 36) {
            currentItem = WMinecraft.getPlayer().inventoryContainer.getSlot(inventorySlot).getStack();
            if (currentItem == null || currentItem.getItem() == Item.getItemById(281) && currentItem.stackSize < 65) {
                hasRoom = true;
            }
            ++inventorySlot;
        }
        inventorySlot = 36;
        while (inventorySlot < 45) {
            if (WMinecraft.getPlayer().inventoryContainer.getSlot(inventorySlot).getHasStack() && (currentItem = WMinecraft.getPlayer().inventoryContainer.getSlot(inventorySlot).getStack()) != null && currentItem.getItem() == Item.getItemById(281)) {
                if (hasRoom) {
                    WPlayerController.windowClick_QUICK_MOVE(inventorySlot);
                    break;
                }
                WPlayerController.windowClick_PICKUP(inventorySlot);
                WPlayerController.windowClick_PICKUP(-999);
                break;
            }
            ++inventorySlot;
        }
    }

    private void updateSoupCount() {
        int soupCount = 0;
        int inventorySlot = 0;
        while (inventorySlot < 45) {
            ItemStack currentItem = WMinecraft.getPlayer().inventoryContainer.getSlot(inventorySlot).getStack();
            if (currentItem != null && currentItem.getItem() == Item.getItemById(282)) {
                ++soupCount;
            }
            ++inventorySlot;
        }
        this.soupCount = soupCount;
    }
}

