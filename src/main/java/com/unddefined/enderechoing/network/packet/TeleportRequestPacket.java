package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.blocks.entity.EnderEchoTunerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record TeleportRequestPacket(Vec3 targetPos) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "teleporter_request");
    public static final Type<TeleportRequestPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, TeleportRequestPacket> STREAM_CODEC = StreamCodec.ofMember(
            (msg, buf) -> buf.writeVec3(msg.targetPos),
             buf -> new TeleportRequestPacket(buf.readVec3())
    );

    public static void handle(TeleportRequestPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var player = ctx.player();
            var pos = msg.targetPos;
            if (pos == null) return;
            var blockEntity = player.level().getBlockEntity(player.blockPosition().above(2));
            player.teleportTo(pos.x, pos.y, pos.z);
            if (blockEntity instanceof EnderEchoTunerBlockEntity tuner) tuner.setSelectedPosition(null, null, null);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}
