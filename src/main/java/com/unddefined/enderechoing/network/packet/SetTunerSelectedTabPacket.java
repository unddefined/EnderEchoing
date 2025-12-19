package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.unddefined.enderechoing.server.registry.DataRegistry.SELECTED_TUNER_TAB;

public record SetTunerSelectedTabPacket(int tab) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "set_tuner_selected_tab");
    public static final Type<SetTunerSelectedTabPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, SetTunerSelectedTabPacket> STREAM_CODEC = StreamCodec.ofMember(
            (msg, buf) -> buf.writeInt(msg.tab),
            buf -> new SetTunerSelectedTabPacket(buf.readInt())
    );

    public void handle(IPayloadContext context) {context.enqueueWork(() -> context.player().setData(SELECTED_TUNER_TAB.get(), tab));}

    @Override
    public Type<? extends CustomPacketPayload> type() {return TYPE;}
}