package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.client.gui.screen.PositionEditScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ReplyStructureInfoPacket(String structureName) implements CustomPacketPayload {
    public static final Type<ReplyStructureInfoPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("enderechoing", "reply_structure_info"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReplyStructureInfoPacket> STREAM_CODEC = StreamCodec.ofMember(
            (msg, buf) -> buf.writeUtf(msg.structureName),
            buf -> new ReplyStructureInfoPacket(buf.readUtf())
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}

    public static void handle(ReplyStructureInfoPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            // 在客户端更新 PositionEditScreen 中的 structure 字段
            if (context.player().level().isClientSide &&
                    Minecraft.getInstance().screen instanceof PositionEditScreen P) P.setStructure(packet.structureName);
        });
    }
}
