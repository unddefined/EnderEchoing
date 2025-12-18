package com.unddefined.enderechoing.items;

import com.unddefined.enderechoing.network.packet.OpenEditScreenPacket;
import com.unddefined.enderechoing.server.DataComponents.PositionData;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
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

import static com.unddefined.enderechoing.server.registry.DataRegistry.POSITION;
import static net.minecraft.core.component.DataComponents.CUSTOM_NAME;

public class EnderEchoingPearl extends Item {
    public static BlockPos targetPosition = null;

    public EnderEchoingPearl(Properties properties) {super(properties.stacksTo(8));}

    public static void handleSetDataRequest(ServerPlayer player, String name, ItemStack handStack, Level level, String icon) {
        var Name = name.isEmpty() ? Component.translatable("item.enderechoing.ender_echoing_pearl").getString() : name;
        var playerPos = player.blockPosition();
        var pearl = new ItemStack(ItemRegistry.ENDER_ECHOING_PEARL.get());
        pearl.set(CUSTOM_NAME, null);
        player.setExperiencePoints(player.totalExperience - 80);

        if (handStack.getItem() instanceof EnderEchoingPearl) {
            //pearl.use()标记
            handStack.set(DataComponents.CUSTOM_NAME, Component.literal(Name));
            handStack.set(POSITION.get(), new PositionData(level.dimension(), playerPos));
        } else {
            //非pearl.use()标记
            var pearlStack = player.getInventory().getItem(player.getInventory().findSlotMatchingItem(pearl));
            var CopyStack = pearlStack.copyWithCount(1);
            CopyStack.set(DataComponents.CUSTOM_NAME, Component.literal(Name));
            CopyStack.set(POSITION.get(), new PositionData(level.dimension(), targetPosition));
            player.getInventory().add(CopyStack);
            pearlStack.shrink(1);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);
        var positionData = itemStack.get(POSITION.get());
        if (level.isClientSide) return InteractionResultHolder.fail(itemStack);

        if (player.isShiftKeyDown() && positionData != null) {
            MarkedPositionsManager.getManager(player).removeMarkedPosition(positionData.Dimension(), positionData.pos(), itemStack.get(DataComponents.CUSTOM_NAME).toString());
            itemStack.remove(POSITION.get());
            itemStack.remove(DataComponents.CUSTOM_NAME);
            return InteractionResultHolder.success(itemStack);
        }

        if (positionData == null) PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenEditScreenPacket(""));

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        var P = stack.get(POSITION.get());
        if (P != null)
            tooltip.add(Component.translatable("item.enderechoing.ender_echoing_pearl.position", P.pos().toShortString(),
                    Component.translatable(P.Dimension().location().toLanguageKey())));
        super.appendHoverText(stack, context, tooltip, tooltipFlag);
    }
}