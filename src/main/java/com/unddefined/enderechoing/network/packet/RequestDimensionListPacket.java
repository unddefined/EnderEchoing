package com.unddefined.enderechoing.network.packet;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record RequestDimensionListPacket() implements CustomPacketPayload {
    public static final Type<RequestDimensionListPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("enderechoing", "request_dimension_list"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestDimensionListPacket> STREAM_CODEC = StreamCodec.unit(new RequestDimensionListPacket());

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                List<ResourceKey<Level>> dimensionList = new ArrayList<>();
                serverPlayer.serverLevel().registryAccess().registryOrThrow(Registries.DIMENSION).holders()
                        .forEach(holder -> dimensionList.add(holder.key()));
                // 发送回复包给客户端
                PacketDistributor.sendToPlayer(serverPlayer, new ReplyDimensionListPacket(dimensionList));
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}
