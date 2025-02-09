/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.util.DamageSource;
import net.wolframclient.compatibility.WEnchantments;
import net.wolframclient.compatibility.WEntityEquipmentSlot;
import net.wolframclient.compatibility.WItem;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.compatibility.WPlayerController;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.NetworkManagerPacketSendEvent;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class AutoArmor
extends ModuleListener {
    private int timer;

    public AutoArmor() {
        super("AutoArmor", Module.Category.COMBAT, "Automatically put on armor");
    }

    @Override
    protected void onEnable2() {
        this.timer = 0;
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        ItemArmor item;
        ItemStack stack;
        if (this.timer > 0) {
            --this.timer;
            return;
        }
        if (Minecraft.getMinecraft().currentScreen instanceof GuiContainer && !(Minecraft.getMinecraft().currentScreen instanceof InventoryEffectRenderer)) {
            return;
        }
        EntityPlayerSP player = WMinecraft.getPlayer();
        InventoryPlayer inventory = player.inventory;
        if (player.movementInput.moveForward != 0.0f || player.movementInput.moveStrafe != 0.0f) {
            return;
        }
        int[] bestArmorSlots = new int[4];
        int[] bestArmorValues = new int[4];
        int type = 0;
        while (type < 4) {
            bestArmorSlots[type] = -1;
            stack = inventory.armorItemInSlot(type);
            if (!WItem.isNullOrEmpty(stack) && stack.getItem() instanceof ItemArmor) {
                item = (ItemArmor)stack.getItem();
                bestArmorValues[type] = this.getArmorValue(item, stack);
            }
            ++type;
        }
        int slot = 0;
        while (slot < 36) {
            stack = inventory.getStackInSlot(slot);
            if (!WItem.isNullOrEmpty(stack) && stack.getItem() instanceof ItemArmor) {
                item = (ItemArmor)stack.getItem();
                int armorType = WItem.getArmorType(item);
                int armorValue = this.getArmorValue(item, stack);
                if (armorValue > bestArmorValues[armorType]) {
                    bestArmorSlots[armorType] = slot;
                    bestArmorValues[armorType] = armorValue;
                }
            }
            ++slot;
        }
        ArrayList<Integer> types = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3));
        Collections.shuffle(types);
        for (int type2 : types) {
            ItemStack oldArmor;
            int slot2 = bestArmorSlots[type2];
            if (slot2 == -1 || !WItem.isNullOrEmpty(oldArmor = inventory.armorItemInSlot(type2)) && inventory.getFirstEmptyStack() == -1) continue;
            if (slot2 < 9) {
                slot2 += 36;
            }
            if (!WItem.isNullOrEmpty(oldArmor)) {
                WPlayerController.windowClick_QUICK_MOVE(8 - type2);
            }
            WPlayerController.windowClick_QUICK_MOVE(slot2);
            break;
        }
    }

    @EventTarget
    public void onPacket(NetworkManagerPacketSendEvent event) {
        if (event.getPacket() instanceof CPacketClickWindow) {
            this.timer = 2;
        }
    }

    private int getArmorValue(ItemArmor item, ItemStack stack) {
        int armorPoints = item.damageReduceAmount;
        int prtPoints = 0;
        int armorToughness = (int)WItem.getArmorToughness(item);
        int armorType = item.getArmorMaterial().getDamageReductionAmount(WEntityEquipmentSlot.LEGS);
        Enchantment protection = WEnchantments.PROTECTION;
        int prtLvl = WEnchantments.getEnchantmentLevel(protection, stack);
        EntityPlayerSP player = WMinecraft.getPlayer();
        DamageSource dmgSource = DamageSource.causePlayerDamage(player);
        prtPoints = protection.calcModifierDamage(prtLvl, dmgSource);
        return armorPoints * 5 + prtPoints * 3 + armorToughness + armorType;
    }
}

