package com.unddefined.enderechoing.blocks;

import com.unddefined.enderechoing.blocks.entity.CalibratedSculkShriekerBlockEntity;
import com.unddefined.enderechoing.server.registry.BlockRegistry;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class CalibratedSculkShriekerBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());

    protected static final VoxelShape SHAPE_NORTH = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SHAPE_SOUTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape SHAPE_EAST = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape SHAPE_WEST = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SHAPE_UP = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape SHAPE_DOWN = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public CalibratedSculkShriekerBlock() {
        super(Properties.of()
                .noOcclusion()
                .sound(SoundType.SCULK_SHRIEKER)
                .explosionResistance(1.0F)
                .destroyTime(1.5F)
                .pushReaction(PushReaction.DESTROY)
                .dynamicShape()
                .lightLevel(state -> 3)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
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
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            case UP -> SHAPE_UP;
            case DOWN -> SHAPE_DOWN;
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {return new CalibratedSculkShriekerBlockEntity(pos, state);}

    @Override
    public RenderShape getRenderShape(BlockState state) {return RenderShape.ENTITYBLOCK_ANIMATED;}


    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;
        // 方块转换
        if (stack.getItem() == ItemRegistry.ENDER_ECHOING_CORE.get()) {
            if (!player.isCreative()) stack.shrink(1);
            level.setBlock(pos, BlockRegistry.ENDER_ECHOIC_RESONATOR.get().defaultBlockState(), 3);
            return ItemInteractionResult.SUCCESS;
        }
        if (stack.getItem() == ItemRegistry.ENDER_ECHO_TUNE_CHAMBER.get()) {
            if (!player.isCreative()) stack.shrink(1);
            level.setBlock(pos, BlockRegistry.ENDER_ECHO_TUNER.get().defaultBlockState(), 3);
            return ItemInteractionResult.SUCCESS;
        }
        // 处理与物品槽位的交互
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CalibratedSculkShriekerBlockEntity shreikerEntity) {
            var itemStackInSlot = shreikerEntity.getTheItem();

            if (stack.isEmpty() && !itemStackInSlot.isEmpty()) {
                // 如果玩家手是空的，但槽位有物品，则取出物品
                player.setItemInHand(hand, itemStackInSlot);
                shreikerEntity.setTheItem(ItemStack.EMPTY);
                return ItemInteractionResult.SUCCESS;
            } else if (!stack.isEmpty() && itemStackInSlot.isEmpty()) {
                // 如果玩家手上有物品，但槽位是空的，则放入物品
                shreikerEntity.setTheItem(stack.copy());
                stack.shrink(stack.getCount());
                return ItemInteractionResult.SUCCESS;
            } else if (ItemStack.isSameItemSameComponents(stack, itemStackInSlot)) {
                // 如果玩家手上有物品，槽位有物品，但物品相同，则物品数量相加
                int max = stack.getItem().getDefaultMaxStackSize();
                int slotCount = itemStackInSlot.getCount();
                int handCount = stack.getCount();

                int total = slotCount + handCount;
                if (total <= max) {
                    // 全部能堆叠进去
                    itemStackInSlot.setCount(total);
                    stack.shrink(handCount);
                } else {
                    // 槽位满堆叠，手上留剩余
                    itemStackInSlot.setCount(max);
                    stack.setCount(total - max);
                }
                return ItemInteractionResult.SUCCESS;

            } else {
                // 如果玩家手上有物品，槽位有物品，但物品不同，则相互替换
                player.setItemInHand(hand, itemStackInSlot);
                shreikerEntity.setTheItem(stack);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CalibratedSculkShriekerBlockEntity shreikerEntity) {
                ItemStack stack = shreikerEntity.getTheItem();
                if (!stack.isEmpty()) {
                    ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                    level.addFreshEntity(itemEntity);
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {return List.of(new ItemStack(ItemRegistry.CALIBRATED_SCULK_SHRIEKER_ITEM.get()));}

}