package com.unddefined.enderechoing.blocks.entity;

import com.unddefined.enderechoing.server.InfrasoundDamage;
import com.unddefined.enderechoing.server.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class SculkWhisperBlockEntity extends BlockEntity implements GeoBlockEntity, VibrationSystem, GameEventListener.Provider<VibrationSystem.Listener> {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final VibrationSystem.Data vibrationData = new VibrationSystem.Data();
    private final VibrationSystem.User vibrationUser = new SculkWhisperVibrationUser(this);
    private final VibrationSystem.Listener vibrationListener = new VibrationSystem.Listener(this);

    public SculkWhisperBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.SCULK_WHISPER.get(), pos, blockState);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public VibrationSystem.@NotNull Data getVibrationData() {
        return this.vibrationData;
    }

    @Override
    public @NotNull User getVibrationUser() {
        return this.vibrationUser;
    }

    @Override
    public @NotNull VibrationSystem.Listener getListener() {
        return this.vibrationListener;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SculkWhisperBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            VibrationSystem.Ticker.tick(serverLevel, blockEntity.vibrationData, blockEntity.vibrationUser);
        }
    }

    static class SculkWhisperVibrationUser implements VibrationSystem.User {
        private final SculkWhisperBlockEntity blockEntity;
        private final PositionSource positionSource;

        public SculkWhisperVibrationUser(SculkWhisperBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
            this.positionSource = new net.minecraft.world.level.gameevent.BlockPositionSource(blockEntity.getBlockPos());
        }

        @Override
        public int getListenerRadius() {
            return 8;
        }

        @Override
        public PositionSource getPositionSource() {
            return positionSource;
        }

        @Override
        public TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.SHRIEKER_CAN_LISTEN;
        }

        @Override
        public boolean canReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> gameEvent, GameEvent.Context context) {
            // 只接收SHRIEKER_CAN_LISTEN标签中的游戏事件
            return gameEvent.is(GameEventTags.SHRIEKER_CAN_LISTEN);
        }

        @Override
        public void onReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> gameEvent, @Nullable Entity entity, @Nullable Entity playerEntity, float distance) {
            Vec3 center = Vec3.atCenterOf(this.blockEntity.getBlockPos().above());
            InfrasoundDamage.InfrasoundBurst(level, center, 4.0f, 12.0f, entity);
        }

        @Override
        public boolean requiresAdjacentChunksToBeTicking() {
            return true;
        }
    }
}