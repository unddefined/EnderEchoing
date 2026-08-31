package com.unddefined.enderechoing.items;

import com.unddefined.enderechoing.blocks.entity.EnderEchoicResonatorBlockEntity;
import com.unddefined.enderechoing.network.packet.OpenEditScreenPacket;
import com.unddefined.enderechoing.server.DataComponents.MarkedPositionsManager;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

import static com.unddefined.enderechoing.server.registry.DataRegistry.*;
import static net.minecraft.core.component.DataComponents.CUSTOM_NAME;

public class EnderEchoingPearl extends Item {
    public EnderEchoingPearl(Properties properties) {
        super(properties.stacksTo(16));
    }

    public static void handleSetDataRequest(ServerPlayer player, String name,int iconIndex, ItemStack handStack, Level level) {
        var Name = name.isEmpty() ? Component.translatable("item.enderechoing.ender_echoing_pearl").getString() : name;
        var playerPos = player.blockPosition();
        var pearl = new ItemStack(ItemRegistry.ENDER_ECHOING_PEARL.get());
        boolean bound = level.getBlockEntity(player.blockPosition()) instanceof EnderEchoicResonatorBlockEntity;
        pearl.set(CUSTOM_NAME, null);
        player.setExperiencePoints(player.totalExperience - 80);

        if (handStack.getItem() instanceof EnderEchoingPearl) {
            //pearl.use()标记
            handStack.set(DataComponents.CUSTOM_NAME, Component.literal(Name));
            handStack.set(POSITION.get(), new GlobalPos(level.dimension(), playerPos));
            handStack.set(TBOUND.get(), bound);
        } else {
            //非pearl.use()标记
            var targetPosition = player.getData(EE_PEARL_POSITION.get());
            if (player.getData(EE_PEARL_AMOUNT.get()) > 0) {
                MarkedPositionsManager.getManager(player)
                        .addMarkedPosition(level.dimension(), targetPosition, name, iconIndex != -1 ? iconIndex : 0, bound);
                player.setData(EE_PEARL_AMOUNT.get(), player.getData(EE_PEARL_AMOUNT.get()) - 1);
            } else {
                var pearlStack = player.getInventory().getItem(player.getInventory().findSlotMatchingItem(pearl));
                var CopyStack = pearlStack.copyWithCount(1);
                CopyStack.set(DataComponents.CUSTOM_NAME, Component.literal(Name));
                CopyStack.set(POSITION.get(), new GlobalPos(level.dimension(), targetPosition));
                CopyStack.set(TBOUND.get(), bound);
                player.getInventory().add(CopyStack);
                pearlStack.shrink(1);
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);
        var positionData = itemStack.get(POSITION.get());
        if (level.isClientSide) return InteractionResultHolder.fail(itemStack);

        if (player.isShiftKeyDown() && positionData != null) {
            itemStack.remove(POSITION.get());
            itemStack.remove(DataComponents.CUSTOM_NAME);
            return InteractionResultHolder.success(itemStack);
        }

        if (positionData == null) PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenEditScreenPacket(
                level.getBlockEntity(player.blockPosition()) instanceof EnderEchoicResonatorBlockEntity ? "><" : "", BlockPos.ZERO));

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        var P = stack.get(POSITION.get());
        if (P != null)
            tooltip.add(Component.translatable("item.enderechoing.ender_echoing_pearl.position", P.pos().toShortString(),
                    Component.translationArg(P.dimension().location())));
        super.appendHoverText(stack, context, tooltip, tooltipFlag);
    }
}