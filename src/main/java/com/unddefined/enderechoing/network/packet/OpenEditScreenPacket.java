package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.gui.screen.PositionEditScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record OpenEditScreenPacket(String fieldValue) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "open_edit_screen");
    public static final Type<OpenEditScreenPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, OpenEditScreenPacket> STREAM_CODEC = StreamCodec.ofMember(
            (msg, buf) -> buf.writeUtf(msg.fieldValue),
            buf -> new OpenEditScreenPacket(buf.readUtf())
    );

    public void handle(IPayloadContext c) {c.enqueueWork(() -> Minecraft.getInstance().setScreen(new PositionEditScreen(Minecraft.getInstance().screen, fieldValue)));}

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}