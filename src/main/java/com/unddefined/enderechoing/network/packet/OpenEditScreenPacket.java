package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.gui.EditScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenEditScreenPacket() implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "open_edit_screen");
    public static final Type<OpenEditScreenPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, OpenEditScreenPacket> STREAM_CODEC = StreamCodec.unit(new OpenEditScreenPacket());

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            // 在客户端打开编辑屏幕
            Minecraft.getInstance().setScreen(new EditScreen(Minecraft.getInstance().screen));
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}