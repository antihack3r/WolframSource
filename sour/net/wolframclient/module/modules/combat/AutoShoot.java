/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.combat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WMath;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class AutoShoot
extends ModuleListener {
    private boolean shouldRelease = false;
    private int shouldUse = -1;

    public AutoShoot() {
        super("AutoShoot", Module.Category.COMBAT, "Automatically shoots, if the arrow is going to hit");
    }

    @EventTarget
    public void onRender(WorldRenderEvent event) {
        EntityPlayerSP player;
        if (this.shouldRelease) {
            this.shouldRelease = false;
            this.shouldUse = 0;
            Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed = false;
            return;
        }
        if (this.shouldUse == 1) {
            this.shouldUse = -1;
            Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed = true;
            return;
        }
        if (this.shouldUse != -1) {
            ++this.shouldUse;
        }
        if ((player = WMinecraft.getPlayer()).getHeldItemMainhand() != null) {
            Item item = player.getHeldItemMainhand().getItem();
            if (!(item instanceof ItemBow)) {
                return;
            }
        } else {
            return;
        }
        float yaw = (float)Math.toRadians(player.rotationYaw);
        float pitch = (float)Math.toRadians(player.rotationPitch);
        double posX = RenderManager.renderPosX - (double)(WMath.cos(yaw) * 0.16f);
        double posY = RenderManager.renderPosY + (double)player.getEyeHeight() - 0.1;
        double posZ = RenderManager.renderPosZ - (double)(WMath.sin(yaw) * 0.16f);
        double motionX = -WMath.sin(yaw) * WMath.cos(pitch);
        double motionY = -WMath.sin(pitch);
        double motionZ = WMath.cos(yaw) * WMath.cos(pitch);
        if (player.getItemInUseCount() == 0) {
            return;
        }
        float power = (float)player.getItemInUseMaxCount() / 20.0f;
        power = (power * power + power * 2.0f) / 3.0f;
        if (Wolfram.getWolfram().moduleManager.isEnabled("fastbow")) {
            power = 1.0f;
        }
        if ((double)power < 0.1) {
            return;
        }
        if (power > 1.0f) {
            power = 1.0f;
        }
        float distance = (float)Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX /= (double)distance;
        motionY /= (double)distance;
        motionZ /= (double)distance;
        motionX *= (double)(power * 2.0f) * 1.5;
        motionY *= (double)(power * 2.0f) * 1.5;
        motionZ *= (double)(power * 2.0f) * 1.5;
        RayTraceResult landingPosition = null;
        boolean hasLanded = false;
        boolean hitEntity = false;
        float gravity = 0.05f;
        float size = 0.3f;
        int limit = 0;
        while (!hasLanded && limit < 300) {
            Vec3d posBefore = new Vec3d(posX, posY, posZ);
            Vec3d posAfter = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            landingPosition = WMinecraft.getWorld().rayTraceBlocks(posBefore, posAfter, false, true, false);
            posBefore = new Vec3d(posX, posY, posZ);
            posAfter = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            if (landingPosition != null) {
                hasLanded = true;
                posAfter = new Vec3d(landingPosition.hitVec.xCoord, landingPosition.hitVec.yCoord, landingPosition.hitVec.zCoord);
            }
            AxisAlignedBB arrowBox = new AxisAlignedBB(posX - (double)size, posY - (double)size, posZ - (double)size, posX + (double)size, posY + (double)size, posZ + (double)size);
            List<Entity> entityList = this.getEntitiesWithinAABB(arrowBox.addCoord(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
            int i = 0;
            while (i < entityList.size()) {
                AxisAlignedBB possibleEntityBoundingBox;
                RayTraceResult possibleEntityLanding;
                Entity possibleEntity = entityList.get(i);
                if (possibleEntity.canBeCollidedWith() && (possibleEntity != player || limit >= 5) && (possibleEntityLanding = (possibleEntityBoundingBox = possibleEntity.getEntityBoundingBox()).calculateIntercept(posBefore, posAfter)) != null) {
                    hitEntity = true;
                    hasLanded = true;
                    landingPosition = possibleEntityLanding;
                }
                ++i;
            }
            BlockPos var18 = new BlockPos(posX += motionX, posY += motionY, posZ += motionZ);
            IBlockState var2 = WMinecraft.getWorld().getBlockState(var18);
            if (var2.getMaterial() == Material.WATER) {
                motionX *= 0.6;
                motionY *= 0.6;
                motionZ *= 0.6;
            } else {
                motionX *= 0.99;
                motionY *= 0.99;
                motionZ *= 0.99;
            }
            motionY -= (double)gravity;
            ++limit;
        }
        if (landingPosition != null && hitEntity) {
            this.shouldRelease = true;
        }
    }

    private List<Entity> getEntitiesWithinAABB(AxisAlignedBB axisalignedBB) {
        ArrayList<Entity> list = new ArrayList<Entity>();
        int chunkMinX = WMath.floor((axisalignedBB.minX - 2.0) / 16.0);
        int chunkMaxX = WMath.floor((axisalignedBB.maxX + 2.0) / 16.0);
        int chunkMinZ = WMath.floor((axisalignedBB.minZ - 2.0) / 16.0);
        int chunkMaxZ = WMath.floor((axisalignedBB.maxZ + 2.0) / 16.0);
        int x = chunkMinX;
        while (x <= chunkMaxX) {
            int z = chunkMinZ;
            while (z <= chunkMaxZ) {
                WMinecraft.getWorld().getChunkFromChunkCoords(x, z).getEntitiesWithinAABBForEntity(WMinecraft.getPlayer(), axisalignedBB, list, null);
                ++z;
            }
            ++x;
        }
        return list;
    }
}

