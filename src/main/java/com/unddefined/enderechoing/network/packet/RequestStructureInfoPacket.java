package com.unddefined.enderechoing.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record RequestStructureInfoPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<RequestStructureInfoPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("enderechoing", "request_structure_info"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestStructureInfoPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, RequestStructureInfoPacket::pos, RequestStructureInfoPacket::new
    );

    public static void handle(RequestStructureInfoPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                var level = serverPlayer.serverLevel();
                String structureName = "no_structure";

                var iterator = level.registryAccess().registryOrThrow(Registries.STRUCTURE).holders().iterator();
                while (iterator.hasNext()) {
                    var holder = iterator.next();
                    if (level.structureManager().getStructureAt(packet.pos, holder.value()).isValid()) {
                        structureName = holder.key().location().toString();
                        break;
                    }
                }
                // 发送回复包给客户端
                PacketDistributor.sendToPlayer(serverPlayer, new ReplyStructureInfoPacket(structureName));
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}
