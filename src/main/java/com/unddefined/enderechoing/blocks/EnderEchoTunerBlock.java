package com.unddefined.enderechoing.blocks;

import com.unddefined.enderechoing.blocks.entity.EnderEchoTunerBlockEntity;
import com.unddefined.enderechoing.client.gui.TunerMenu;
import com.unddefined.enderechoing.server.registry.BlockEntityRegistry;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import com.unddefined.enderechoing.util.MarkedPositionsManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.unddefined.enderechoing.server.registry.DataRegistry.EE_PEARL_AMOUNT;
import static com.unddefined.enderechoing.server.registry.DataRegistry.POSITION;
import static com.unddefined.enderechoing.server.registry.ItemRegistry.ENDER_ECHOING_PEARL;
import static net.minecraft.core.component.DataComponents.CUSTOM_NAME;

public class EnderEchoTunerBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = CalibratedSculkShriekerBlock.FACING;
    public static final BooleanProperty CHARGED = BooleanProperty.create("charged");
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
                .dynamicShape()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
        this.registerDefaultState(this.stateDefinition.any().setValue(CHARGED, false));
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeB, BlockEntityTicker<? super E> ticker) {
        return typeA == typeB ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {return Component.translatable("menu.title.enderechoing.tunermenu");}

            @Override
            public @NotNull AbstractContainerMenu createMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory, Player player) {
                return new TunerMenu(containerId, playerInventory, ContainerLevelAccess.create(level, pos));
            }

            @Override
            public void writeClientSideData(AbstractContainerMenu menu, net.minecraft.network.RegistryFriendlyByteBuf buf) {
                if (menu instanceof TunerMenu tunerMenu) tunerMenu.writeClientSideData(buf, pos);
            }
        };
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, @NotNull Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) return ItemInteractionResult.FAIL;
        if (stack.is(Items.DRAGON_BREATH) && !state.getValue(CHARGED)) {
            level.setBlock(pos, state.setValue(CHARGED, true), 3);
            stack.shrink(1);
            return ItemInteractionResult.SUCCESS;
        }
        if (hand != InteractionHand.MAIN_HAND) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!stack.is(ENDER_ECHOING_PEARL.get())) {
            if (player instanceof ServerPlayer P) P.openMenu(state.getMenuProvider(level, pos));
            return ItemInteractionResult.SUCCESS;
        }
        var stackPos = stack.get(POSITION);
        boolean result = stackPos != null && MarkedPositionsManager.getManager(player)
                .addMarkedPosition(stackPos.Dimension(), stackPos.pos(), stack.get(CUSTOM_NAME).getString(), 0);
        player.setData(EE_PEARL_AMOUNT.get(), player.getData(EE_PEARL_AMOUNT.get()) + stack.getCount() - (result ? 1 : 0));
        stack.shrink(stack.getCount());

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {builder.add(FACING, CHARGED);}

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