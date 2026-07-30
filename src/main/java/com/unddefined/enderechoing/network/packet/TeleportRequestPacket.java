package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.blocks.entity.EnderEchoTunerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.portal.DimensionTransition;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.unddefined.enderechoing.EnderEchoing.GZERO;

public record TeleportRequestPacket(GlobalPos targetPos) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "teleporter_request");
    public static final Type<TeleportRequestPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, TeleportRequestPacket> STREAM_CODEC = StreamCodec.ofMember(
            (msg, buf) -> buf.writeGlobalPos(msg.targetPos),
             buf -> new TeleportRequestPacket(buf.readGlobalPos())
    );

    public static void handle(TeleportRequestPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var player = ctx.player();
            if (msg.targetPos == null) return;
            var pos = msg.targetPos.pos();
            var blockEntity = player.level().getBlockEntity(player.blockPosition().above(2));
            if (player.level().dimension().equals(msg.targetPos.dimension()))
                player.teleportTo(pos.getCenter().x, pos.getCenter().y, pos.getCenter().z);
            else player.changeDimension(new DimensionTransition(player.level().getServer().getLevel(msg.targetPos.dimension()),pos.getCenter(), player.getDeltaMovement(), player.getYRot(), player.getXRot(), DimensionTransition.DO_NOTHING));
            if (blockEntity instanceof EnderEchoTunerBlockEntity tuner) tuner.setSelectedPosition(GZERO, "");
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}
