package com.unddefined.enderechoing.items;

import com.unddefined.enderechoing.blocks.entity.EnderEchoCrystalBlockEntity;
import com.unddefined.enderechoing.entities.EnderEchoingEyeEntity;
import com.unddefined.enderechoing.server.DataComponents.EnderEchoCrystalSavedData;
import com.unddefined.enderechoing.server.EnderEchoingEyeLocator;
import com.unddefined.enderechoing.server.registry.DataRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Comparator;

import static com.unddefined.enderechoing.Config.*;
import static com.unddefined.enderechoing.blocks.entity.EnderEchoCrystalBlockEntity.zeroUUID;
import static com.unddefined.enderechoing.compat.curios.EnderEchoCuriosPlugin.*;
import static com.unddefined.enderechoing.server.registry.DataRegistry.VISITED_STRUCTURES;

public class EnderEchoingEye extends Item implements ICurioItem {
    private EndCrystal crystal;
    private EnderEchoCrystalBlockEntity EECrystal;

    public EnderEchoingEye(Properties properties) {
        super(properties.stacksTo(8));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        if (!(level instanceof ServerLevel S)) return InteractionResultHolder.fail(itemStack);

        EnderEchoingEyeLocator.markVisitedIfInside((ServerPlayer) player);
        BlockPos targetPos = EnderEchoingEyeLocator.findNearestUnvisited(S, player.blockPosition(),
                player.getData(VISITED_STRUCTURES.get()).all());
        if (targetPos == null) return InteractionResultHolder.fail(itemStack);

        EnderEchoingEyeEntity eye = new EnderEchoingEyeEntity(level, player.getX(), player.getY(0.5D), player.getZ());
        eye.setItem(itemStack);
        eye.signalTo(targetPos);
        level.gameEvent(GameEvent.PROJECTILE_SHOOT, eye.position(), GameEvent.Context.of(player));
        level.addFreshEntity(eye);

        float pitch = Mth.lerp(level.random.nextFloat(), 0.33F, 0.5F);
        level.playSound(null, player.blockPosition(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 1.0F, pitch);
        itemStack.consume(1, player);
        player.awardStat(Stats.ITEM_USED.get(this));
        player.swing(hand, true);
        player.getCooldowns().addCooldown(this, 5 * 20);
        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public boolean canEquip(SlotContext ctx, ItemStack stack) {
        return CuriosApi.getCuriosInventory(ctx.entity())
                .map(handler -> {
                    for (int i = 0; i < handler.getEquippedCurios().getSlots(); i++) {
                        if (handler.getEquippedCurios().getStackInSlot(i).getItem().equals(Items.ENDER_EYE)) return false;
                    }
                    return ctx.identifier().equals("charm");
                }).orElse(ctx.identifier().equals("charm"));
    }

    @Override
    public void curioTick(SlotContext ctx, ItemStack stack) {
        if (!(ctx.entity() instanceof ServerPlayer player)) return;
        showResonatorName(player);
        if (player.totalExperience < EECrystal_HEAL_XP_COST.get() || player.getHealth() >= player.getMaxHealth()) return;

        if (EECrystal == null || EECrystal.getPlayerUUID().equals(zeroUUID) || !EECrystal.getPlayerUUID().equals(player.getUUID()))
            crystal = enderEyeCurioHealTick(player);
        if (crystal != null) {
            var UUID = crystal.getEntityData().get(DataRegistry.ENDER_EYE_OWNER);
            if (UUID.isPresent() && UUID.get().equals(player.getUUID())) return;
        }

        var level = (ServerLevel) player.level();
        var D = EECrystal_HEAL_DISTANCE.get();
        EnderEchoCrystalSavedData.get(level).crystals.stream().filter(c -> c.pos().dimension().equals(level.dimension()))
                .min(Comparator.comparingDouble(c -> c.pos().pos().distToCenterSqr(player.getX(), player.getY(), player.getZ())))
                .filter(c -> Math.sqrt(c.pos().pos().distToCenterSqr(player.getX(), player.getY(), player.getZ())) < D)
                .ifPresentOrElse(c -> EECrystal = (EnderEchoCrystalBlockEntity) level.getBlockEntity(c.pos().pos()), () -> EECrystal = null);
        if (EECrystal == null) return;

        if (!EECrystal.getPlayerUUID().equals(zeroUUID) && !EECrystal.getPlayerUUID().equals(player.getUUID())) return;

        EECrystal.setPlayerUUID(player.getUUID());

        if (level.getGameTime() % EECrystal_HEAL_INTERVAL.get() * 10 != 0) return;
        player.giveExperiencePoints(-EECrystal_HEAL_XP_COST.get());
        player.heal(EECrystal_HEAL_AMOUNT.get());
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof ServerPlayer player)) return;
        onEnderEyeUnequip(crystal, player);
        if (EECrystal != null) EECrystal.setPlayerUUID(null);
    }

    @Override
    public boolean isEnderMask(SlotContext slotContext, EnderMan enderMan, ItemStack stack) {
        return true;
    }
}
