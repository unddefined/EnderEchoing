package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.ClientEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.unddefined.enderechoing.client.particles.ParticleMethods.spawnInfrasoundParticles;

public record InfrasoundParticlePacket(Vec3 center, float radius) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "infrasound_particle");
    public static final Type<InfrasoundParticlePacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, InfrasoundParticlePacket> STREAM_CODEC = StreamCodec.ofMember(
            InfrasoundParticlePacket::encode,
            InfrasoundParticlePacket::decode
    );

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(center.x);
        buf.writeDouble(center.y);
        buf.writeDouble(center.z);
        buf.writeFloat(radius);
    }

    public static InfrasoundParticlePacket decode(FriendlyByteBuf buf) {
        return new InfrasoundParticlePacket(
                new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                buf.readFloat()
        );
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            // 在客户端播放粒子效果
            if (Minecraft.getInstance().level != null) {
                spawnInfrasoundParticles(Minecraft.getInstance().level, center, radius);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}