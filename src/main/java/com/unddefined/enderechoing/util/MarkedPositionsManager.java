package com.unddefined.enderechoing.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.unddefined.enderechoing.blocks.entity.EnderEchoicResonatorBlockEntity;
import com.unddefined.enderechoing.server.registry.DataRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public record MarkedPositionsManager(List<Teleporters> teleporters,
                                     List<MarkedPositions> markedPositions) implements INBTSerializable<Tag> {
    public MarkedPositionsManager() {this(new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>());}

    public MarkedPositionsManager(List<Teleporters> teleporters, List<MarkedPositions> markedPositions) {
        this.teleporters = new CopyOnWriteArrayList<>(teleporters);
        this.markedPositions = new CopyOnWriteArrayList<>(markedPositions);
    }

    public static MarkedPositionsManager getManager(Player player) {return player.getData(DataRegistry.MARKED_POSITIONS_CACHE.get());}

    public void addTeleporter(Level level, BlockPos pos) {teleporters.add(new Teleporters(level.dimension().location().toString(), pos));}

    public void addMarkedPosition(Level level, BlockPos pos, String name) {
        String dimension = level.dimension().location().toString();
        for (MarkedPositions entry : markedPositions) {
            if (entry.dimensionLocation.equals(dimension) && entry.pos.equals(pos) && entry.name.equals(name)) {
                return;
            }
        }
        markedPositions.add(new MarkedPositions(level.dimension().location().toString(), pos, name));
    }

    public void removeMarkedPosition(Level level, BlockPos pos, String name) {
        // 移除标记位置
        String dimension = level.dimension().location().toString();
        markedPositions.removeIf(entry -> entry.dimensionLocation.equals(dimension) && entry.pos.equals(pos) && entry.name.equals(name));
    }

    public List<BlockPos> getNearestTeleporter(Level level, BlockPos fromPos) {
        String dimension = level.dimension().location().toString();
        BlockPos nearestPos = null;
        double nearestDistance = Double.MAX_VALUE;
        teleporters.removeIf(e -> !(level.getBlockEntity(e.pos) instanceof EnderEchoicResonatorBlockEntity));
        for (Teleporters entry : teleporters) {
            // 只检查同一维度的传送器
            if (entry.dimensionLocation.equals(dimension)) {
                double distance = entry.pos.distSqr(fromPos);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestPos = entry.pos;
                }
            }
        }
        return Collections.singletonList(nearestPos);
    }

    public List<BlockPos> getTeleporterPositions(Level level) {
        String currentDimension = level.dimension().location().toString();
        teleporters.removeIf(e -> !(level.getBlockEntity(e.pos) instanceof EnderEchoicResonatorBlockEntity));
        return teleporters.stream()
                .filter(entry -> entry.dimensionLocation.equals(currentDimension))
                .map(entry -> entry.pos)
                .collect(Collectors.toList());
    }

    public boolean hasTeleporters() {return !teleporters.isEmpty();}

    public Map<BlockPos, String> getMarkedTeleportersMap(List<BlockPos> posList, Level level) {
        teleporters.removeIf(e -> !(level.getBlockEntity(e.pos) instanceof EnderEchoicResonatorBlockEntity));
        var currentDimension = level.dimension().location().toString();
        Map<BlockPos, String> resultMap = new HashMap<>();
        for (BlockPos P : posList)
            markedPositions.stream().filter(entry -> entry.dimensionLocation.equals(currentDimension))
                    .filter(entry -> entry.pos.equals(P))
                    .forEach(entry -> resultMap.put(entry.pos, entry.name));
        return resultMap;
    }

    @Override
    public Tag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        // Serialize teleporters list
        ListTag teleportersTag = new ListTag();
        for (Teleporters teleporter : teleporters) {
            teleportersTag.add(Teleporters.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), teleporter)
                    .getOrThrow(IllegalStateException::new));
        }
        tag.put("teleporters", teleportersTag);

        // Serialize marked positions list
        ListTag markedPositionsTag = new ListTag();
        for (MarkedPositions markedPosition : markedPositions) {
            markedPositionsTag.add(MarkedPositions.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), markedPosition)
                    .getOrThrow(IllegalStateException::new));
        }
        tag.put("marked_positions", markedPositionsTag);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag nbt) {
        if (!(nbt instanceof CompoundTag tag)) return;

        // Deserialize teleporters list
        teleporters.clear();
        if (tag.contains("teleporters")) {
            ListTag teleportersTag = tag.getList("teleporters", 10); // 10 is compound tag type
            for (Tag value : teleportersTag)
                Teleporters.CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), value)
                        .resultOrPartial(error -> {
                        }).ifPresent(teleporters::add);
        }

        // Deserialize marked positions list
        markedPositions.clear();
        if (tag.contains("marked_positions")) {
            ListTag markedPositionsTag = tag.getList("marked_positions", 10); // 10 is compound tag type
            for (Tag value : markedPositionsTag)
                MarkedPositions.CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), value)
                        .resultOrPartial(error -> {
                        }).ifPresent(markedPositions::add);
        }
    }

    public record Teleporters(String dimensionLocation, BlockPos pos) {
        public static final Codec<Teleporters> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Codec.STRING.fieldOf("dimension").forGetter(Teleporters::dimensionLocation),
                BlockPos.CODEC.fieldOf("pos").forGetter(Teleporters::pos)
        ).apply(builder, Teleporters::new));

        public Teleporters(String dimensionLocation, BlockPos pos) {
            this.dimensionLocation = dimensionLocation;
            this.pos = pos.immutable();
        }
    }

    public record MarkedPositions(String dimensionLocation, BlockPos pos, String name) {
        public static final Codec<MarkedPositions> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Codec.STRING.fieldOf("dimension").forGetter(MarkedPositions::dimensionLocation),
                BlockPos.CODEC.fieldOf("pos").forGetter(MarkedPositions::pos),
                Codec.STRING.fieldOf("name").forGetter(MarkedPositions::name)
        ).apply(builder, MarkedPositions::new));

        public MarkedPositions(String dimensionLocation, BlockPos pos, String name) {
            this.dimensionLocation = dimensionLocation;
            this.pos = pos.immutable();
            this.name = name;
        }
    }
}