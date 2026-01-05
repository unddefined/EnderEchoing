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
                PearlRenamePacket.TYPE,
                PearlRenamePacket.STREAM_CODEC,
                PearlRenamePacket::handle
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

        registrar.playToServer(
                SetSelectedPositionPacket.TYPE,
                SetSelectedPositionPacket.STREAM_CODEC,
                SetSelectedPositionPacket::handle
        );

        registrar.playToServer(
                GivePlayerPearlPacket.TYPE,
                GivePlayerPearlPacket.STREAM_CODEC,
                GivePlayerPearlPacket::handle
        );

        // 注册同步Tuner数据包
        registrar.playToServer(
                SyncTunerDataPacket.TYPE,
                SyncTunerDataPacket.STREAM_CODEC,
                SyncTunerDataPacket::handle
        );

        registrar.playToServer(
                SetTunerSelectedTabPacket.TYPE,
                SetTunerSelectedTabPacket.STREAM_CODEC,
                SetTunerSelectedTabPacket::handle
        );
        registrar.playToClient(
                SetTeleportPosPacket.TYPE,
                SetTeleportPosPacket.STREAM_CODEC,
                SetTeleportPosPacket::handle
        );
        registrar.playToClient(
                SetEchoSoundingPosPacket.TYPE,
                SetEchoSoundingPosPacket.STREAM_CODEC,
                SetEchoSoundingPosPacket::handle
        );
        registrar.playToClient(
                SetPlayerAnimationPacket.TYPE,
                SetPlayerAnimationPacket.STREAM_CODEC,
                SetPlayerAnimationPacket::handle
        );
        registrar.playToClient(
                SendMarkedPositionNamesPacket.TYPE,
                SendMarkedPositionNamesPacket.STREAM_CODEC,
                SendMarkedPositionNamesPacket::handle
        );
        registrar.playToClient(
                SendSyncedTeleporterPositionsPacket.TYPE,
                SendSyncedTeleporterPositionsPacket.STREAM_CODEC,
                SendSyncedTeleporterPositionsPacket::handle
        );
        
        // 注册结构信息请求和回复数据包
        registrar.playToServer(
                RequestStructureInfoPacket.TYPE,
                RequestStructureInfoPacket.STREAM_CODEC,
                RequestStructureInfoPacket::handle
        );
        registrar.playToClient(
                ReplyStructureInfoPacket.TYPE,
                ReplyStructureInfoPacket.STREAM_CODEC,
                ReplyStructureInfoPacket::handle
        );

        // 注册维度列表请求和回复数据包
        registrar.playToServer(
                RequestDimensionListPacket.TYPE,
                RequestDimensionListPacket.STREAM_CODEC,
                RequestDimensionListPacket::handle
        );
        registrar.playToClient(
                ReplyDimensionListPacket.TYPE,
                ReplyDimensionListPacket.STREAM_CODEC,
                ReplyDimensionListPacket::handle
        );

        // 注册设置充能状态数据包
        registrar.playToServer(
                SetUnchargedPacket.TYPE,
                SetUnchargedPacket.STREAM_CODEC,
                SetUnchargedPacket::handle
        );
    }
}