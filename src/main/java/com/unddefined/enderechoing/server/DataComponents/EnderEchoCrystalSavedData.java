package com.unddefined.enderechoing.server.DataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class EnderEchoCrystalSavedData extends SavedData {
    public static final String ID = "ender_echo_crystals";
    public final Set<CrystalEntry> crystals = new HashSet<>();

    public EnderEchoCrystalSavedData() {
    }

    public static EnderEchoCrystalSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(EnderEchoCrystalSavedData::new, EnderEchoCrystalSavedData::load), ID);
    }

    public static EnderEchoCrystalSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        EnderEchoCrystalSavedData data = new EnderEchoCrystalSavedData();
        ListTag list = tag.getList(ID, Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag crystal = (CompoundTag) t;
            var dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(crystal.getString("dimension")));
            GlobalPos pos = GlobalPos.of(dimension, BlockPos.of(crystal.getLong("pos")));
            String name = crystal.getString("name");
            data.crystals.add(new CrystalEntry(pos, name));
        }
        return data;
    }

    // ===== API =====
    public void add(ResourceKey<Level> d, BlockPos p) {
        crystals.add(new CrystalEntry(GlobalPos.of(d, p), ""));
        setDirty();
    }

    public void remove(GlobalPos pos) {
        crystals.removeIf(g -> g.pos.equals(pos));
        setDirty();
    }

    public Set<CrystalEntry> getAll() {
        return Collections.unmodifiableSet(crystals);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (CrystalEntry entry : crystals) {
            CompoundTag crystal = new CompoundTag();
            crystal.putString("dimension", entry.pos().dimension().location().toString());
            crystal.putLong("pos", entry.pos().pos().asLong());
            crystal.putString("name", entry.name());
            list.add(crystal);
        }
        tag.put(ID, list);
        return tag;
    }

    public static class CrystalEntry {
        private final GlobalPos pos;
        private String name = "";

        public CrystalEntry(GlobalPos pos, String name) {
            this.pos = pos;
            this.name = name;
        }

        public GlobalPos pos() {
            return pos;
        }

        public String name() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}