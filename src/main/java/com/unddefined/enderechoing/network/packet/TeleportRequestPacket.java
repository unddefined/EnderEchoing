package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TeleportRequestPacket(Vec3 targetPos) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "teleporter_request");
    public static final Type<TeleportRequestPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, TeleportRequestPacket> STREAM_CODEC = StreamCodec.ofMember(
            TeleportRequestPacket::encode,
            TeleportRequestPacket::decode
    );

    public static void encode(TeleportRequestPacket msg, FriendlyByteBuf buf) {
        buf.writeVec3(msg.targetPos);
    }

    public static TeleportRequestPacket decode(FriendlyByteBuf buf) {
        return new TeleportRequestPacket(buf.readVec3());
    }

    public static void handle(TeleportRequestPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var player = ctx.player();
            var pos = msg.targetPos;
            if (pos != null) player.teleportTo(pos.x, pos.y, pos.z);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {return TYPE;}
}
