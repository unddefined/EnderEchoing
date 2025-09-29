package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.renderer.EchoRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record SyncTeleportersPacket(List<BlockPos> teleporterPositions) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "sync_teleporters");
    public static final Type<SyncTeleportersPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, SyncTeleportersPacket> STREAM_CODEC = StreamCodec.ofMember(
            SyncTeleportersPacket::encode,
            SyncTeleportersPacket::decode
    );

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(teleporterPositions.size());
        for (BlockPos pos : teleporterPositions) {
            buf.writeBlockPos(pos);
        }
    }

    public static SyncTeleportersPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<BlockPos> positions = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            positions.add(buf.readBlockPos());
        }
        return new SyncTeleportersPacket(positions);
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            // 在客户端处理同步数据
            EchoRenderer.updateTeleporterPositions(teleporterPositions);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}