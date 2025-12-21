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

public record SetTeleportPosPacket(BlockPos targetPos, boolean targetPreseted) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "set_teleport_pos");
    public static final Type<SetTeleportPosPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, SetTeleportPosPacket> STREAM_CODEC = StreamCodec.ofMember(
            (msg, buf) -> {buf.writeBlockPos(msg.targetPos);buf.writeBoolean(msg.targetPreseted);},
             buf -> new SetTeleportPosPacket(buf.readBlockPos(), buf.readBoolean())
    );

    public static void handle(SetTeleportPosPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            EchoRenderer.targetPos = msg.targetPos;
            EchoRenderer.targetPreseted = msg.targetPreseted;
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}
