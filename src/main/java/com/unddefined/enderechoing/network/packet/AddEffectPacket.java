package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AddEffectPacket(Holder<MobEffect> effect, int duration) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "add_mob_effect");
    public static final Type<AddEffectPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, AddEffectPacket> STREAM_CODEC = StreamCodec.ofMember(
            AddEffectPacket::encode, AddEffectPacket::decode);

    public void encode(RegistryFriendlyByteBuf buf) {
        MobEffect.STREAM_CODEC.encode(buf, effect);
        buf.writeVarInt(duration);
    }

    public static AddEffectPacket decode(RegistryFriendlyByteBuf buf) {
        Holder<MobEffect> effect = MobEffect.STREAM_CODEC.decode(buf);
        int duration = buf.readVarInt();
        return new AddEffectPacket(effect, duration);
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            // 确保在服务端执行
            if (context.player() instanceof ServerPlayer serverPlayer) {
                serverPlayer.addEffect(new MobEffectInstance(effect, duration));
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}