package com.unddefined.enderechoing.server.DataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public class EnderEchoCrystalSavedData extends SavedData {
    public static final String ID = "ender_echo_crystals";
    public final Set<BlockPos> crystals = new HashSet<>();

    public EnderEchoCrystalSavedData() {
    }

    public static EnderEchoCrystalSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(EnderEchoCrystalSavedData::new, EnderEchoCrystalSavedData::load), ID);
    }

    // ===== API =====
    public void add(BlockPos pos) {
        crystals.add(pos);
        setDirty();
    }

    public void remove(BlockPos pos) {
        crystals.remove(pos);
        setDirty();
    }

    public static EnderEchoCrystalSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        var data = new EnderEchoCrystalSavedData();
        NbtUtils.readBlockPos(tag.getCompound(ID), ID).ifPresent(data.crystals::add);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (BlockPos pos : crystals) list.add(NbtUtils.writeBlockPos(pos));
        compoundTag.put(ID, list);
        return compoundTag;
    }
}