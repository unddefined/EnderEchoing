package com.unddefined.enderechoing.entities;

import com.unddefined.enderechoing.server.registry.EntityRegistry;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;

public class CrystalHitProxyEntity extends Entity {
    private static final EntityDataAccessor<BlockPos> OWNER_POS = SynchedEntityData.defineId(CrystalHitProxyEntity.class, EntityDataSerializers.BLOCK_POS);

    public CrystalHitProxyEntity(Level level, BlockPos ownerPos) {
        this(EntityRegistry.CRYSTAL_HIT_PROXY.get(), level);
        this.entityData.set(OWNER_POS, ownerPos);
        this.setPos(ownerPos.getX() + 0.5, ownerPos.getY() + 0.5, ownerPos.getZ() + 0.5);
    }

    public CrystalHitProxyEntity(EntityType<CrystalHitProxyEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean isPickable() {return true;}

    @Override
    public boolean isAttackable() {return true;}

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source.getEntity() instanceof EnderDragon) {
            return false;
        } else {
            if (!this.isRemoved() && !this.level().isClientSide) {
                this.remove(RemovalReason.KILLED);
                if (!source.is(DamageTypeTags.IS_EXPLOSION)) {
                    DamageSource d = source.getEntity() != null ? this.damageSources().explosion(this, source.getEntity()) : null;
                    this.level().removeBlock(this.entityData.get(OWNER_POS), false);
                    this.level().explode(this, d, null, this.getX(), this.getY(), this.getZ(), 6.0F, false, Level.ExplosionInteraction.BLOCK);
                }
//                this.onDestroyedBy(source);
            }
            return true;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER_POS, BlockPos.ZERO);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("OwnerPos")) this.entityData.set(OWNER_POS,BlockPos.of(tag.getLong("OwnerPos")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putLong("OwnerPos", this.entityData.get(OWNER_POS).asLong());
    }

    public static class NoopRenderer extends EntityRenderer<CrystalHitProxyEntity> {
        public NoopRenderer(EntityRendererProvider.Context ctx) {super(ctx);}

        @Override
        public ResourceLocation getTextureLocation(CrystalHitProxyEntity entity) {return null;}
    }

}

