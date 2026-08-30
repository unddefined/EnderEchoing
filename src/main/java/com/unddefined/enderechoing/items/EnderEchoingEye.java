package com.unddefined.enderechoing.items;

import com.unddefined.enderechoing.blocks.entity.EnderEchoCrystalBlockEntity;
import com.unddefined.enderechoing.server.DataComponents.EnderEchoCrystalSavedData;
import com.unddefined.enderechoing.server.registry.DataRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Comparator;

import static com.unddefined.enderechoing.Config.*;
import static com.unddefined.enderechoing.blocks.entity.EnderEchoCrystalBlockEntity.zeroUUID;
import static com.unddefined.enderechoing.compat.curios.EnderEchoCuriosPlugin.*;

public class EnderEchoingEye extends Item implements ICurioItem {
    private EndCrystal crystal;
    private EnderEchoCrystalBlockEntity EECrystal;

    public EnderEchoingEye(Properties properties) {
        super(properties.stacksTo(8));
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
