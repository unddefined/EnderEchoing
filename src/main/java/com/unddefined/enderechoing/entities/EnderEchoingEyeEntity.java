package com.unddefined.enderechoing.entities;

import com.unddefined.enderechoing.server.registry.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static com.unddefined.enderechoing.server.registry.ItemRegistry.ENDER_ECHOING_EYE;

public class EnderEchoingEyeEntity extends Entity implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK =
            SynchedEntityData.defineId(EnderEchoingEyeEntity.class, EntityDataSerializers.ITEM_STACK);
    private double targetX;
    private double targetY;
    private double targetZ;
    private int life;
    private boolean surviveAfterDeath;

    public EnderEchoingEyeEntity(EntityType<? extends EnderEchoingEyeEntity> type, Level level) {
        super(type, level);
    }

    public EnderEchoingEyeEntity(Level level, double x, double y, double z) {
        this(EntityRegistry.ENDER_ECHOING_EYE_ENTITY.get(), level);
        this.setPos(x, y, z);
    }

    public void setItem(ItemStack stack) {
        if (stack.isEmpty()) this.getEntityData().set(DATA_ITEM_STACK, this.getDefaultItem());
        else this.getEntityData().set(DATA_ITEM_STACK, stack.copyWithCount(1));
    }

    @Override
    public ItemStack getItem() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ITEM_STACK, this.getDefaultItem());
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(d0)) d0 = 4.0;
        d0 *= 64.0;
        return distance < d0 * d0;
    }

    public void signalTo(BlockPos pos) {
        double d0 = pos.getX();
        int i = pos.getY();
        double d1 = pos.getZ();
        double d2 = d0 - this.getX();
        double d3 = d1 - this.getZ();
        double d4 = Math.sqrt(d2 * d2 + d3 * d3);
        if (d4 > 12.0) {
            this.targetX = this.getX() + d2 / d4 * 12.0;
            this.targetZ = this.getZ() + d3 / d4 * 12.0;
            this.targetY = this.getY() + 8.0;
        } else {
            this.targetX = d0;
            this.targetY = i;
            this.targetZ = d1;
        }

        this.life = 0;
        this.surviveAfterDeath = this.random.nextInt(5) > 0;
    }

    @Override
    public void lerpMotion(double x, double y, double z) {
        this.setDeltaMovement(x, y, z);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = Math.sqrt(x * x + z * z);
            this.setYRot((float) (Mth.atan2(x, z) * 180.0F / (float) Math.PI));
            this.setXRot((float) (Mth.atan2(y, d0) * 180.0F / (float) Math.PI));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        double d3 = vec3.horizontalDistance();
        this.setXRot(lerpRotation(this.xRotO, (float) (Mth.atan2(vec3.y, d3) * 180.0F / (float) Math.PI)));
        this.setYRot(lerpRotation(this.yRotO, (float) (Mth.atan2(vec3.x, vec3.z) * 180.0F / (float) Math.PI)));
        if (!this.level().isClientSide) {
            double d4 = this.targetX - d0;
            double d5 = this.targetZ - d2;
            float f = (float) Math.sqrt(d4 * d4 + d5 * d5);
            float f1 = (float) Mth.atan2(d5, d4);
            double d6 = Mth.lerp(0.0025, d3, f);
            double d7 = vec3.y;
            if (f < 1.0F) {
                d6 *= 0.8;
                d7 *= 0.8;
            }

            int j = this.getY() < this.targetY ? 1 : -1;
            vec3 = new Vec3(Math.cos(f1) * d6, d7 + ((double) j - d7) * 0.015F, Math.sin(f1) * d6);
            this.setDeltaMovement(vec3);
        }

        if (this.isInWater()) {
            for (int i = 0; i < 4; i++) {
                this.level().addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * 0.25, d1 - vec3.y * 0.25,
                        d2 - vec3.z * 0.25, vec3.x, vec3.y, vec3.z);
            }
        } else {
            this.level().addParticle(ParticleTypes.PORTAL,
                    d0 - vec3.x * 0.25 + this.random.nextDouble() * 0.6 - 0.3,
                    d1 - vec3.y * 0.25 - 0.5,
                    d2 - vec3.z * 0.25 + this.random.nextDouble() * 0.6 - 0.3,
                    vec3.x, vec3.y, vec3.z);
        }

        if (!this.level().isClientSide) {
            this.setPos(d0, d1, d2);
            this.life++;
            if (this.life > 80) {
                this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 1.0F);
                this.discard();
                if (this.surviveAfterDeath)
                    this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.getItem()));
                else this.level().levelEvent(2003, this.blockPosition(), 0);

            }
        } else this.setPosRaw(d0, d1, d2);

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.put("Item", this.getItem().save(this.registryAccess()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("Item", 10)) {
            this.setItem(ItemStack.parse(this.registryAccess(), compound.getCompound("Item")).orElse(this.getDefaultItem()));
        } else this.setItem(this.getDefaultItem());

    }

    private ItemStack getDefaultItem() {
        return new ItemStack(ENDER_ECHOING_EYE.get());
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    private static float lerpRotation(float currentRotation, float targetRotation) {
        while (targetRotation - currentRotation < -180.0F) currentRotation -= 360.0F;

        while (targetRotation - currentRotation >= 180.0F) currentRotation += 360.0F;

        return currentRotation + (targetRotation - currentRotation) * 0.2F;
    }
}
