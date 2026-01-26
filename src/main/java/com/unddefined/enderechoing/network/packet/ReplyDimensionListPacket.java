package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.client.gui.screen.PositionEditScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ReplyDimensionListPacket(List<ResourceKey<Level>> dimensionList) implements CustomPacketPayload {
    public static final Type<ReplyDimensionListPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("enderechoing", "reply_dimension_list"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReplyDimensionListPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION).apply(ByteBufCodecs.list()),
            ReplyDimensionListPacket::dimensionList,
            ReplyDimensionListPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}

    public static void handle(ReplyDimensionListPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().isClientSide &&
                    Minecraft.getInstance().screen instanceof PositionEditScreen P) P.setDimensionList(packet.dimensionList);
        });
    }
}
