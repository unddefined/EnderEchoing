package com.unddefined.enderechoing.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.unddefined.enderechoing.server.registry.DataRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public record MarkedPositionsManager(List<MarkedPositionsManager.Teleporters> teleporters,
                                     List<MarkedPositionsManager.MarkedPositions> markedPositions) implements INBTSerializable<Tag> {
    public static final StreamCodec<FriendlyByteBuf, MarkedPositions> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION),
            MarkedPositions::Dimension,
            BlockPos.STREAM_CODEC,
            MarkedPositions::pos,
            ByteBufCodecs.STRING_UTF8,
            MarkedPositions::name,
            ByteBufCodecs.VAR_INT,
            MarkedPositions::iconIndex,
            MarkedPositions::new
    );

    public MarkedPositionsManager() {this(new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>());}

    public MarkedPositionsManager(List<MarkedPositionsManager.Teleporters> teleporters, List<MarkedPositionsManager.MarkedPositions> markedPositions) {
        this.teleporters = new CopyOnWriteArrayList<>(teleporters);
        this.markedPositions = new CopyOnWriteArrayList<>(markedPositions);
    }

    public static MarkedPositionsManager getManager(Player player) {return player.getData(DataRegistry.MARKED_POSITIONS_CACHE.get());}

    public void addTeleporter(Level level, BlockPos pos) {teleporters.add(new MarkedPositionsManager.Teleporters(level.dimension(), pos));}

    public boolean addMarkedPosition(ResourceKey<Level> dimension, BlockPos pos, String name, int iconIndex) {
        if (pos == null || name == null || dimension == null) return false;
        for (MarkedPositionsManager.MarkedPositions entry : markedPositions)
            if (entry.Dimension.equals(dimension) && entry.pos.equals(pos)) return false;

        markedPositions.add(new MarkedPositionsManager.MarkedPositions(dimension, pos, name, iconIndex));
        return true;
    }

    public List<BlockPos> getNearestTeleporter(Level level, BlockPos fromPos) {
        BlockPos nearestPos = null;
        double nearestDistance = Double.MAX_VALUE;
        for (MarkedPositionsManager.Teleporters entry : teleporters) {
            // 只检查同一维度的传送器
            if (entry.Dimension.equals(level.dimension())) {
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
        return teleporters.stream()
                .filter(e -> e.Dimension.equals(level.dimension()))
                .map(entry -> entry.pos).collect(Collectors.toList());
    }

    public boolean hasTeleporters() {return !teleporters.isEmpty();}

    public Map<BlockPos, String> getMarkedTeleportersMap(List<BlockPos> posList, Level level) {
        Map<BlockPos, String> resultMap = new HashMap<>();
        for (BlockPos P : posList)
            markedPositions.stream().filter(entry -> entry.Dimension.equals(level.dimension()))
                    .filter(entry -> entry.pos.equals(P))
                    .forEach(entry -> resultMap.put(entry.pos, entry.name));
        return resultMap;
    }

    @Override
    public Tag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        // Serialize teleporters list
        ListTag teleportersTag = new ListTag();
        for (MarkedPositionsManager.Teleporters teleporter : teleporters) {
            teleportersTag.add(MarkedPositionsManager.Teleporters.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), teleporter)
                    .getOrThrow(IllegalStateException::new));
        }
        tag.put("teleporters", teleportersTag);

        // Serialize marked positions list
        ListTag markedPositionsTag = new ListTag();
        for (MarkedPositionsManager.MarkedPositions markedPosition : markedPositions) {
            markedPositionsTag.add(MarkedPositionsManager.MarkedPositions.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), markedPosition)
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
                MarkedPositionsManager.Teleporters.CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), value)
                        .resultOrPartial(error -> {
                        }).ifPresent(teleporters::add);
        }

        // Deserialize marked positions list
        markedPositions.clear();
        if (tag.contains("marked_positions")) {
            ListTag markedPositionsTag = tag.getList("marked_positions", 10); // 10 is compound tag type
            for (Tag value : markedPositionsTag)
                MarkedPositionsManager.MarkedPositions.CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), value)
                        .resultOrPartial(error -> {
                        }).ifPresent(markedPositions::add);
        }
    }

    public record Teleporters(ResourceKey<Level> Dimension, BlockPos pos) {
        public static final Codec<MarkedPositionsManager.Teleporters> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(MarkedPositionsManager.Teleporters::Dimension),
                BlockPos.CODEC.fieldOf("pos").forGetter(MarkedPositionsManager.Teleporters::pos)
        ).apply(builder, MarkedPositionsManager.Teleporters::new));
    }

    public record MarkedPositions(ResourceKey<Level> Dimension, BlockPos pos, String name, int iconIndex) {
        public static final Codec<MarkedPositionsManager.MarkedPositions> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(MarkedPositionsManager.MarkedPositions::Dimension),
                BlockPos.CODEC.fieldOf("pos").forGetter(MarkedPositionsManager.MarkedPositions::pos),
                Codec.STRING.fieldOf("name").forGetter(MarkedPositionsManager.MarkedPositions::name),
                Codec.INT.fieldOf("icon").forGetter(MarkedPositionsManager.MarkedPositions::iconIndex)
        ).apply(builder, MarkedPositionsManager.MarkedPositions::new));
    }
}