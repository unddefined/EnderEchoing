package com.unddefined.enderechoing.server.DataComponents;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VisitedStructures implements INBTSerializable<CompoundTag> {
    private final Set<VisitedStructure> visited = new HashSet<>();

    public boolean add(VisitedStructure structure) {
        return visited.add(structure);
    }

    public Set<VisitedStructure> all() {
        return Collections.unmodifiableSet(visited);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (VisitedStructure entry : visited) {
            CompoundTag compound = new CompoundTag();
            compound.putString("structure", entry.structure().location().toString());
            compound.putLong("chunk", entry.chunk().toLong());
            list.add(compound);
        }
        tag.put("visited", list);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        visited.clear();
        if (!nbt.contains("visited")) return;
        ListTag list = nbt.getList("visited", Tag.TAG_COMPOUND);
        for (Tag value : list) {
            CompoundTag compound = (CompoundTag) value;
            visited.add(new VisitedStructure(
                    ResourceKey.create(Registries.STRUCTURE, ResourceLocation.parse(compound.getString("structure"))),
                    new ChunkPos(compound.getLong("chunk"))));
        }
    }

    public record VisitedStructure(ResourceKey<Structure> structure, ChunkPos chunk) {
    }
}
