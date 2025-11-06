package com.unddefined.enderechoing.client.renderer;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import org.joml.Matrix4f;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.unddefined.enderechoing.client.EnderEchoingClient.sculkVeilPostChain;

public class SculkVeilRenderer {
    private static final Minecraft mc = Minecraft.getInstance();
    private static int lastWidth = -1;
    private static int lastHeight = -1;
    public static float fadeProgress = 0.0f;

    public static void renderSculkVeil(int tick, float PartialTicks, Matrix4f M, Matrix4f P) {
        if (tick < 0) fadeProgress = 0.001f;
        var Camera = mc.gameRenderer.getMainCamera();
        safeResize(sculkVeilPostChain);
        sculkVeilPostChain.process(PartialTicks);
        applyUniforms(tick, Camera.getPosition(), M, P);
    }

    private static Field findPassesField() {
        try {
            return ObfuscationReflectionHelper.findField(PostChain.class, "passes");
        } catch (ObfuscationReflectionHelper.UnableToFindFieldException var3) {
            try {
                return ObfuscationReflectionHelper.findField(PostChain.class, "passes");
            } catch (ObfuscationReflectionHelper.UnableToFindFieldException var2) {
                LogUtils.getLogger().info("Unable to find passes field on PostChain using Mojmap or SRG identifiers", var2);
                return null;
            }
        }
    }

    private static List<PostPass> getPasses() {
        if (sculkVeilPostChain == null) {
            return Collections.emptyList();
        } else if (findPassesField() == null) {
            return Collections.emptyList();
        } else {
            try {
                Object value = Objects.requireNonNull(findPassesField()).get(sculkVeilPostChain);
                if (value instanceof List) {
                    return (List<PostPass>) value;
                }
                LogUtils.getLogger().error("Orbital railgun post chain passes had unexpected type: {}", value == null ? "null" : value.getClass().getName());
            } catch (IllegalAccessException var3) {
                LogUtils.getLogger().error("Failed to access orbital railgun post chain passes", var3);
                return Collections.emptyList();
            }

            return Collections.emptyList();
        }
    }

    private static void applyUniforms(int tick, Vec3 cameraPos, Matrix4f M, Matrix4f P) {
        List<PostPass> passes = getPasses();
        if (!passes.isEmpty()) {

//            RenderTarget renderTarget = mc.getMainRenderTarget();
//            float width = renderTarget.width > 0 ? (float) renderTarget.width : (float) renderTarget.viewWidth;
//            float height = renderTarget.height > 0 ? (float) renderTarget.height : (float) renderTarget.viewHeight;
            Iterator<PostPass> var16 = passes.iterator();

            while (true) {
                if (!var16.hasNext()) return;
                var pass = var16.next();
                var effect = pass.getEffect();

                effect.safeGetUniform("ModelViewMat").set(M);
                effect.safeGetUniform("InverseProjectionMatrix").set(P.invert());
                effect.safeGetUniform("InverseModelViewMatrix").set(M.invert());
                effect.safeGetUniform("CameraPos").set(cameraPos.toVector3f());
                effect.safeGetUniform("GameTime").set((float) tick);
                effect.safeGetUniform("fadeProgress").set(fadeProgress);
                effect.setSampler("DepthSampler", pass.inTarget::getDepthTextureId);
            }
        }
    }

    public static void safeResize(PostChain chain) {
        int w = mc.getWindow().getWidth();
        int h = mc.getWindow().getHeight();
        if (w != lastWidth || h != lastHeight) {
            chain.resize(w, h);
            lastWidth = w;
            lastHeight = h;
        }
    }

    public static void updateFadeProgress(boolean fadeIO, float delta) {
        float speed = 0.007f;
        if (fadeIO) fadeProgress += speed * delta;
        else fadeProgress -= speed * delta;

        fadeProgress = Mth.clamp(fadeProgress, 0.0f, 1.0f);
    }
}
