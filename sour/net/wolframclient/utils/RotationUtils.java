/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.utils;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.Vec3d;
import net.wolframclient.compatibility.WMath;
import net.wolframclient.compatibility.WMinecraft;

public final class RotationUtils {
    public static Vec3d getEyesPos() {
        EntityPlayerSP player = WMinecraft.getPlayer();
        return new Vec3d(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ);
    }

    public static Vec3d getClientLookVec() {
        EntityPlayerSP player = WMinecraft.getPlayer();
        float f = WMath.cos(-player.rotationYaw * ((float)Math.PI / 180) - (float)Math.PI);
        float f1 = WMath.sin(-player.rotationYaw * ((float)Math.PI / 180) - (float)Math.PI);
        float f2 = -WMath.cos(-player.rotationPitch * ((float)Math.PI / 180));
        float f3 = WMath.sin(-player.rotationPitch * ((float)Math.PI / 180));
        return new Vec3d(f1 * f2, f3, f * f2);
    }
}

