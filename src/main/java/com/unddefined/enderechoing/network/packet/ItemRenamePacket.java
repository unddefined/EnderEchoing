package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.items.EnderEchoingPearl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ItemRenamePacket(String name) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "item_rename");
    public static final Type<ItemRenamePacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, ItemRenamePacket> STREAM_CODEC = StreamCodec.ofMember(
            ItemRenamePacket::encode,
            ItemRenamePacket::decode
    );

    public static ItemRenamePacket decode(FriendlyByteBuf buf) {return new ItemRenamePacket(buf.readUtf());}

    public void encode(FriendlyByteBuf buf) {buf.writeUtf(name);}

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ItemStack stack = player.getMainHandItem();
                var level = player.level();
                EnderEchoingPearl.handleSetDataRequest(player, name, stack, level);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {return TYPE;}
}