/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.world.IBlockAccess;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WMath;
import net.wolframclient.compatibility.WMinecraft;

public class EntityHelper {
    private static PathFinder pathFinder = new PathFinder(new WalkNodeProcessor());

    public static double[] interpolate(Entity entity) {
        double partialTicks = Minecraft.getMinecraft().timer.renderPartialTicks;
        double[] pos = new double[]{entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks};
        return pos;
    }

    public static void faceEntity(Entity entity) {
        EntityHelper.faceEntity(entity, 20.0f);
    }

    public static void faceEntity(Entity entity, float limit) {
        double diffY;
        if (entity == null) {
            return;
        }
        double diffX = entity.posX - WMinecraft.getPlayer().posX;
        double diffZ = entity.posZ - WMinecraft.getPlayer().posZ;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            diffY = entityLivingBase.posY + (double)entityLivingBase.getEyeHeight() - (WMinecraft.getPlayer().posY + (double)WMinecraft.getPlayer().getEyeHeight());
        } else {
            diffY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0 - (WMinecraft.getPlayer().posY + (double)WMinecraft.getPlayer().getEyeHeight());
        }
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        float diffYaw = WMath.wrapDegrees(yaw - WMinecraft.getPlayer().rotationYaw);
        float diffPitch = WMath.wrapDegrees(pitch - WMinecraft.getPlayer().rotationPitch);
        if (diffYaw > limit) {
            diffYaw = limit;
        } else if (diffYaw < -limit) {
            diffYaw = -limit;
        }
        WMinecraft.getPlayer().rotationYaw += diffYaw;
        WMinecraft.getPlayer().rotationPitch += diffPitch;
    }

    public static void facePos(double posX, double posY, double posZ) {
        EntityHelper.facePos(posX, posY, posZ, true);
    }

    public static void facePos(double posX, double posY, double posZ, boolean shouldLimit) {
        double diffX = posX - WMinecraft.getPlayer().posX;
        double diffZ = posZ - WMinecraft.getPlayer().posZ;
        double diffY = posY - WMinecraft.getPlayer().posY;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        float diffYaw = WMath.wrapDegrees(yaw - WMinecraft.getPlayer().rotationYaw);
        float diffPitch = WMath.wrapDegrees(pitch - WMinecraft.getPlayer().rotationPitch) + 5.0f;
        if (shouldLimit) {
            if (diffYaw > 20.0f) {
                diffYaw = 20.0f;
            } else if (diffYaw < -20.0f) {
                diffYaw = -20.0f;
            }
        }
        WMinecraft.getPlayer().rotationYaw += diffYaw;
        WMinecraft.getPlayer().rotationPitch += diffPitch;
    }

    public static EntityLivingBase getClosestEntityWithoutReachFactor() {
        EntityLivingBase closestEntity = null;
        float distance = 9999.0f;
        for (Object object : WMinecraft.getWorld().loadedEntityList) {
            EntityLivingBase entity;
            if (!(object instanceof EntityLivingBase) || !EntityHelper.isAttackable(entity = (EntityLivingBase)object)) continue;
            float newDistance = WMinecraft.getPlayer().getDistanceToEntity(entity);
            if (entity instanceof EntityPlayer && (Wolfram.getWolfram().relationManager.isFriend((EntityPlayer)entity) || !Wolfram.getWolfram().storageManager.moduleSettings.getBoolean("killaura_players")) || entity instanceof IMob && !Wolfram.getWolfram().storageManager.moduleSettings.getBoolean("killaura_mobs") || entity instanceof IAnimals && !Wolfram.getWolfram().storageManager.moduleSettings.getBoolean("killaura_animals")) continue;
            if (entity instanceof IMob) {
                newDistance *= 1.5f;
            }
            if (entity instanceof IAnimals) {
                newDistance *= 3.0f;
            }
            if (closestEntity != null) {
                if (!(distance > newDistance)) continue;
                closestEntity = entity;
                distance = newDistance;
                continue;
            }
            closestEntity = entity;
            distance = newDistance;
        }
        return closestEntity;
    }

    public static EntityLivingBase getClosestEntity(boolean killaura) {
        if (killaura) {
            return EntityHelper.getClosestEntity();
        }
        return EntityHelper.getClosestEntityWithoutReachFactor();
    }

    public static EntityLivingBase getClosestEntity() {
        EntityLivingBase closestEntity = null;
        if (Wolfram.getWolfram().moduleManager.isEnabled("farmhuntesp")) {
            for (Object object : WMinecraft.getWorld().loadedEntityList) {
                if (object == WMinecraft.getPlayer() || !(object instanceof EntityLivingBase)) continue;
                EntityLivingBase mob = (EntityLivingBase)object;
                if (mob.rotationPitch == 0.0f) continue;
                if (closestEntity != null) {
                    if (!EntityHelper.isCloser(mob, closestEntity) || !EntityHelper.isBiggerThreat(mob, closestEntity)) continue;
                    closestEntity = mob;
                    continue;
                }
                closestEntity = mob;
            }
            return closestEntity;
        }
        for (Object object : WMinecraft.getWorld().loadedEntityList) {
            EntityLivingBase entity;
            if (!(object instanceof EntityLivingBase) || !EntityHelper.isAttackable(entity = (EntityLivingBase)object) || entity instanceof EntityPlayer && (Wolfram.getWolfram().relationManager.isFriend((EntityPlayer)entity) || !Wolfram.getWolfram().storageManager.moduleSettings.getBoolean("killaura_players")) || entity instanceof IMob && !Wolfram.getWolfram().storageManager.moduleSettings.getBoolean("killaura_mobs") || entity instanceof IAnimals && !Wolfram.getWolfram().storageManager.moduleSettings.getBoolean("killaura_animals")) continue;
            if (closestEntity != null) {
                if (!EntityHelper.isCloser(entity, closestEntity) || !EntityHelper.isBiggerThreat(entity, closestEntity)) continue;
                closestEntity = entity;
                continue;
            }
            closestEntity = entity;
        }
        return closestEntity;
    }

    public static boolean canEntityBeAccessed(EntityLivingBase entity) {
        boolean z;
        Path Path2 = pathFinder.findPath((IBlockAccess)WMinecraft.getWorld(), (Entity)WMinecraft.getPlayer(), entity, 50.0f);
        PathPoint finalPathPoint = Path2.getFinalPathPoint();
        boolean x = (double)finalPathPoint.xCoord < entity.posX + 1.0 && (double)finalPathPoint.xCoord > entity.posX - 1.0;
        boolean y = (double)finalPathPoint.yCoord < entity.posY + 1.0 && (double)finalPathPoint.yCoord > entity.posY - 1.0;
        boolean bl = z = (double)finalPathPoint.zCoord < entity.posZ + 1.0 && (double)finalPathPoint.zCoord > entity.posZ - 1.0;
        return x && y && z;
    }

    public static boolean isAttackable(EntityLivingBase entity) {
        return entity != null && entity != WMinecraft.getPlayer() && entity.isEntityAlive();
    }

    public static boolean isReachable(EntityLivingBase entity) {
        return WMinecraft.getPlayer().getDistanceToEntity(entity) <= Wolfram.getWolfram().storageManager.moduleSettings.getFloat("reach");
    }

    public static boolean isFar(EntityLivingBase entity) {
        return WMinecraft.getPlayer().getDistanceToEntity(entity) > Wolfram.getWolfram().storageManager.moduleSettings.getFloat("reach") + 2.0f;
    }

    public static boolean isCloser(Entity entityCompared, Entity referenceEntity) {
        return WMinecraft.getPlayer().getDistanceToEntity(entityCompared) < WMinecraft.getPlayer().getDistanceToEntity(referenceEntity);
    }

    public static boolean isBiggerThreat(EntityLivingBase entityCompared, EntityLivingBase referenceEntity) {
        if (Wolfram.getWolfram().relationManager.isEnemy(entityCompared) && Wolfram.getWolfram().relationManager.isEnemy(referenceEntity)) {
            if (EntityHelper.isFar(referenceEntity) && EntityHelper.isReachable(entityCompared)) {
                return true;
            }
            if (EntityHelper.isFar(entityCompared)) {
                return false;
            }
            return WMinecraft.getPlayer().getDistanceToEntity(entityCompared) > WMinecraft.getPlayer().getDistanceToEntity(referenceEntity);
        }
        if (Wolfram.getWolfram().relationManager.isEnemy(referenceEntity) && !EntityHelper.isFar(referenceEntity)) {
            return false;
        }
        if (Wolfram.getWolfram().relationManager.isEnemy(entityCompared) && !EntityHelper.isFar(entityCompared)) {
            return true;
        }
        if (EntityHelper.isFar(referenceEntity) && EntityHelper.isReachable(entityCompared)) {
            return true;
        }
        if (EntityHelper.isFar(entityCompared)) {
            return false;
        }
        if (entityCompared instanceof EntityPlayer) {
            return true;
        }
        if (entityCompared instanceof IMob) {
            return !(referenceEntity instanceof EntityPlayer);
        }
        return !(referenceEntity instanceof EntityPlayer) && !(referenceEntity instanceof IMob);
    }
}

