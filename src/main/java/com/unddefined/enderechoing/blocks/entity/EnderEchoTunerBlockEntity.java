package com.unddefined.enderechoing.blocks.entity;

import com.unddefined.enderechoing.server.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EnderEchoTunerBlockEntity extends BlockEntity implements GeoBlockEntity {
    private static final RawAnimation common = RawAnimation.begin().thenPlay("common");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    public EnderEchoTunerBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ENDER_ECHO_TUNER.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EnderEchoTunerBlockEntity blockEntity) {

        //粒子效果
        if (level.isClientSide && level.getRandom().nextFloat() < 0.1) {
            level.addParticle(ParticleTypes.ENCHANT,
                    pos.getX() + Math.clamp(level.random.nextDouble(), 0.2, 0.8),
                    pos.getY() + 1.5,
                    pos.getZ() + Math.clamp(level.random.nextDouble(), 0.2, 0.8),
                    (level.random.nextDouble() - 0.5),
                    -level.random.nextDouble(),
                    (level.random.nextDouble() - 0.5));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 20, event -> event.setAndContinue(common)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return this.cache;}

}