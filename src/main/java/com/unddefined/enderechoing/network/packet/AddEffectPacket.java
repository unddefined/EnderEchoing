package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record AddEffectPacket(Holder<MobEffect> effect, int duration, UUID player) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "add_mob_effect");
    public static final Type<AddEffectPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, AddEffectPacket> STREAM_CODEC = StreamCodec.ofMember(
            (msg, buf) -> {
                MobEffect.STREAM_CODEC.encode(buf, msg.effect);
                buf.writeVarInt(msg.duration);
                buf.writeUUID(msg.player);
            },
            buf -> new AddEffectPacket(MobEffect.STREAM_CODEC.decode(buf), buf.readVarInt(), buf.readUUID())
    );

    public void handle(IPayloadContext c) {
        c.enqueueWork(() -> {
            var S = c.player().level().getServer();
            if (S == null) return;
            var P = S.getPlayerList().getPlayer(player);
            if (P != null) P.addEffect(new MobEffectInstance(effect, duration));
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}