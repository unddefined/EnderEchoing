package com.unddefined.enderechoing.items;

import com.unddefined.enderechoing.blocks.entity.EnderEchoCrystalBlockEntity;
import com.unddefined.enderechoing.network.packet.SendMarkedPositionNamesPacket;
import com.unddefined.enderechoing.server.DataComponents.EnderEchoCrystalSavedData;
import com.unddefined.enderechoing.server.registry.DataRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Comparator;
import java.util.Map;

import static com.unddefined.enderechoing.blocks.entity.EnderEchoCrystalBlockEntity.nullUUID;
import static com.unddefined.enderechoing.compat.curios.EnderEchoCuriosPlugin.*;

public class EnderEchoingEye extends Item implements ICurioItem {
    private static final int HEAL_INTERVAL = 50; // 5s
    private static final int XP_COST = 100;

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
        if (player.totalExperience < XP_COST || player.getHealth() >= player.getMaxHealth()) return;

        if (EECrystal == null || EECrystal.getPlayerUUID().equals(nullUUID) || !EECrystal.getPlayerUUID().equals(player.getUUID()))
            crystal = enderEyeCurioHealTick(player, 50, 50);
        if (crystal != null) {
            var UUID = crystal.getEntityData().get(DataRegistry.ENDER_EYE_OWNER);
            if (UUID.isPresent() && UUID.get().equals(player.getUUID())) return;
        }

        var level = (ServerLevel) player.level();
        EnderEchoCrystalSavedData.get(level).crystals.stream()
                .min(Comparator.comparingDouble(c -> c.distToCenterSqr(player.getX(), player.getY(), player.getZ())))
                .filter(c -> c.distToCenterSqr(player.getX(), player.getY(), player.getZ()) < 16 * 16)
                .ifPresentOrElse(blockPos -> EECrystal = (EnderEchoCrystalBlockEntity) level.getBlockEntity(blockPos), () -> EECrystal = null);
        if (EECrystal == null) return;

        if (!EECrystal.getPlayerUUID().equals(nullUUID) && !EECrystal.getPlayerUUID().equals(player.getUUID())) return;

        EECrystal.setPlayerUUID(player.getUUID());

        if (level.getGameTime() % HEAL_INTERVAL != 0) return;
        player.giveExperiencePoints(-XP_COST);
        player.heal(2.0F);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof ServerPlayer player)) return;

        onEnderEyeUnequip(crystal, player);

        Map<BlockPos, String> posName = new java.util.HashMap<>();
        PacketDistributor.sendToPlayer(player, new SendMarkedPositionNamesPacket(posName));
        if (EECrystal != null) EECrystal.setPlayerUUID(null);
    }

    @Override
    public boolean isEnderMask(SlotContext slotContext, EnderMan enderMan, ItemStack stack) {
        return true;
    }
}
