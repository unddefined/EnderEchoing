package com.unddefined.enderechoing.blocks;

import com.unddefined.enderechoing.blocks.entity.CalibratedSculkShriekerBlockEntity;
import com.unddefined.enderechoing.blocks.entity.EnderEchoTunerBlockEntity;
import com.unddefined.enderechoing.blocks.entity.EnderEchoicResonatorBlockEntity;
import com.unddefined.enderechoing.client.renderer.EchoRenderer;
import com.unddefined.enderechoing.items.EnderEchoingPearl;
import com.unddefined.enderechoing.server.registry.BlockEntityRegistry;
import com.unddefined.enderechoing.server.registry.DataRegistry;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import com.unddefined.enderechoing.util.MarkedPositionsManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.unddefined.enderechoing.server.registry.MobEffectRegistry.SCULK_VEIL;

public class EnderEchoicResonatorBlock extends Block implements EntityBlock {
    private static int temptick = 0;

    public EnderEchoicResonatorBlock() {
        super(Properties.of()
                .noOcclusion()
                .sound(SoundType.SCULK_SHRIEKER)
                .explosionResistance(1000.0F)
                .destroyTime(1.5F)
                .pushReaction(PushReaction.DESTROY)
                .lightLevel(state -> 3)
        );
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeB, BlockEntityTicker<? super E> ticker) {
        return typeA == typeB ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {return new EnderEchoicResonatorBlockEntity(pos, state);}

    @Override
    public RenderShape getRenderShape(BlockState state) {return RenderShape.ENTITYBLOCK_ANIMATED;}

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(new ItemStack(ItemRegistry.ENDER_ECHOING_CORE.get()), new ItemStack(ItemRegistry.CALIBRATED_SCULK_SHRIEKER_ITEM.get()));
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!(entity instanceof ServerPlayer player)) return;
        if (entity.isCurrentlyGlowing()) return;
        if (temptick > 0) temptick--;
        var manager = MarkedPositionsManager.getManager(player);
        if (!manager.hasTeleporters()) return;
        //获取目的地名称
        var posList = manager.getTeleporterPositions(level);
        EchoRenderer.MarkedPositionNames = manager.getMarkedTeleportersMap(posList, level);
        BlockPos targetPos = null;
        //获取定向目的地 TODO:跨维度传送
        if (level.getBlockEntity(pos.above(2)) instanceof CalibratedSculkShriekerBlockEntity blockEntity)
            if (blockEntity.getTheItem().getItem() instanceof EnderEchoingPearl) {
                var p = blockEntity.getTheItem().get(DataRegistry.POSITION);
                targetPos = (p == null || !p.Dimension().equals(level.dimension())) ? null : p.pos();
            }
        if (temptick == 0) {
            EchoRenderer.EchoSoundingPos = pos;
            player.addEffect(new MobEffectInstance(SCULK_VEIL, 60));
            temptick = 30;
            if (targetPos != null) {
                EchoRenderer.EchoSoundingExtraRender = true;
                EchoRenderer.targetPos = targetPos.getCenter();
            } else EchoRenderer.syncedTeleporterPositions = posList;
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityRegistry.ENDER_ECHOIC_RESONATOR.get(), EnderEchoicResonatorBlockEntity::tick);
    }
}