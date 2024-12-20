/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.others;

import net.minecraft.entity.player.EntityPlayer;
import net.wolframclient.compatibility.WMath;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.RenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public class FakeHackers
extends ModuleListener {
    public FakeHackers() {
        super("FakeHackers", Module.Category.OTHER, "Make other players look like if they're hacking");
    }

    @EventTarget
    public void onUpdate(RenderEvent event) {
        for (Object o : WMinecraft.getWorld().loadedEntityList) {
            EntityPlayer player;
            if (!(o instanceof EntityPlayer) || (player = (EntityPlayer)o) == WMinecraft.getPlayer() || (double)WMinecraft.getPlayer().getDistanceToEntity(player) > 3.8) continue;
            double diffX = WMinecraft.getPlayer().posX - player.posX;
            double diffZ = WMinecraft.getPlayer().posZ - player.posZ;
            double diffY = WMinecraft.getPlayer().posY + (double)WMinecraft.getPlayer().getEyeHeight() - (player.posY + (double)player.getEyeHeight());
            double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
            float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
            float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
            player.rotationYawHead += WMath.wrapDegrees(yaw - player.rotationYawHead);
            player.rotationYaw += WMath.wrapDegrees(yaw - player.rotationYaw);
            player.rotationPitch += WMath.wrapDegrees(pitch - player.rotationPitch);
        }
    }
}

