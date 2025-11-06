package com.unddefined.enderechoing.items;

import com.unddefined.enderechoing.network.packet.OpenEditScreenPacket;
import com.unddefined.enderechoing.server.DataComponents.PositionData;
import com.unddefined.enderechoing.util.MarkedPositionsManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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

import static com.unddefined.enderechoing.server.registry.DataComponentsRegistry.POSITION;

public class EnderEchoingPearl extends Item {
    public EnderEchoingPearl(Properties properties) {super(properties.stacksTo(8));}

    public static void handleSetDataRequest(ServerPlayer player, String name, ItemStack stack, Level level) {
        var Name = name.isEmpty() ? Component.translatable("item.enderechoing.ender_echoing_pearl").getString() : name;
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(Name));
        player.sendSystemMessage(Component.translatable("item.enderechoing.ender_echoing_pearl.named", name));
        var playerPos = player.blockPosition();
        var location = new PositionData(playerPos.getX(), playerPos.getY(), playerPos.getZ(), level.dimension().location().toString());
        player.setExperiencePoints(player.totalExperience - 80);
        stack.set(POSITION.get(), location);
        MarkedPositionsManager.getMarkedPositions(level).setMarkedPosition((ServerLevel) level, playerPos, Name, stack.getCount());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);
        var positionData = itemStack.get(POSITION.get());
        if (level.isClientSide) return InteractionResultHolder.fail(itemStack);

        if (player.isShiftKeyDown() && positionData != null) {
            var playerPos = new BlockPos(positionData.x(), positionData.y(), positionData.z());
            MarkedPositionsManager.getMarkedPositions(level).setMarkedPosition((ServerLevel) level, playerPos,
                    itemStack.get(DataComponents.CUSTOM_NAME).toString(), -itemStack.getCount());

            itemStack.remove(POSITION.get());
            itemStack.remove(DataComponents.CUSTOM_NAME);
            return InteractionResultHolder.success(itemStack);
        }
        if (positionData == null) PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenEditScreenPacket());

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        PositionData positionData = stack.get(POSITION.get());
        if (positionData != null) {
            int x = positionData.x();
            int y = positionData.y();
            int z = positionData.z();
            String dimension = positionData.dimension();
            tooltip.add(Component.translatable("item.enderechoing.ender_echoing_pearl.position", x, y, z, dimension));
        }

        super.appendHoverText(stack, context, tooltip, tooltipFlag);
    }
}