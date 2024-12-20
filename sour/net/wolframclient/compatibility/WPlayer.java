/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.compatibility;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.wolframclient.compatibility.WMinecraft;

public final class WPlayer {
    public static void swingArmClient() {
        WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
    }

    public static float getCooldown() {
        return WMinecraft.getPlayer().getCooledAttackStrength(0.0f);
    }

    public static EnumFacing getHorizontalFacing() {
        return WMinecraft.getPlayer().getHorizontalFacing();
    }

    public static void copyPlayerModel(EntityPlayer from, EntityPlayer to) {
        to.getDataManager().set(EntityPlayer.PLAYER_MODEL_FLAG, from.getDataManager().get(EntityPlayer.PLAYER_MODEL_FLAG));
    }
}

