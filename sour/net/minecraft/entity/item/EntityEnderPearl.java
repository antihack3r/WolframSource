/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityEnderPearl
extends EntityThrowable {
    private EntityLivingBase thrower;

    public EntityEnderPearl(World worldIn) {
        super(worldIn);
    }

    public EntityEnderPearl(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        this.thrower = throwerIn;
    }

    public EntityEnderPearl(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public static void registerFixesEnderPearl(DataFixer fixer) {
        EntityThrowable.registerFixesThrowable(fixer, "ThrownEnderpearl");
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        BlockPos blockpos;
        TileEntity tileentity;
        EntityLivingBase entitylivingbase = this.getThrower();
        if (result.entityHit != null) {
            if (result.entityHit == this.thrower) {
                return;
            }
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, entitylivingbase), 0.0f);
        }
        if (result.typeOfHit == RayTraceResult.Type.BLOCK && (tileentity = this.world.getTileEntity(blockpos = result.getBlockPos())) instanceof TileEntityEndGateway) {
            TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway)tileentity;
            if (entitylivingbase != null) {
                if (entitylivingbase instanceof EntityPlayerMP) {
                    CriteriaTriggers.field_192124_d.func_192193_a((EntityPlayerMP)entitylivingbase, this.world.getBlockState(blockpos));
                }
                tileentityendgateway.teleportEntity(entitylivingbase);
                this.setDead();
                return;
            }
            tileentityendgateway.teleportEntity(this);
            return;
        }
        int i = 0;
        while (i < 32) {
            this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX, this.posY + this.rand.nextDouble() * 2.0, this.posZ, this.rand.nextGaussian(), 0.0, this.rand.nextGaussian(), new int[0]);
            ++i;
        }
        if (!this.world.isRemote) {
            if (entitylivingbase instanceof EntityPlayerMP) {
                EntityPlayerMP entityplayermp = (EntityPlayerMP)entitylivingbase;
                if (entityplayermp.connection.getNetworkManager().isChannelOpen() && entityplayermp.world == this.world && !entityplayermp.isPlayerSleeping()) {
                    if (this.rand.nextFloat() < 0.05f && this.world.getGameRules().getBoolean("doMobSpawning")) {
                        EntityEndermite entityendermite = new EntityEndermite(this.world);
                        entityendermite.setSpawnedByPlayer(true);
                        entityendermite.setLocationAndAngles(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, entitylivingbase.rotationYaw, entitylivingbase.rotationPitch);
                        this.world.spawnEntityInWorld(entityendermite);
                    }
                    if (entitylivingbase.isRiding()) {
                        entitylivingbase.dismountRidingEntity();
                    }
                    entitylivingbase.setPositionAndUpdate(this.posX, this.posY, this.posZ);
                    entitylivingbase.fallDistance = 0.0f;
                    entitylivingbase.attackEntityFrom(DamageSource.fall, 5.0f);
                }
            } else if (entitylivingbase != null) {
                entitylivingbase.setPositionAndUpdate(this.posX, this.posY, this.posZ);
                entitylivingbase.fallDistance = 0.0f;
            }
            this.setDead();
        }
    }

    @Override
    public void onUpdate() {
        EntityLivingBase entitylivingbase = this.getThrower();
        if (entitylivingbase != null && entitylivingbase instanceof EntityPlayer && !entitylivingbase.isEntityAlive()) {
            this.setDead();
        } else {
            super.onUpdate();
        }
    }
}

