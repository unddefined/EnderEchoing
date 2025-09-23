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

public class EnderEchoicResonatorBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation ANIMS = RawAnimation.begin().thenPlay("ender_echoic_resonator.common");

    public EnderEchoicResonatorBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ENDER_ECHOIC_RESONATOR.get(), pos, blockState);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Activation", 0,
                state -> state.setAndContinue(ANIMS)));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EnderEchoicResonatorBlockEntity blockEntity) {
        //粒子效果
        if (level.isClientSide && level.getRandom().nextFloat() < 0.1) {
            level.addParticle(ParticleTypes.PORTAL,
                    pos.getX() + 0.5 + (level.random.nextDouble() - 0.1) * 0.2,
                    pos.getY() + 1 + (level.random.nextDouble() - 0.1) * 0.2,
                    pos.getZ() + 0.5 + (level.random.nextDouble() - 0.1) * 0.2,
                    (level.random.nextDouble() - 0.5),
                    -level.random.nextDouble(),
                    (level.random.nextDouble() - 0.5));
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}