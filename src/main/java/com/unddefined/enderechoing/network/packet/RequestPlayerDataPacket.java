package com.unddefined.enderechoing.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.unddefined.enderechoing.server.registry.DataRegistry.EE_PEARL_AMOUNT;
import static com.unddefined.enderechoing.server.registry.DataRegistry.ICON_LIST;

public record RequestPlayerDataPacket() implements CustomPacketPayload {
    public static final Type<RequestPlayerDataPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("enderechoing", "request_player_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestPlayerDataPacket> STREAM_CODEC = StreamCodec.unit(new RequestPlayerDataPacket());


    public static void handle(RequestPlayerDataPacket packet, IPayloadContext c) {
        c.enqueueWork(() -> {
            if (c.player() instanceof ServerPlayer P)
                PacketDistributor.sendToPlayer(P, new ReplyPlayerDataPacket(P.getData(EE_PEARL_AMOUNT), P.getData(ICON_LIST)));
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
