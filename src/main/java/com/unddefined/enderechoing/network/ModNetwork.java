package com.unddefined.enderechoing.network;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.network.packet.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = EnderEchoing.MODID)
public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1.0";
    
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        
        // 注册物品重命名数据包
        registrar.playToServer(
                ItemRenamePacket.TYPE,
                ItemRenamePacket.STREAM_CODEC,
                ItemRenamePacket::handle
        );
        
        // 注册打开编辑屏幕数据包
        registrar.playToClient(
                OpenEditScreenPacket.TYPE,
                OpenEditScreenPacket.STREAM_CODEC,
                OpenEditScreenPacket::handle
        );

        // 注册次声波粒子效果数据包
        registrar.playToClient(
                InfrasoundParticlePacket.TYPE,
                InfrasoundParticlePacket.STREAM_CODEC,
                InfrasoundParticlePacket::handle
        );

        // 注册添加效果数据包
        registrar.playToServer(
                AddEffectPacket.TYPE,
                AddEffectPacket.STREAM_CODEC,
                AddEffectPacket::handle
        );

        // 注册传送数据包
        registrar.playToServer(
                TeleportRequestPacket.TYPE,
                TeleportRequestPacket.STREAM_CODEC,
                TeleportRequestPacket::handle
        );
    }
}