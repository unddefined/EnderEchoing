package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetPlayerAnimationPacket() implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "set_player_animation");
    public static final Type<SetPlayerAnimationPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, SetPlayerAnimationPacket> STREAM_CODEC = StreamCodec.ofMember(
            (msg, buf) -> {}, buf -> new SetPlayerAnimationPacket()
    );

    public void handle(IPayloadContext c) {
        c.enqueueWork(() -> {
            if (c.player() instanceof AbstractClientPlayer clientPlayer) {
                var animationStack = PlayerAnimationAccess.getPlayerAnimLayer(clientPlayer);
                animationStack.removeLayer(42);
                var playerAnimation = new ModifierLayer<>();
                var anim = PlayerAnimationRegistry.getAnimation(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echoing_core.player.use"));
                if (anim != null) playerAnimation.setAnimation(anim.playAnimation());
                animationStack.addAnimLayer(42, playerAnimation);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}