package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.renderer.EchoRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record SendSyncedTeleporterPositionsPacket(List<BlockPos> list) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID,   "synced_teleporter_positions");
    public static final Type<SendSyncedTeleporterPositionsPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, SendSyncedTeleporterPositionsPacket> STREAM_CODEC = StreamCodec.ofMember(
            (msg, buf) -> {
                buf.writeInt(msg.list.size());
                msg.list.forEach(buf::writeBlockPos);
            },
            buf -> {
                List<BlockPos> list = new ArrayList<>();
                for (int i = 0; i < buf.readInt(); i++) list.add(buf.readBlockPos());
                return new SendSyncedTeleporterPositionsPacket(list);
            }
    );

    public static void handle(SendSyncedTeleporterPositionsPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> EchoRenderer.syncedTeleporterPositions = msg.list);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}
