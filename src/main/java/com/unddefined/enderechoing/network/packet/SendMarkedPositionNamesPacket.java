package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.renderer.EchoRenderer;
import com.unddefined.enderechoing.client.renderer.PositionNameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record SendMarkedPositionNamesPacket(Map<BlockPos, String> markedPositionNames) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "send_marked_position_names");
    public static final Type<SendMarkedPositionNamesPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SendMarkedPositionNamesPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, BlockPos.STREAM_CODEC, ByteBufCodecs.STRING_UTF8, 256),
            SendMarkedPositionNamesPacket::markedPositionNames, SendMarkedPositionNamesPacket::new);

    public static void handle(SendMarkedPositionNamesPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (msg.markedPositionNames.size() > 1) EchoRenderer.MarkedPositionNames = msg.markedPositionNames();
            else PositionNameRenderer.posName = msg.markedPositionNames();
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}