package com.unddefined.enderechoing.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TransparentScreen extends Screen {
    public TransparentScreen() {
        super(Component.literal("Transparent Screen"));
    }

    @Override
    protected void init() {
        super.init();
        // 初始化屏幕组件
        // 这里可以添加按钮、文本等GUI元素
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 不调用super.render()来避免默认背景渲染，实现透明效果
        // 可以在这里绘制自定义内容

        // 示例：绘制一个小的半透明矩形
        guiGraphics.fill(10, 10, 100, 60, 0x80000000); // 半透明黑色矩形

        // 示例：绘制文本
        guiGraphics.drawString(this.font, "Transparent Screen", 20, 20, 0xFFFFFF);

        // super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        // 设置为false使游戏在打开此屏幕时不会暂停
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        // 按ESC键可以关闭屏幕
        return true;
    }
}