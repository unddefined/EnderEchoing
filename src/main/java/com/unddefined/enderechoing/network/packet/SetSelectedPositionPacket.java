package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.blocks.entity.EnderEchoTunerBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetSelectedPositionPacket(BlockPos blockPos, BlockPos selectedPos, ResourceKey<Level> dimension,
                                        String name) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, SetSelectedPositionPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            SetSelectedPositionPacket::blockPos,
            BlockPos.STREAM_CODEC,
            SetSelectedPositionPacket::selectedPos,
            ResourceKey.streamCodec(Registries.DIMENSION),
            SetSelectedPositionPacket::dimension,
            ByteBufCodecs.STRING_UTF8,
            SetSelectedPositionPacket::name,
            SetSelectedPositionPacket::new
    );

    public static final Type<SetSelectedPositionPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "set_selected_position"));

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            BlockEntity be = player.level().getBlockEntity(blockPos);
            if (be instanceof EnderEchoTunerBlockEntity tuner) tuner.setSelectedPosition(selectedPos, dimension, name);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}