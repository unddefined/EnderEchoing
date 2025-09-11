package com.unddefined.enderechoing.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unddefined.enderechoing.client.model.EnderEchoingCoreModel;
import com.unddefined.enderechoing.items.EnderEchoingCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class EnderEchoingCoreRenderer extends GeoItemRenderer<EnderEchoingCore> {
    public EnderEchoingCoreRenderer(GeoModel<EnderEchoingCore> model) {
        super(new EnderEchoingCoreModel());
    }

    protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack,
                               MultiBufferSource bufferSource, int packedLight, int packedOverlay, float partialTick) {

        this.animatable = (EnderEchoingCore) this.getCurrentItemStack().getItem();
        this.currentItemStack = this.getCurrentItemStack();
        this.renderPerspective = transformType;
        
        // 只在播放使用动画时应用平移
        ItemStack activeStack = Minecraft.getInstance().player.getUseItem();
        if (this.currentItemStack != null && activeStack.getItem() instanceof EnderEchoingCore &&
                Minecraft.getInstance().player.isUsingItem()) {
            poseStack.translate(0.3, 0.26, 0);
        }
        RenderType renderType = getRenderType(this.animatable, getTextureLocation(this.animatable), bufferSource, partialTick);
        VertexConsumer buffer = Minecraft.getInstance().getItemRenderer().getFoilBufferDirect(bufferSource, renderType, true, this.currentItemStack != null && this.currentItemStack.hasFoil());

        defaultRender(poseStack, this.animatable, bufferSource, renderType, buffer, 0f, partialTick, packedLight);

    }
}