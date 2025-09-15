package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.items.EnderEchoingPearl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ItemRenamePacket(String name) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "item_rename");
    public static final Type<ItemRenamePacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, ItemRenamePacket> STREAM_CODEC = StreamCodec.ofMember(
            ItemRenamePacket::encode,
            ItemRenamePacket::decode
    );
    
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(name);
    }
    
    public static ItemRenamePacket decode(FriendlyByteBuf buf) {
        return new ItemRenamePacket(buf.readUtf());
    }
    
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            // 确保在服务端执行
            if (context.player() instanceof ServerPlayer serverPlayer) {
                EnderEchoingPearl.handleRenameRequest(serverPlayer, name);
            }
        });
    }
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}