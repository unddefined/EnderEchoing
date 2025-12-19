package com.unddefined.enderechoing.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public record IconListManager(List<ItemStack> icons) implements INBTSerializable<Tag> {
    public static final Codec<IconListManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.listOf().fieldOf("icons").forGetter(manager -> manager.icons)
    ).apply(instance, IconListManager::new));

    public IconListManager(IAttachmentHolder iAttachmentHolder) {this(new ArrayList<>());}

    public IconListManager(List<ItemStack> icons) {this.icons = new ArrayList<>(icons);}

    public List<ItemStack> reset(IconListManager manager) {
        if (manager == null) manager = new IconListManager(new ArrayList<>());
        manager.icons.clear();
        manager.icons.add(new ItemStack(ItemRegistry.ENDER_ECHOING_PEARL.get()));
        manager.icons.add(new ItemStack(Items.FILLED_MAP));
        manager.icons.add(new ItemStack(Items.IRON_SWORD));
        manager.icons.add(new ItemStack(Items.BELL));
        manager.icons.add(new ItemStack(Items.IRON_PICKAXE));
        manager.icons.add(new ItemStack(Items.LEATHER));
        manager.icons.add(new ItemStack(Items.WATER_BUCKET));
        manager.icons.add(new ItemStack(Items.CHEST_MINECART));
        manager.icons.add(new ItemStack(Items.FURNACE));
        manager.icons.add(new ItemStack(Items.REDSTONE_TORCH));
        return manager.icons;
    }

    @Override
    public Tag serializeNBT(HolderLookup.Provider provider) {
        return CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag tag) {
        if (!(tag instanceof CompoundTag compoundTag)) {
            reset(this);
            return;
        }

        CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), compoundTag).result()
                .ifPresentOrElse(parsed -> {
                    this.icons.clear();
                    this.icons.addAll(parsed.icons);
                }, () -> reset(this));
    }
}