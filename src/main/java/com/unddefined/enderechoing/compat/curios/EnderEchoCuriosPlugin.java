package com.unddefined.enderechoing.compat.curios;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.network.packet.SendMarkedPositionNamesPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

import static com.unddefined.enderechoing.server.registry.DataRegistry.ENDER_EYE_OWNER;
import static com.unddefined.enderechoing.server.registry.DataRegistry.MARKED_POSITIONS_CACHE;
import static com.unddefined.enderechoing.server.registry.ItemRegistry.ENDER_ECHOING_EYE;

@EventBusSubscriber(modid = EnderEchoing.MODID)
public class EnderEchoCuriosPlugin {
    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent evt) {
        evt.registerItem(CuriosCapability.ITEM,
                (stack, context) -> new ICurio() {
                    private static final int HEAL_INTERVAL = 50; // 5s
                    private static final int XP_COST = 50;
                    private static EndCrystal crystal;

                    @Override
                    public ItemStack getStack() {
                        return stack;
                    }

                    @Override
                    public boolean canEquip(SlotContext ctx) {
                        return CuriosApi.getCuriosInventory(ctx.entity())
                                .map(handler -> {
                                    for (int i = 0; i < handler.getEquippedCurios().getSlots(); i++) {
                                        if (handler.getEquippedCurios().getStackInSlot(i).getItem().equals(ENDER_ECHOING_EYE.get())) return false;
                                    }
                                    return ctx.identifier().equals("charm");
                                }).orElse(ctx.identifier().equals("charm"));

                    }

                    @Override
                    public void curioTick(SlotContext slotContext) {
                        showResonatorName(slotContext);
                        crystal = enderEyeCurioHealTick(slotContext, XP_COST, HEAL_INTERVAL);
                    }

                    @Override
                    public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                        onEnderEyeUnequip(slotContext, crystal);
                    }
                }, Items.ENDER_EYE);
    }

    public static EndCrystal enderEyeCurioHealTick(SlotContext ctx, int XP_COST, int HEAL_INTERVAL) {
        if (!(ctx.entity() instanceof ServerPlayer player)) return null;
        var level = (ServerLevel) player.level();
        if (level.getDragonFight() != null) return null;

        if (player.totalExperience < XP_COST || player.getHealth() >= player.getMaxHealth()) return null;

        EndCrystal crystal = level.getEntitiesOfClass(EndCrystal.class, player.getBoundingBox().inflate(16))
                .stream().min(Comparator.comparingDouble(c -> c.distanceToSqr(player))).orElse(null);
        if (crystal == null) return null;
        var tag = crystal.getEntityData().get(ENDER_EYE_OWNER);
        if (tag.isPresent() && !tag.get().equals(player.getUUID())) return null;

        crystal.getEntityData().set(ENDER_EYE_OWNER, Optional.of(player.getUUID()));

        if (level.getGameTime() % HEAL_INTERVAL != 0) return crystal;
        player.giveExperiencePoints(-XP_COST);
        player.heal(1.0F);
        return crystal;
    }

    public static void showResonatorName(SlotContext ctx) {
        if (!(ctx.entity() instanceof ServerPlayer player)) return;
        var level = (ServerLevel) player.level();
        var manager = player.getData(MARKED_POSITIONS_CACHE);
        if (manager.teleporters().isEmpty() && manager.markedPositions().isEmpty()) return;

        Map<BlockPos, String> posName = new java.util.HashMap<>();
        var map = manager.getMarkedTeleportersMap(manager.getTeleporterPositions(level), level);
        var min = map.keySet().stream().min(Comparator.comparingDouble(e -> e.distToCenterSqr(player.position()))).get();
        if (min.distToCenterSqr(player.position()) < 9) posName.put(min, map.get(min));
        PacketDistributor.sendToPlayer(player, new SendMarkedPositionNamesPacket(posName));
    }

    public static void onEnderEyeUnequip(SlotContext slotContext, EndCrystal crystal){
        if (!(slotContext.entity() instanceof ServerPlayer player)) return;
        Map<BlockPos, String> posName = new java.util.HashMap<>();
        PacketDistributor.sendToPlayer(player, new SendMarkedPositionNamesPacket(posName));
        if (crystal != null) crystal.getEntityData().set(ENDER_EYE_OWNER, Optional.empty());
    }
}
