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

import java.util.HashMap;
import java.util.Map;

public record SendMarkedPositionNamesPacket(Map<BlockPos, String> MarkedPositionNames) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID,  "send_marked_position_names");
    public static final Type<SendMarkedPositionNamesPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, SendMarkedPositionNamesPacket> STREAM_CODEC = StreamCodec.ofMember(
            (msg, buf) -> {
                buf.writeInt(msg.MarkedPositionNames.size());
                msg.MarkedPositionNames.forEach((pos, name) -> {
                    buf.writeBlockPos(pos);
                    buf.writeUtf(name);
                });
            },
            buf -> {
                Map<BlockPos, String> markedPositionNames = new HashMap<>();
                for (int i = 0; i < buf.readInt(); i++) markedPositionNames.put(buf.readBlockPos(), buf.readUtf());
                return new SendMarkedPositionNamesPacket(markedPositionNames);
            }
    );

    public static void handle(SendMarkedPositionNamesPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> EchoRenderer.MarkedPositionNames = msg.MarkedPositionNames);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}
