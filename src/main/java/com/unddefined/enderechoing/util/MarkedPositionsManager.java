package com.unddefined.enderechoing.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class MarkedPositionsManager extends SavedData {
    private static final String TELEPORTERS_DATA_NAME = "enderechoing_teleporters";
    private static final String MARKED_POSITIONS_DATA_NAME = "enderechoing_marked_positions";
    // 使用线程安全的列表存储传送器位置
    private final List<Teleporters> teleporters = new CopyOnWriteArrayList<>();
    private final List<MarkedPositions> markedPositions = new CopyOnWriteArrayList<>();

    public static MarkedPositionsManager getTeleporters(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) return null;

        DimensionDataStorage storage = serverLevel.getServer().getLevel(Level.OVERWORLD).getDataStorage();
        return storage.computeIfAbsent(new Factory<>(MarkedPositionsManager::new, (tag, provider) -> loadTeleporters(tag)), TELEPORTERS_DATA_NAME);
    }

    public static MarkedPositionsManager getMarkedPositions(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) return null;

        DimensionDataStorage storage = serverLevel.getServer().getLevel(Level.OVERWORLD).getDataStorage();
        return storage.computeIfAbsent(new Factory<>(MarkedPositionsManager::new, (tag, provider) -> loadMarkedPositions(tag)), MARKED_POSITIONS_DATA_NAME);
    }

    public static MarkedPositionsManager loadMarkedPositions(CompoundTag tag) {
        MarkedPositionsManager manager = new MarkedPositionsManager();

        ListTag list = tag.getList("marked_positions", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entryTag = list.getCompound(i);
            String dimension = entryTag.getString("dimension");
            BlockPos pos = BlockPos.of(entryTag.getLong("pos"));
            String name = entryTag.getString("name");
            int amountOfmarkers = entryTag.getInt("amountOfmarkers");

            // 我们需要在实际使用时再关联ServerLevel
            manager.markedPositions.add(new MarkedPositions(dimension, pos, name, amountOfmarkers));
        }
        return manager;
    }

    public static MarkedPositionsManager loadTeleporters(CompoundTag tag) {
        MarkedPositionsManager manager = new MarkedPositionsManager();

        ListTag list = tag.getList("teleporters", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entryTag = list.getCompound(i);
            String dimension = entryTag.getString("dimension");
            BlockPos pos = BlockPos.of(entryTag.getLong("pos"));

            // 我们需要在实际使用时再关联ServerLevel
            manager.teleporters.add(new Teleporters(dimension, pos));
        }

        return manager;
    }

    @Override
    public CompoundTag save(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        ListTag Teleporterslist = new ListTag();
        ListTag MarkedPositionslist = new ListTag();
        for (Teleporters entry : teleporters) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("dimension", entry.dimensionLocation);
            entryTag.putLong("pos", entry.pos.asLong());
            Teleporterslist.add(entryTag);
        }
        tag.put("teleporters", Teleporterslist);

        for (MarkedPositions entry : markedPositions) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("dimension", entry.dimensionLocation);
            entryTag.putLong("pos", entry.pos.asLong());
            entryTag.putString("name", entry.name);
            entryTag.putInt("amountOfmarkers", entry.amountOfmarkers);
            MarkedPositionslist.add(entryTag);
        }
        tag.put("marked_positions", MarkedPositionslist);
        return tag;
    }

    public void addTeleporter(ServerLevel level, BlockPos pos) {
        // 添加新的传送器位置
        teleporters.add(new Teleporters(level.dimension().location().toString(), pos));
        setDirty();
    }

    public void setMarkedPosition(ServerLevel level, BlockPos pos, String name, int addORdecrease) {
        String dimension = level.dimension().location().toString();
        boolean found = false;
        for (int i = 0; i < markedPositions.size(); i++) {
            MarkedPositions entry = markedPositions.get(i);
            if (entry.dimensionLocation.equals(dimension) && entry.pos.equals(pos) && entry.name.equals(name)) {
                int newAmount = entry.amountOfmarkers + addORdecrease;
                markedPositions.set(i, new MarkedPositions(entry.dimensionLocation, entry.pos, entry.name, newAmount));
                found = true;
                setDirty();
                break;
            }
        }
        if (!found && addORdecrease > 0) {
            markedPositions.add(new MarkedPositions(level.dimension().location().toString(), pos, name, addORdecrease));
            setDirty();
        }
        // 移除数量为0的标记位置
        markedPositions.removeIf(entry -> entry.amountOfmarkers <= 0);
    }

    public void removeTeleporter(ServerLevel level, BlockPos pos) {
        // 移除传送器位置
        String dimension = level.dimension().location().toString();
        teleporters.removeIf(entry -> entry.dimensionLocation.equals(dimension) && entry.pos.equals(pos));
        setDirty();
    }

    public void removeMarkedPosition(ServerLevel level, BlockPos pos, String name) {
        // 移除标记位置
        String dimension = level.dimension().location().toString();
        markedPositions.removeIf(entry -> entry.dimensionLocation.equals(dimension) && entry.pos.equals(pos) && entry.name.equals(name));
        setDirty();
    }

    public List<BlockPos> getNearestTeleporter(Level level, BlockPos fromPos) {
        String dimension = level.dimension().location().toString();
        BlockPos nearestPos = null;
        double nearestDistance = Double.MAX_VALUE;

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
        return teleporters.stream()
                .filter(entry -> entry.dimensionLocation.equals(currentDimension))
                .map(entry -> entry.pos)
                .collect(Collectors.toList());
    }

    public boolean hasTeleporters() {return !teleporters.isEmpty();}

    public List<Map<BlockPos, String>> getMarkedTeleportersNameMapList(List<BlockPos> posList, Level level) {
        var currentDimension = level.dimension().location().toString();
        List<Map<BlockPos, String>> M = new ArrayList<>();
        for (BlockPos P : posList) {
            Map<BlockPos, String> resultMap = new HashMap<>();
            markedPositions.stream()
                    .filter(entry -> entry.dimensionLocation.equals(currentDimension))
                    .filter(entry -> entry.pos.equals(P))
                    .forEach(entry -> resultMap.put(entry.pos, entry.name));
            M.add(resultMap);
        }
        return M;
    }

    private record Teleporters(String dimensionLocation, BlockPos pos) {
        private Teleporters(String dimensionLocation, BlockPos pos) {
            this.dimensionLocation = dimensionLocation;
            this.pos = pos.immutable();
        }
    }

    private record MarkedPositions(String dimensionLocation, BlockPos pos, String name, int amountOfmarkers) {
        private MarkedPositions(String dimensionLocation, BlockPos pos, String name, int amountOfmarkers) {
            this.dimensionLocation = dimensionLocation;
            this.pos = pos.immutable();
            this.name = name;
            this.amountOfmarkers = amountOfmarkers;

        }
    }
}