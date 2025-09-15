package com.unddefined.enderechoing.util;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.server.registry.BlockRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
@EventBusSubscriber(modid = EnderEchoing.MODID)
public class SculkShriekerCalibrater {
    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        // 检查是否是右键点击方块
        ItemStack stack = event.getItemStack();
        // 检查玩家是否使用紫水晶碎片
        if (stack.getItem() == Items.AMETHYST_SHARD) {
            // 获取被点击的方块状态
            BlockState blockState = event.getLevel().getBlockState(event.getPos());
            // 检查被点击的方块是否是Sculk Shrieker
            if (blockState.getBlock() == Blocks.SCULK_SHRIEKER) {
                // 检查Sculk Shrieker的can_summon属性是否为true
                if (blockState.getValue(BlockStateProperties.CAN_SUMMON)) {
                    // 检查玩家是否拥有足够的紫水晶碎片(至少4个)
                    if (stack.getCount() >= 4) {
                        // 只在服务端执行逻辑
                        if (!event.getLevel().isClientSide()) {
                            // 消耗4个紫水晶碎片(创造模式玩家不消耗)
                            if (!event.getEntity().isCreative()) {
                                stack.shrink(4);
                            }
                            // 将方块替换为CalibratedSculkShrieker
                            event.getLevel().setBlock(event.getPos(), BlockRegistry.CALIBRATED_SCULK_SHRIEKER.get().defaultBlockState(), 3);
                        }
                        // 设置交互结果为成功
                        if (!event.getLevel().isClientSide()) {
                            event.setCanceled(true);
                            event.setCancellationResult(InteractionResult.SUCCESS);
                        }
                    }
                    //播放Sculk音效
                    event.getLevel().playSound(null, event.getPos(), SoundEvents.SCULK_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    event.getLevel().playSound(null, event.getPos(), SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 0.3F, 1.0F);
                    event.getLevel().gameEvent(event.getEntity(), GameEvent.BLOCK_PLACE, event.getPos());
                }
            }
        }
    }
}
