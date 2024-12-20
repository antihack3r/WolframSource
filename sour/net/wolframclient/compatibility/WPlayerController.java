/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.compatibility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.compatibility.WPlayer;

public final class WPlayerController {
    private static PlayerControllerMP getPlayerController() {
        return Minecraft.getMinecraft().playerController;
    }

    public static ItemStack windowClick_PICKUP(int slot) {
        return WPlayerController.getPlayerController().windowClick(0, slot, 0, ClickType.PICKUP, WMinecraft.getPlayer());
    }

    public static ItemStack windowClick_QUICK_MOVE(int slot) {
        return WPlayerController.getPlayerController().windowClick(0, slot, 0, ClickType.QUICK_MOVE, WMinecraft.getPlayer());
    }

    public static void processRightClick() {
        WPlayerController.getPlayerController().processRightClick(WMinecraft.getPlayer(), WMinecraft.getWorld(), EnumHand.MAIN_HAND);
    }

    public static void attackAndSwing(Entity entity) {
        WPlayerController.getPlayerController().attackEntity(WMinecraft.getPlayer(), entity);
        WPlayer.swingArmClient();
    }
}

