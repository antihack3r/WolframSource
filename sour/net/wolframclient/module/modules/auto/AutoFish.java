/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.auto;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WEnchantments;
import net.wolframclient.compatibility.WItem;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.compatibility.WPlayerController;
import net.wolframclient.compatibility.WSoundEvents;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.NetworkManagerPacketReceiveEvent;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class AutoFish
extends ModuleListener {
    private int timer;
    private double validRange = 1.5;

    public AutoFish() {
        super("AutoFish", Module.Category.AUTO, "Catch fish automatically");
    }

    @Override
    protected void onEnable2() {
        this.timer = 0;
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        EntityPlayerSP player = WMinecraft.getPlayer();
        InventoryPlayer inventory = player.inventory;
        if (this.timer < 0) {
            WPlayerController.windowClick_PICKUP(-this.timer);
            this.timer = 15;
            return;
        }
        int bestRodValue = this.getRodValue(inventory.getStackInSlot(inventory.currentItem));
        int bestRodSlot = bestRodValue > -1 ? inventory.currentItem : -1;
        int slot = 0;
        while (slot < 36) {
            ItemStack stack = inventory.getStackInSlot(slot);
            int rodValue = this.getRodValue(stack);
            if (rodValue > bestRodValue) {
                bestRodValue = rodValue;
                bestRodSlot = slot;
            }
            ++slot;
        }
        if (bestRodSlot == inventory.currentItem) {
            if (this.timer > 0) {
                --this.timer;
                return;
            }
            if (player.fishEntity == null) {
                this.rightClick();
            }
            return;
        }
        if (bestRodSlot == -1) {
            Wolfram.getWolfram().addChatMessage("Out of fishing rods.");
            this.setEnabled(false, false);
            return;
        }
        if (bestRodSlot < 9) {
            inventory.currentItem = bestRodSlot;
            return;
        }
        int firstEmptySlot = inventory.getFirstEmptyStack();
        if (firstEmptySlot != -1) {
            if (firstEmptySlot >= 9) {
                WPlayerController.windowClick_QUICK_MOVE(36 + inventory.currentItem);
            }
            WPlayerController.windowClick_QUICK_MOVE(bestRodSlot);
        } else {
            WPlayerController.windowClick_PICKUP(bestRodSlot);
            WPlayerController.windowClick_PICKUP(36 + inventory.currentItem);
            this.timer = -bestRodSlot;
        }
    }

    @EventTarget
    public void onPacketInput(NetworkManagerPacketReceiveEvent event) {
        EntityPlayerSP player = WMinecraft.getPlayer();
        if (player == null || player.fishEntity == null) {
            return;
        }
        if (!(event.getPacket() instanceof SPacketSoundEffect)) {
            return;
        }
        SPacketSoundEffect sound = (SPacketSoundEffect)event.getPacket();
        if (!WSoundEvents.isBobberSplash(sound)) {
            return;
        }
        EntityFishHook bobber = player.fishEntity;
        if (Math.abs(sound.getX() - bobber.posX) > this.validRange || Math.abs(sound.getZ() - bobber.posZ) > this.validRange) {
            return;
        }
        this.rightClick();
    }

    private int getRodValue(ItemStack stack) {
        if (WItem.isNullOrEmpty(stack) || !(stack.getItem() instanceof ItemFishingRod)) {
            return -1;
        }
        int luckOTSLvl = WEnchantments.getEnchantmentLevel(WEnchantments.LUCK_OF_THE_SEA, stack);
        int lureLvl = WEnchantments.getEnchantmentLevel(WEnchantments.LURE, stack);
        int unbreakingLvl = WEnchantments.getEnchantmentLevel(WEnchantments.UNBREAKING, stack);
        int mendingBonus = WEnchantments.getEnchantmentLevel(WEnchantments.MENDING, stack);
        int noVanishBonus = WEnchantments.hasVanishingCurse(stack) ? 0 : 1;
        return luckOTSLvl * 9 + lureLvl * 9 + unbreakingLvl * 2 + mendingBonus + noVanishBonus;
    }

    private void rightClick() {
        ItemStack stack = WMinecraft.getPlayer().inventory.getCurrentItem();
        if (WItem.isNullOrEmpty(stack) || !(stack.getItem() instanceof ItemFishingRod)) {
            return;
        }
        Minecraft.getMinecraft().rightClickMouse();
        this.timer = 15;
    }
}

