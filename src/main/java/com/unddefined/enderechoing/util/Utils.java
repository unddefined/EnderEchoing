package com.unddefined.enderechoing.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

import static com.unddefined.enderechoing.server.registry.ItemRegistry.ENDER_ECHOING_CORE;

public class Utils {
    public static List<ServerPlayer> getNearEchoPlayers(Level level, Player player) {
        List<ServerPlayer> playerList = new java.util.ArrayList<>();
        level.getEntities(player, new AABB(player.blockPosition()).inflate(1.5), e ->
                e instanceof ServerPlayer P && P.getMainHandItem().is(ENDER_ECHOING_CORE)
        ).forEach(e -> {
            if (e instanceof ServerPlayer P) playerList.add(P);
        });
        return playerList;
    }
}
