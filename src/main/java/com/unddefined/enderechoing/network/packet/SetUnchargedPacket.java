package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.blocks.entity.EnderEchoTunerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.unddefined.enderechoing.blocks.EnderEchoTunerBlock.CHARGED;

public record SetUnchargedPacket(BlockPos blockPos) implements CustomPacketPayload {
    public static final Type<SetUnchargedPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("enderechoing", "set_uncharged"));
    public static final StreamCodec<FriendlyByteBuf, SetUnchargedPacket> STREAM_CODEC = StreamCodec.ofMember(
            (msg, buf) -> buf.writeBlockPos(msg.blockPos),
            buf -> new SetUnchargedPacket(buf.readBlockPos())
    );
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getBlockEntity(blockPos) instanceof EnderEchoTunerBlockEntity tuner)
                tuner.getBlockState().setValue(CHARGED, false);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}
