package com.unddefined.enderechoing.client.gui;

import com.unddefined.enderechoing.network.packet.ItemRenamePacket;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class EditScreen extends Screen {
    private EditBox nameField;
    private final Screen lastScreen;
    private Button doneButton;
    private Button cancelButton;

    public EditScreen(Screen lastScreen) {
        super(Component.translatable("screen.enderechoing.edit_title"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        // 添加文本输入框
        this.nameField = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 10, 200, 20, Component.translatable("screen.enderechoing.enter_name"));
        this.nameField.setMaxLength(50); // 设置最大长度
        this.nameField.setValue(""); // 默认为空
        this.addWidget(this.nameField);
        this.setInitialFocus(this.nameField);

        // 添加完成按钮
        this.doneButton = this.addRenderableWidget(Button.builder(Component.translatable("screen.enderechoing.done"), (button) ->
                this.onDone()).bounds(this.width / 2 - 102, this.height / 2 + 25, 100, 20).build());

        // 添加取消按钮
        this.cancelButton = this.addRenderableWidget(Button.builder(Component.translatable("screen.enderechoing.cancel"), (button) ->
                this.onClose()).bounds(this.width / 2 + 2, this.height / 2 + 25, 100, 20).build());
    }

    @Override
    public void tick() {
        // EditBox没有tick方法，移除此调用
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // 渲染标题
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 30, 0xFFFFFF);
        
        // 渲染文本框提示
        this.nameField.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void onDone() {
        String name = this.nameField.getValue().trim();
        String OriginalName = ItemRegistry.ENDER_ECHOING_PEARL.getRegisteredName();
        if (!name.isEmpty()) {
            // 发送重命名数据包到服务端
            PacketDistributor.sendToServer(new ItemRenamePacket(name));
        }else {
            PacketDistributor.sendToServer(new ItemRenamePacket(OriginalName));
        }
        this.onClose();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // 不暂停游戏
    }

    public Button getDoneButton() {
        return doneButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }
}