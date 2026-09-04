package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.client.gui.screen.PositionEditScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

import static net.minecraft.network.codec.ByteBufCodecs.INT;

public record ReplyPlayerDataPacket(int amount, List<ItemStack> iconList) implements CustomPacketPayload {
    public static final Type<ReplyPlayerDataPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("enderechoing", "reply_player_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReplyPlayerDataPacket> STREAM_CODEC = StreamCodec.composite(
            INT.cast(),
            ReplyPlayerDataPacket::amount,
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ReplyPlayerDataPacket::iconList,
            ReplyPlayerDataPacket::new
    );

    @OnlyIn(Dist.CLIENT)
    public static void handle(ReplyPlayerDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().isClientSide && Minecraft.getInstance().screen instanceof PositionEditScreen P)
                P.tabBar.setPlayerData(packet.amount(), packet.iconList);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
