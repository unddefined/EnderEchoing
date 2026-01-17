package com.unddefined.enderechoing.blocks;

import com.unddefined.enderechoing.blocks.entity.EnderEchoCrystalBlockEntity;
import com.unddefined.enderechoing.network.packet.SendSyncedTeleporterPositionsPacket;
import com.unddefined.enderechoing.network.packet.SetEchoSoundingPosPacket;
import com.unddefined.enderechoing.server.DataComponents.EnderEchoCrystalSavedData;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
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
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EnderEchoCrystalBlock extends Block implements EntityBlock {
    public EnderEchoCrystalBlock() {
        super(Properties.of()
                .noOcclusion()
                .sound(SoundType.SCULK_SHRIEKER)
                .explosionResistance(1000.0F)
                .destroyTime(1.5F)
                .pushReaction(PushReaction.DESTROY)
                .lightLevel(state -> 3)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {return new EnderEchoCrystalBlockEntity(pos, state);}

    @Override
    public RenderShape getRenderShape(BlockState state) {return RenderShape.ENTITYBLOCK_ANIMATED;}

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(new ItemStack(ItemRegistry.ENDER_ECHO_CRYSTAL.get()), new ItemStack(ItemRegistry.CALIBRATED_SCULK_SHRIEKER_ITEM.get()));
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!(entity instanceof ServerPlayer player)) return;
        var crystals = EnderEchoCrystalSavedData.get((ServerLevel) level).getAll();
        if (crystals.size() < 2) return;
        if (entity.isCurrentlyGlowing()) return;
        PacketDistributor.sendToPlayer(player, new SetEchoSoundingPosPacket(pos));
        List<BlockPos> posList = new ArrayList<>();
        crystals.stream().filter(p -> p.distSqr(pos) <= 64*64).forEach(posList::add);
        PacketDistributor.sendToPlayer(player, new SendSyncedTeleporterPositionsPacket(posList));
       if(player.isShiftKeyDown()) posList.stream().filter(p -> (p.getX() == pos.getX()) && (p.getZ() == pos.getZ()) && (p.getY() < pos.getY()))
               .min(Comparator.comparingInt(BlockPos::getY))
               .ifPresent(p -> player.teleportTo(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5));
    }
}