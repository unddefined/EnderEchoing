package com.unddefined.enderechoing.blocks.entity;

import com.unddefined.enderechoing.blocks.EnderEchoTunerBlock;
import com.unddefined.enderechoing.server.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EnderEchoTunerBlockEntity extends BlockEntity implements GeoBlockEntity {
    private static final RawAnimation common = RawAnimation.begin().thenPlay("common");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private ResourceKey<Level> Dimension;
    private String name;
    private BlockPos pos = null;

    public EnderEchoTunerBlockEntity(BlockPos pos, BlockState blockState) {super(BlockEntityRegistry.ENDER_ECHO_TUNER.get(), pos, blockState);}

    public static void tick(Level level, BlockPos pos, BlockState state, EnderEchoTunerBlockEntity blockEntity) {
        float offset = state.getValue(EnderEchoTunerBlock.FACING) == Direction.DOWN ? 0.7f : 0;

        //粒子效果
        if (level.isClientSide && level.getRandom().nextFloat() < 0.15) {
            level.addParticle(ParticleTypes.ENCHANT,
                    pos.getX() + Math.clamp(level.random.nextDouble(), 0.2, 0.8),
                    pos.getY() + 1.1 - offset,
                    pos.getZ() + Math.clamp(level.random.nextDouble(), 0.2, 0.8),
                    0, 0, 0);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("pos")) pos = BlockPos.of(tag.getLong("pos"));

        if (tag.contains("dimension"))
            Dimension = ResourceKey.create(ResourceKey.createRegistryKey(ResourceLocation.parse("dimension")),
                    ResourceLocation.parse(tag.getString("dimension")));

        if (tag.contains("name")) name = tag.getString("name");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putLong("pos", pos == null ? BlockPos.ZERO.asLong() : pos.asLong());
        if (Dimension != null) tag.putString("dimension", Dimension.location().toString());
        if (name != null) tag.putString("name", name);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {return this.saveWithoutMetadata(registries);}

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {return ClientboundBlockEntityDataPacket.create(this);}

    public void setSelectedPosition(BlockPos p, ResourceKey<Level> d, String n) {
        boolean clear = p == null || p.equals(BlockPos.ZERO);
        this.pos = clear ? null : p;
        this.Dimension = clear ? null : d;
        this.name = clear ? null : n;
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        setChanged();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 20, event -> event.setAndContinue(common)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return this.cache;}

    public ResourceKey<Level> getDimension() {return Dimension;}

    public String getName() {return name;}

    public BlockPos getPos() {return pos;}
}