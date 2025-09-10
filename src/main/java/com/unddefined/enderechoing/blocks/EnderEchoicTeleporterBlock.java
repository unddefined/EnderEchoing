package com.unddefined.enderechoing.blocks;

import com.mojang.serialization.MapCodec;
import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.blocks.entity.EnderEchoicTeleporterBlockEntity;
import com.unddefined.enderechoing.registry.ItemRegistry;
import com.unddefined.enderechoing.util.TeleporterManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnderEchoicTeleporterBlock extends Block implements EntityBlock {

    public EnderEchoicTeleporterBlock() {
        super(Properties.of()
                .noOcclusion()
                .noTerrainParticles()
                .sound(SoundType.SCULK_SHRIEKER)
                .explosionResistance(1000.0F)
                .destroyTime(1.5F)
                .pushReaction(PushReaction.DESTROY)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return  Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnderEchoicTeleporterBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(Component.translatable("block." + EnderEchoing.MODID + ".ender_echoic_teleporter.tooltip"));

        super.appendHoverText(stack, context, tooltip, tooltipFlag);
    }
    
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(new ItemStack(ItemRegistry.ENDER_ECHOIC_TELEPORTER_ITEM.get()));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            TeleporterManager.get(level).addTeleporter(serverLevel, pos);
            // 向附近玩家发送反馈消息
            level.players().stream()
                .filter(player -> player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64.0) // 8格范围内的玩家
                .forEach(player -> player.displayClientMessage(
                    Component.translatable("block.enderechoing.ender_echoic_teleporter.placed"), true));
        }
    }
    
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            TeleporterManager.get(level).removeTeleporter(serverLevel, pos);
            // 向附近玩家发送反馈消息
            level.players().stream()
                .filter(player -> player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64.0) // 8格范围内的玩家
                .forEach(player -> player.displayClientMessage(
                    Component.translatable("block.enderechoing.ender_echoic_teleporter.removed"), true));
        }
    }

}