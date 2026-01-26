package com.unddefined.enderechoing.server;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.blocks.EnderEchoCrystalBlock;
import com.unddefined.enderechoing.server.DataComponents.EnderEchoCrystalSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.Comparator;

import static com.unddefined.enderechoing.effects.AttackScatteredEffect.attack_scattered_modifier_id;
import static com.unddefined.enderechoing.effects.StaggerEffect.stagger_modifier_id;
import static com.unddefined.enderechoing.effects.TinnitusEffect.tinnitus_modifier_id;
import static com.unddefined.enderechoing.server.registry.DataRegistry.EE_PEARL_AMOUNT;
import static com.unddefined.enderechoing.server.registry.DataRegistry.MARKED_POSITIONS_CACHE;
import static com.unddefined.enderechoing.server.registry.MobEffectRegistry.*;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;

@EventBusSubscriber(modid = EnderEchoing.MODID)
public class ServerEvents {
    @SubscribeEvent
    public static void onExpireEffect(MobEffectEvent.Expired event) {
        if (!event.getEntity().hasEffect(TINNITUS) && event.getEntity().getAttribute(FOLLOW_RANGE) != null) {
            if (event.getEntity() instanceof Monster monster)
                monster.getAttribute(FOLLOW_RANGE).removeModifier(tinnitus_modifier_id);
        }
        if (!event.getEntity().hasEffect(STAGGER) && event.getEntity().getAttribute(MOVEMENT_SPEED) != null) {
            event.getEntity().getAttribute(MOVEMENT_SPEED).removeModifier(stagger_modifier_id);
        }
        if (!event.getEntity().hasEffect(ATTACK_SCATTERED) && event.getEntity().getAttribute(ATTACK_SPEED) != null) {
            event.getEntity().getAttribute(ATTACK_SPEED).removeModifier(attack_scattered_modifier_id);
        }
    }

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        Player player = event.getEntity();
        if (player.hasEffect(STAGGER)) {
            // 获取当前移动输入
            var movement = event.getInput();

            // 获取效果等级（用于确定偏移程度）
            int amplifier = player.getEffect(STAGGER).getAmplifier();

            // 随机偏移移动方向
            RandomSource random = player.getRandom();
            float offsetStrength = 0.1f * (amplifier + 1); // 等级越高偏移越严重

            // 添加随机偏移
            movement.forwardImpulse += (random.nextFloat() - 0.5f) * offsetStrength;
            movement.leftImpulse += (random.nextFloat() - 0.5f) * offsetStrength;
        }

    }

    @SubscribeEvent
    public static void onLivingAttack(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(ATTACK_SCATTERED)) {
            // 获取效果实例
            MobEffectInstance effectInstance = entity.getEffect(ATTACK_SCATTERED);
            if (effectInstance != null) {
                int amplifier = effectInstance.getAmplifier();

                // 根据效果等级有概率取消攻击
                RandomSource random = entity.getRandom();
                float chance = 0.3f * (amplifier + 1); // 每级增加5%的概率
                if (random.nextFloat() < chance) {
                    // 取消攻击
                    event.setCanceled(true);
                }
            }
        }
        if (entity.hasEffect(SCULK_VEIL)) entity.removeEffect(SCULK_VEIL);
    }

    @SubscribeEvent
    public static void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        var pos = player.blockPosition();
        var level = player.level();
        if (!(level.getBlockState(pos).getBlock() instanceof EnderEchoCrystalBlock)) return;
        EnderEchoCrystalSavedData.get((ServerLevel) level).getAll()
                .stream().filter(p -> p.getX() == pos.getX() && p.getZ() == pos.getZ() && p.getY() > pos.getY())
                .min(Comparator.comparingInt(BlockPos::getY))
                .ifPresent(p -> player.teleportTo(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5));
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        var manager = player.getData(MARKED_POSITIONS_CACHE);
        // 取出所有死亡点（按原顺序）
        var deaths = manager.markedPositions().stream().filter(p -> p.name().startsWith("☠")).toList();
        if (player.getData(EE_PEARL_AMOUNT) < 0 && deaths.size() <= 3) return;
        // 先删掉所有旧的死亡点（准备重建）
        manager.markedPositions().removeIf(p -> p.name().startsWith("☠"));
        // 最新的死亡点，直接加
        manager.addMarkedPosition(player.level().dimension(), player.blockPosition(),
                "☠" + Component.translatable("screen.enderechoing.last_death").getString() + "☠", 0);
        // 之前的死亡点依次下沉，最多保留 3 个
        for (int i = 0; i < Math.min(3, deaths.size()); i++) {
            var old = deaths.get(i);
            String name = "";
            switch (i) {
                case 0 -> name = "☠" + Component.translatable("screen.enderechoing.previous_death").getString() + "☠";
                case 1 -> name = "☠" + Component.translatable("screen.enderechoing.earlier_death").getString() + "☠";
                case 2 -> name = "☠" + Component.translatable("screen.enderechoing.even_earlier_death").getString() + "☠";
            }
            manager.addMarkedPosition(old.dimension(), old.pos(), name, 0);
        }
        // 消耗珍珠
        if (deaths.size() < 4) player.setData(EE_PEARL_AMOUNT, player.getData(EE_PEARL_AMOUNT) - 1);
    }

}