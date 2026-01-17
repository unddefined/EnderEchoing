package com.unddefined.enderechoing.blocks.entity;

import com.unddefined.enderechoing.server.DataComponents.EnderEchoCrystalSavedData;
import com.unddefined.enderechoing.server.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EnderEchoCrystalBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public EnderEchoCrystalBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ENDER_ECHO_CRYSTAL.get(), pos, blockState);
    }

    @Override
    public void setRemoved() {
        if (level == null || level.isClientSide || level.getServer() == null || level.getServer().getPlayerList().getPlayers().isEmpty()) return;
        EnderEchoCrystalSavedData.get((ServerLevel) level).remove(worldPosition);
        super.setRemoved();
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0,
                        state -> state.setAndContinue(RawAnimation.begin().thenPlay("idle")))
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}
}