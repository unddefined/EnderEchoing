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

public record SetEchoSoundingPosPacket(BlockPos targetPos) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "set_echo_sounding_pos");
    public static final Type<SetEchoSoundingPosPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, SetEchoSoundingPosPacket> STREAM_CODEC = StreamCodec.ofMember(
            (msg, buf) -> buf.writeBlockPos(msg.targetPos),
             buf -> new SetEchoSoundingPosPacket(buf.readBlockPos())
    );

    public static void handle(SetEchoSoundingPosPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> EchoRenderer.EchoSoundingPos = msg.targetPos);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}
