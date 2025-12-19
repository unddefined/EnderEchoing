package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.items.EnderEchoingPearl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PearlRenamePacket(String name) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "item_rename");
    public static final Type<PearlRenamePacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, PearlRenamePacket> STREAM_CODEC = StreamCodec.ofMember(
            (msg, buf) -> buf.writeUtf(msg.name),
            buf -> new PearlRenamePacket(buf.readUtf())
    );

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer P) EnderEchoingPearl.handleSetDataRequest(P, name, P.getMainHandItem(), P.level());
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {return TYPE;}
}