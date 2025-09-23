package com.unddefined.enderechoing.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unddefined.enderechoing.blocks.entity.EnderEchoicTeleporterBlockEntity;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import java.util.Objects;

public class EnderEchoicTeleporterLayer extends BlockAndItemGeoLayer<EnderEchoicTeleporterBlockEntity> {
    public EnderEchoicTeleporterLayer(GeoRenderer<EnderEchoicTeleporterBlockEntity> renderer) {
        super(renderer);
    }

    @Override
    protected ItemStack getStackForBone(GeoBone bone, EnderEchoicTeleporterBlockEntity animatable) {
        // 只在特定骨骼上渲染物品
        if (bone.getName().equals("EnderEchoingCore")) return new ItemStack(ItemRegistry.ENDER_ECHOING_CORE.get());
        return this.stackForBone.apply(bone, animatable);
    }
    double currentPositionY = -0.18;
    double currentPivotY = 16;
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, EnderEchoicTeleporterBlockEntity animatable,
                                      MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        Vec3 blockpos = animatable.getBlockPos().getCenter();
        Player NearestPlayer =
                Objects.requireNonNull(Minecraft.getInstance().level).
                        getNearestPlayer(blockpos.x, blockpos.y, blockpos.z, 2.0, false);

        double pY1 = -0.18;
        double pY2 = -0.23;
        double bY1 = 16;
        double bY2 = 21;

// 根据是否有玩家来决定目标位置
        double targetPositionY = NearestPlayer == null ? pY1 : pY2;
        double targetPivotY = NearestPlayer == null ? bY1 : bY2;
// 平滑过渡：每帧向目标位置靠近一点
        currentPositionY = Mth.lerp(0.05, currentPositionY, targetPositionY);
        currentPivotY = Mth.lerp(0.05, currentPivotY, targetPivotY);
// 应用到渲染
        poseStack.translate(0, currentPositionY, 0);
        bone.setPivotY((float) currentPivotY);
        super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
    }
}