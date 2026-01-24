package com.unddefined.enderechoing.compat.curios;

import com.unddefined.enderechoing.EnderEchoing;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.Comparator;
import java.util.Optional;

import static com.unddefined.enderechoing.server.registry.DataRegistry.ENDER_EYE_OWNER;

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
                        return ctx.identifier().equals("charm");
                    }

                    @Override
                    public void curioTick(SlotContext slotContext) {
                        if (!(slotContext.entity() instanceof ServerPlayer player)) return;
                        var level = (ServerLevel) player.level();
                        if (level.getDragonFight() != null) return;

                        if (player.totalExperience < XP_COST || player.getHealth() >= player.getMaxHealth()) return;

                        crystal = level.getEntitiesOfClass(EndCrystal.class, player.getBoundingBox().inflate(16))
                                .stream().min(Comparator.comparingDouble(c -> c.distanceToSqr(player))).orElse(null);
                        if (crystal == null) return;
                        var tag = crystal.getEntityData().get(ENDER_EYE_OWNER);
                        if (tag.isPresent() && !tag.get().equals(player.getUUID())) return;

                        crystal.getEntityData().set(ENDER_EYE_OWNER, Optional.of(player.getUUID()));

                        if (level.getGameTime() % HEAL_INTERVAL != 0) return;
                        player.giveExperiencePoints(-XP_COST);
                        player.heal(1.0F);
                    }

                    @Override
                    public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                        if (!(slotContext.entity() instanceof ServerPlayer player)) return;
                        if (crystal == null || crystal.getBeamTarget() == null || !crystal.getBeamTarget().equals(player.blockPosition().below())) return;
                        crystal.getEntityData().set(ENDER_EYE_OWNER, Optional.empty());
                    }
                }, Items.ENDER_EYE);
    }
}
