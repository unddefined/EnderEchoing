package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.server.DataComponents.MarkedPositionsManager;
import com.unddefined.enderechoing.server.team.TeamManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ShareToTeamPacket(MarkedPositionsManager.MarkedPositions markedPosition) implements CustomPacketPayload {
    public static final Type<ShareToTeamPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            EnderEchoing.MODID, "share_marked_position_to_team"));
    public static final StreamCodec<FriendlyByteBuf, ShareToTeamPacket> STREAM_CODEC = StreamCodec.ofMember(
            (packet, buf) -> MarkedPositionsManager.MarkedPositions.STREAM_CODEC.encode(buf, packet.markedPosition()),
            buf -> new ShareToTeamPacket(MarkedPositionsManager.MarkedPositions.STREAM_CODEC.decode(buf))
    );

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer sharer)) return;
            if (TeamManager.teamOf(sharer.server, sharer.getUUID()) == null) {
                sharer.sendSystemMessage(Component.translatable("message.enderechoing.team.not_in_team"));
                return;
            }
            boolean shared = TeamManager.shareToTeam(sharer, markedPosition);
            sharer.sendSystemMessage(Component.translatable(shared
                    ? "message.enderechoing.team.share_success"
                    : "message.enderechoing.team.share_no_online_recipient"));
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
