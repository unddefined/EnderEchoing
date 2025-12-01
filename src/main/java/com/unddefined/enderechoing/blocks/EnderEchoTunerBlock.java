package com.unddefined.enderechoing.blocks;

import com.unddefined.enderechoing.blocks.entity.EnderEchoTunerBlockEntity;
import com.unddefined.enderechoing.server.registry.BlockEntityRegistry;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnderEchoTunerBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());
    protected static final VoxelShape SHAPE_UP = Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(6.0D, 14.0D, 6.0D, 10.0D, 16.0D, 10.0D));
    protected static final VoxelShape SHAPE_DOWN = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public EnderEchoTunerBlock() {
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {builder.add(FACING);}

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getNearestLookingDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case UP, SOUTH, NORTH, WEST, EAST -> SHAPE_UP;
            case DOWN -> SHAPE_DOWN;
        };
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(new ItemStack(ItemRegistry.ENDER_ECHO_TUNE_CHAMBER.get()), new ItemStack(ItemRegistry.CALIBRATED_SCULK_SHRIEKER_ITEM.get()));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {return RenderShape.ENTITYBLOCK_ANIMATED;}

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {return new EnderEchoTunerBlockEntity(pos, state);}

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityRegistry.ENDER_ECHO_TUNER.get(), EnderEchoTunerBlockEntity::tick);
    }
}