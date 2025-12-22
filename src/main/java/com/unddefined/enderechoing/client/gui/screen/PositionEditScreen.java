package com.unddefined.enderechoing.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.unddefined.enderechoing.network.packet.PearlRenamePacket;
import com.unddefined.enderechoing.network.packet.RequestStructureInfoPacket;
import com.unddefined.enderechoing.util.MarkedPositionsManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class PositionEditScreen extends Screen {
    private final Screen lastScreen;
    private final BlockPos pos;
    public String fieldValue;
    private EditBox nameField;
    private boolean CursorMoved = false;
    private String dimension;
    private String biome;
    private String structure = "loading...";
    private Button structureBtn;

    public PositionEditScreen(Screen lastScreen, String fieldValue, BlockPos pos) {
        super(Component.translatable("screen.enderechoing.edit_title"));
        this.lastScreen = lastScreen;
        this.fieldValue = fieldValue;
        this.pos = pos;
    }

    @Override
    protected void init() {
        // 添加文本输入框
        this.nameField = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 10, 200, 20, Component.translatable("screen.enderechoing.enter_name"));
        this.nameField.setMaxLength(50); // 设置最大长度
        this.nameField.setValue(fieldValue);
        this.addWidget(this.nameField);
        this.setInitialFocus(this.nameField);
        if (this.minecraft != null) {
            var player = this.minecraft.player;
            if (player != null) {
                dimension = player.level().dimension().location().toShortLanguageKey();
                biome = player.level().getBiome(player.blockPosition()).getRegisteredName();

                // 请求服务器上的结构信息
                PacketDistributor.sendToServer(new RequestStructureInfoPacket(pos));
            }
        }
        // 添加维度按钮
        var dimBtn = Button.builder(Component.translatable("screen.enderechoing.dimension"), (button) ->
                this.nameField.insertText(dimension != null ? dimension : "null"))
                .bounds(this.width / 2 - 102, this.height / 2 + 70, 70, 20).build();
        dimBtn.setTooltip(Tooltip.create(Component.translatable(dimension != null ? dimension : "null")));
        this.addRenderableWidget(dimBtn);

        // 添加生物群系按钮
        var biomeBtn = Button.builder(Component.translatable("screen.enderechoing.biome"), (button) ->
                this.nameField.insertText(biome != null ? biome : "null"))
                .bounds(this.width / 2 - 25, this.height / 2 + 70, 49, 20).build();
        biomeBtn.setTooltip(Tooltip.create(Component.translatable(biome != null ? biome : "null")));
        this.addRenderableWidget(biomeBtn);

        // 添加结构按钮
        structureBtn = Button.builder(Component.translatable("screen.enderechoing.structure"), (button) ->
                this.nameField.insertText(structure != null ? structure : "null"))
                .bounds(this.width / 2 + 32, this.height / 2 + 70, 70, 20).build();
        structureBtn.setTooltip(Tooltip.create(Component.translatable(structure != null ? structure : "null")));
        this.addRenderableWidget(structureBtn);

        // 添加完成按钮
        this.addRenderableWidget(Button.builder(Component.translatable("screen.enderechoing.done"), (button) ->
                this.onDone()).bounds(this.width / 2 - 102, this.height / 2 + 25, 100, 20).build());

        // 添加取消按钮
        this.addRenderableWidget(Button.builder(Component.translatable("screen.enderechoing.cancel"), (button) ->
                this.onClose()).bounds(this.width / 2 + 2, this.height / 2 + 25, 100, 20).build());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // 渲染标题
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 30, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, Component.translatable("screen.enderechoing.insert_info"), this.width / 2, this.height / 2 + 55, 0xFFFFFF);

        // 渲染文本框提示
        this.nameField.render(guiGraphics, mouseX, mouseY, partialTick);
        if (!CursorMoved) this.nameField.moveCursorTo(1, false);
        CursorMoved = true;
    }

    private void onDone() {
        String name = this.nameField.getValue().trim();

        if (lastScreen instanceof TunerScreen tunerScreen) {
            var M = tunerScreen.getFocusingEntry().getMarkedPosition();
            var newPosition = new MarkedPositionsManager.MarkedPositions(M.Dimension(), M.pos(), name, M.iconIndex());
            tunerScreen.getMarkedPositionsCache().set(tunerScreen.getMarkedPositionsCache().indexOf(M), newPosition);
            tunerScreen.populateWaypointList();
        } else PacketDistributor.sendToServer(new PearlRenamePacket(name));

        this.onClose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_RETURN) {
            this.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
            this.onDone();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        CursorMoved = false;
        if (this.minecraft != null) this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public boolean isPauseScreen() {return false;}

    // 供 ReplyStructureInfoPacket 回调使用
    public void setStructure(String structure) {
        this.structure = structure;
        if (this.structureBtn != null) this.structureBtn.setTooltip(Tooltip.create(Component.translatable(structure != null ? structure : "null")));
    }
}