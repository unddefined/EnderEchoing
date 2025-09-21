package com.unddefined.enderechoing.items;

import com.unddefined.enderechoing.network.packet.OpenEditScreenPacket;
import com.unddefined.enderechoing.server.DataComponents.PositionData;
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

import static com.unddefined.enderechoing.server.registry.DataComponentsRegistry.POSITION;

public class EnderEchoingPearl extends Item {
    public EnderEchoingPearl(Properties properties) {
        super(properties.stacksTo(8));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            // 检查玩家是否按住Shift键
            if (player.isShiftKeyDown()) {
                // 清除数据
                itemStack.remove(POSITION.get());
                itemStack.remove(DataComponents.CUSTOM_NAME);

                return InteractionResultHolder.success(itemStack);
            }

            PositionData positionData = itemStack.get(POSITION.get());
            if (positionData == null) {
                // 发送网络包打开编辑屏幕
                PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenEditScreenPacket());
                // 获取玩家当前位置并存储到物品的NBT中
                BlockPos playerPos = player.blockPosition();
                PositionData location = new PositionData(playerPos.getX(), playerPos.getY(), playerPos.getZ(), level.dimension().location().toString());
                ((ServerPlayer) player).setExperiencePoints(player.totalExperience - 80);
                // 将数据存储到物品的位置组件中
                itemStack.set(POSITION.get(), location);
            }
        }
        
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    public static void setName(ItemStack stack, String name) {
        if (!name.isEmpty()) {
            stack.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        } else {
            stack.remove(DataComponents.CUSTOM_NAME);
        }
    }

    public static void handleRenameRequest(ServerPlayer player, String name) {
        // 获取玩家主手的物品
        ItemStack stack = player.getMainHandItem();
        
        // 检查物品是否为EnderEchoingPearl
        if (stack.getItem() instanceof EnderEchoingPearl) {
            // 设置物品名称
            setName(stack, name);
            
            // 向玩家发送确认消息
            player.sendSystemMessage(Component.translatable("item.enderechoing.ender_echoing_pearl.named", name));
        }
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