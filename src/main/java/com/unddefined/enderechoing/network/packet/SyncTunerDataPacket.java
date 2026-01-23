package com.unddefined.enderechoing.network.packet;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.util.IconListManager;
import com.unddefined.enderechoing.util.MarkedPositionsManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.unddefined.enderechoing.server.registry.DataRegistry.EE_PEARL_AMOUNT;
import static com.unddefined.enderechoing.server.registry.DataRegistry.ICON_LIST;
import static net.minecraft.network.codec.ByteBufCodecs.INT;

public record SyncTunerDataPacket(List<ItemStack> iconList,
                                  List<MarkedPositionsManager.MarkedPositions> markedPositionsCache,
                                  int ee_pearl_amount) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncTunerDataPacket> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
            SyncTunerDataPacket::iconList,
            MarkedPositionsManager.MarkedPositions.STREAM_CODEC.apply(ByteBufCodecs.list()),
            SyncTunerDataPacket::markedPositionsCache,
            INT.cast(),
            SyncTunerDataPacket::ee_pearl_amount,
            SyncTunerDataPacket::new
    );

    public static final Type<SyncTunerDataPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "sync_icon_list"));

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().setData(ICON_LIST.get(), new IconListManager(iconList));
            context.player().setData(EE_PEARL_AMOUNT.get(), ee_pearl_amount);
            var manager = MarkedPositionsManager.getManager(context.player());
            manager.markedPositions().clear();
            manager.markedPositions().addAll(markedPositionsCache);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}