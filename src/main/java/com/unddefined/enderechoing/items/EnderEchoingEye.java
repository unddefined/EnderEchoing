package com.unddefined.enderechoing.items;

import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import static com.unddefined.enderechoing.compat.curios.EnderEchoCuriosPlugin.*;

public class EnderEchoingEye extends Item implements ICurioItem {
    private static final int HEAL_INTERVAL = 50; // 5s
    private static final int XP_COST = 50;
    private static EndCrystal crystal;

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
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        showResonatorName(slotContext);
        crystal = enderEyeCurioHealTick(slotContext, 50, 50);

    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        onEnderEyeUnequip(slotContext, crystal);
    }

    @Override
    public boolean isEnderMask(SlotContext slotContext, EnderMan enderMan, ItemStack stack) {
        return slotContext.entity() instanceof Player;
    }
}
