package com.unddefined.enderechoing.server.DataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class EnderEchoCrystalSavedData extends SavedData {
    public static final String ID = "ender_echo_crystals";
    public final Set<BlockPos> crystals = new HashSet<>();

    public EnderEchoCrystalSavedData() {}

    public static EnderEchoCrystalSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(EnderEchoCrystalSavedData::new, EnderEchoCrystalSavedData::load), ID);
    }

    public static EnderEchoCrystalSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        var data = new EnderEchoCrystalSavedData();
        Arrays.stream(tag.getLongArray(ID)).forEach(l -> data.crystals.add(BlockPos.of(l)));
        return data;
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

    public Set<BlockPos> getAll() {return Collections.unmodifiableSet(crystals);}

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.@NotNull Provider registries) {
        compoundTag.putLongArray(ID, crystals.stream().mapToLong(BlockPos::asLong).toArray());
        return compoundTag;
    }
}