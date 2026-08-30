package com.unddefined.enderechoing.client.renderer.layer;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.util.RenderUtil;

/**
 * GeckoLib Geo 模型的几何描边：顶点沿面法线外扩。
 */
public final class OutlineRenderer {

    private OutlineRenderer() {
    }
    public static void render(PoseStack poseStack, BakedGeoModel model, String targetBone, float scale, int color, float offset) {
        ByteBufferBuilder outlineBuilder = new ByteBufferBuilder(256);
        try (outlineBuilder) {
            var outlineSource = MultiBufferSource.immediate(outlineBuilder);
            var outlineBuffer = outlineSource.getBuffer(outlineRenderType);
            render(poseStack, model, outlineBuffer, targetBone, scale, color, offset);
            outlineSource.endBatch();
        }
    }

    public static void render(PoseStack poseStack, BakedGeoModel model, VertexConsumer consumer, String targetBone,
                              float scale, int color, float offset) {
        for (GeoBone bone : model.topLevelBones()) {
            GeoBone target = findBone(bone, targetBone);
            if (target != null) {
                poseStack.pushPose();
                try {
                    // warp_core 的模型中心约在 Y=3/16；绕中心放大，避免整体向上偏移。
                    poseStack.translate(0f, 3f / 16f, 0f);
                    poseStack.scale(scale, scale, scale);
                    poseStack.translate(0f, -3f / 16f, 0f);
                    renderBone(poseStack, target, consumer, color, offset);
                } finally {
                    poseStack.popPose();
                }
                return;
            }
        }
    }

    private static GeoBone findBone(GeoBone bone, String name) {
        if (bone.getName().equals(name)) return bone;
        for (GeoBone child : bone.getChildBones()) {
            GeoBone result = findBone(child, name);
            if (result != null) return result;
        }
        return null;
    }

    private static void renderBone(PoseStack poseStack, GeoBone bone, VertexConsumer consumer, int color, float offset) {
        if (bone.isHidden()) return;
        poseStack.pushPose();
        try {
//            RenderUtil.prepMatrixForBone(poseStack, bone);

            for (GeoCube cube : bone.getCubes()) renderCube(poseStack, cube, consumer, color, offset);

            if (!bone.isHidingChildren())
                for (GeoBone child : bone.getChildBones()) renderBone(poseStack, child, consumer, color, offset);

        } finally {
            poseStack.popPose();
        }
    }

    private static void renderCube(PoseStack poseStack, GeoCube cube, VertexConsumer consumer, int color, float offset) {
        poseStack.pushPose();
        try {
            RenderUtil.translateToPivotPoint(poseStack, cube);
            RenderUtil.rotateMatrixAroundCube(poseStack, cube);
            RenderUtil.translateAwayFromPivotPoint(poseStack, cube);

            Matrix4f matrix = new Matrix4f(poseStack.last().pose());
            Vector3f normal = new Vector3f();

            for (GeoQuad quad : cube.quads()) {
                if (quad == null) continue;

                // 外扩必须使用模型空间法线；变换后的法线只用于写入顶点属性。
                Vector3f localNormal = new Vector3f(quad.normal()).normalize();
                normal.set(localNormal);
                poseStack.last().normal().transform(normal).normalize();

                for (GeoVertex vertex : quad.vertices()) {
                    Vector3f position = new Vector3f(vertex.position());
                    position.fma(offset, localNormal);
                    matrix.transformPosition(position);

                    consumer.addVertex(position.x(), position.y(), position.z()).setColor(color)
                            .setUv(vertex.texU(),vertex.texV()).setUv1(0,1).setUv2(2,0)
                            .setNormal(normal.x, normal.y, normal.z);
                }
            }
        } finally {
            poseStack.popPose();
        }
    }

    public static RenderType outlineRenderType = RenderType.create(
            "enderechoing_outline",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
                    .setTextureState(RenderStateShard.NO_TEXTURE)
                    .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                    .createCompositeState(false)
    );

}
