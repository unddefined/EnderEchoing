package com.unddefined.enderechoing.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TeleporterManager extends SavedData {
    // 使用线程安全的列表存储传送器位置
    private final List<TeleporterEntry> teleporters = new CopyOnWriteArrayList<>();
    
    private static final String DATA_NAME = "enderechoing_teleporters";
    
    public static TeleporterManager get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            DimensionDataStorage storage = serverLevel.getServer().getLevel(Level.OVERWORLD).getDataStorage();
            return storage.computeIfAbsent(new Factory<>(TeleporterManager::new, (tag, provider) -> load(tag)), DATA_NAME);
        }
        return null;
    }
    
    public static TeleporterManager load(CompoundTag tag) {
        TeleporterManager manager = new TeleporterManager();
        
        ListTag list = tag.getList("teleporters", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entryTag = list.getCompound(i);
            String dimension = entryTag.getString("dimension");
            BlockPos pos = BlockPos.of(entryTag.getLong("pos"));
            
            // 我们需要在实际使用时再关联ServerLevel
            manager.teleporters.add(new TeleporterEntry(dimension, pos));
        }
        
        return manager;
    }
    
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag list = new ListTag();
        for (TeleporterEntry entry : teleporters) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("dimension", entry.dimensionLocation);
            entryTag.putLong("pos", entry.pos.asLong());
            list.add(entryTag);
        }
        tag.put("teleporters", list);
        return tag;
    }
    
    public void addTeleporter(ServerLevel level, BlockPos pos) {
        // 添加新的传送器位置
        teleporters.add(new TeleporterEntry(level.dimension().location().toString(), pos));
        setDirty();
    }
    
    public void removeTeleporter(ServerLevel level, BlockPos pos) {
        // 移除传送器位置
        String dimension = level.dimension().location().toString();
        teleporters.removeIf(entry -> entry.dimensionLocation.equals(dimension) && entry.pos.equals(pos));
        setDirty();
    }
    
    public BlockPos getNearestTeleporter(ServerLevel level, BlockPos fromPos) {
        String dimension = level.dimension().location().toString();
        BlockPos nearestPos = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (TeleporterEntry entry : teleporters) {
            // 只检查同一维度的传送器
            if (entry.dimensionLocation.equals(dimension)) {
                double distance = entry.pos.distSqr(fromPos);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestPos = entry.pos;
                }
            }
        }
        
        return nearestPos;
    }
    
    public boolean hasTeleporters() {
        return !teleporters.isEmpty();
    }
    
    // 内部类用于存储传送器位置和所在世界的信息
    private static class TeleporterEntry {
        private final String dimensionLocation;
        private final BlockPos pos;
        
        public TeleporterEntry(String dimensionLocation, BlockPos pos) {
            this.dimensionLocation = dimensionLocation;
            this.pos = pos.immutable();
        }
    }
}