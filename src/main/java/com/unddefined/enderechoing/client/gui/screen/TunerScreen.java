package com.unddefined.enderechoing.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.unddefined.enderechoing.client.gui.TunerMenu;
import com.unddefined.enderechoing.client.gui.widgets.TabBar;
import com.unddefined.enderechoing.client.gui.widgets.WaypointList;
import com.unddefined.enderechoing.network.packet.SyncTunerDataPacket;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import com.unddefined.enderechoing.util.MarkedPositionsManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.regex.Pattern;

import static net.minecraft.core.registries.BuiltInRegistries.ITEM;

public class TunerScreen extends AbstractContainerScreen<TunerMenu> {
    private final List<MarkedPositionsManager.MarkedPositions> MarkedPositionsCache;
    private final WidgetSprites ACCEPT_SPRITE = new WidgetSprites(ResourceLocation.withDefaultNamespace("pending_invite/accept_highlighted"), ResourceLocation.withDefaultNamespace("pending_invite/accept"));
    private final WidgetSprites REJECT_SPRITE = new WidgetSprites(ResourceLocation.withDefaultNamespace("pending_invite/reject_highlighted"), ResourceLocation.withDefaultNamespace("pending_invite/reject"));
    private final int editBarWidth = 240;
    private final int editBarHeight = 20;
    public int selectedTab = 0;
    public boolean changeIcon = false;
    public ItemStack previousIcon;
    public EditBox nameField;
    private int editBarX, editBarY;
    private WaypointList waypointList;
    private boolean dragging = false;
    private TabBar tabBar;
    private WaypointList.WaypointEntry focusingEntry;
    private boolean isValidString;
    private ImageButton ACCEPT_BUTTON;
    private ImageButton REJECT_BUTTON;

    public TunerScreen(TunerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 376;
        this.imageHeight = 302;
        this.inventoryLabelY = this.imageHeight - 74;
        this.MarkedPositionsCache = menu.getMarkedPositionsCache();
    }

    @Override
    protected void init() {
        super.init();
        editBarX = this.width / 2 - 81 - 29;
        editBarY = this.height / 2 - 154;
        selectedTab = menu.selected_tuner_tab;
        int listLeft = this.width / 2 - 110;
        int listWidth = this.width / 4 + 18;
        int listTop = 200;
        int listBottom = this.height - 300;
        tabBar = new TabBar(this.width / 2 - 81, this.height - 332, this);
        waypointList = new WaypointList(this.minecraft, listWidth, listTop, listLeft, listBottom, 24, this);

        populateWaypointList();
        this.addWidget(waypointList);

        this.nameField = new EditBox(this.font, editBarX, editBarY, editBarWidth - 66, editBarHeight, Component.translatable("screen.enderechoing.enter_registry_name"));
        this.nameField.setMaxLength(50);
        this.setInitialFocus(this.nameField);
        this.nameField.setTooltip(Tooltip.create(Component.translatable("screen.enderechoing.enter_registry_name")));
        this.addWidget(this.nameField);

        // 添加确定和取消按钮
        ACCEPT_BUTTON = this.addRenderableWidget(new ImageButton(editBarX + 110 + 68, editBarY, 18, 18,
                ACCEPT_SPRITE, btn -> {
            if (!isValidString || menu.getIconList().get(selectedTab).isEmpty()) return;
            changeIcon = false;
        }));

        REJECT_BUTTON = this.addRenderableWidget(new ImageButton(editBarX + 110 + 86, editBarY, 18, 18,
                REJECT_SPRITE, btn -> {
            menu.getIconList().set(selectedTab, previousIcon);
            changeIcon = false;
        }));
    }

    public void populateWaypointList() {
        waypointList.children().clear();
        var list = MarkedPositionsCache.stream().filter(entry -> entry.iconIndex() == selectedTab).toList();
        list.forEach(e -> waypointList.addWaypoint(e));
    }

    @Override
    public void onClose() {
        if (menu.getIconList().get(selectedTab).isEmpty()) menu.getIconList().set(selectedTab, previousIcon);
        PacketDistributor.sendToServer(new SyncTunerDataPacket(menu.getIconList(), MarkedPositionsCache, menu.ee_pearl_amount));
        super.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.nameField.render(guiGraphics, mouseX, mouseY, partialTick);

        waypointList.render(guiGraphics, mouseX, mouseY, partialTick);

        // 渲染waypoint列表项的tooltip，避免被列表边框截断
        var hoveredEntry = waypointList.getEntryFromMouse(mouseX, mouseY);
        if (hoveredEntry != null) hoveredEntry.renderTooltip(guiGraphics, mouseX + 5, mouseY + 5);

        tabBar.render(guiGraphics, mouseX, mouseY, partialTick);

        // 渲染标题
        guiGraphics.drawString(this.font, this.title, this.leftPos + (this.imageWidth / 2) - (this.font.width(this.title) / 2),
                this.topPos + 6 - (changeIcon ? 24 : 0), 0xd1d6b6, false);

        waypointList.getContextMenu().render(guiGraphics, mouseX, mouseY, partialTick);
        tabBar.getContextMenu().render(guiGraphics, mouseX, mouseY, partialTick);
        if (dragging) guiGraphics.renderFakeItem(new ItemStack(ItemRegistry.ENDER_ECHOING_PEARL.get()), mouseX - 8, mouseY - 8);
    }

    @Override
    public void containerTick() {
        this.nameField.visible = changeIcon;
        ACCEPT_BUTTON.visible = changeIcon;
        REJECT_BUTTON.visible = changeIcon;
        if (selectedTab == 0) changeIcon = false;
        if (!changeIcon) return;

        String value = this.nameField.getValue();
        isValidString = Pattern.compile("[a-z0-9:_.-]+").matcher(value).matches();
        if (isValidString) {
            var location = ResourceLocation.tryParse(value);
            if (location != null) menu.getIconList().set(selectedTab, ITEM.get(location).getDefaultInstance());
        }
        this.nameField.setTextColor(!isValidString || menu.getIconList().get(selectedTab).isEmpty() ? 0xFF0000 : 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (changeIcon && !(mouseX >= editBarX && mouseX < editBarX + editBarWidth && mouseY >= editBarY && mouseY < editBarY + editBarHeight)) {
            if (menu.getIconList().get(selectedTab).isEmpty()) menu.getIconList().set(selectedTab, previousIcon);
            changeIcon = false;
        }

        if (tabBar.getContextMenu().isVisible() && tabBar.getContextMenu().mouseClicked(mouseX, mouseY, button)) return true;

        if (waypointList.getContextMenu().isVisible() && waypointList.getContextMenu().mouseClicked(mouseX, mouseY, button)) return true;

        focusingEntry = waypointList.getEntryFromMouse(mouseX, mouseY);
        if (focusingEntry != null && focusingEntry.mouseClicked(mouseX, mouseY, button)) return true;

        if (tabBar.mouseClicked(mouseX, mouseY, button)) return true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (tabBar.mouseReleased(mouseX, mouseY, button)) return true;
        if (!dragging) return true;
        this.dragging = false;
        if (waypointList.getSelected() == null) return true;
        if (button != 0) return true;

        var M = waypointList.getSelected().getMarkedPosition();
        waypointList.setSelected(null);
        // 更换图标
        for (int i = 0; i <= 9; i++) {
            double slotSize = 20;
            double tx = (double) this.width / 2 - 81 - 30 + i * slotSize + (i > 0 ? 9 : 0);
            double ty = this.height - 332;

            if (!(mouseX >= tx && mouseX < tx + slotSize && mouseY >= ty && mouseY < ty + slotSize)) continue;

            selectedTab = i;

            var newPosition = new MarkedPositionsManager.MarkedPositions(M.Dimension(), M.pos(), M.name(), i);
            MarkedPositionsCache.set(MarkedPositionsCache.indexOf(M), newPosition);
            populateWaypointList();
        }
        // 交换位置
        var swapEntry = waypointList.getEntryFromMouse(mouseX, mouseY);
        if (swapEntry != null && swapEntry != focusingEntry) {
            waypointList.swapInMainList(M, swapEntry.getMarkedPosition());
            populateWaypointList();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (waypointList.getContextMenu().isVisible() || tabBar.getContextMenu().isVisible()) return false;
        if (button != 0) return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        if (tabBar.mouseDragged(mouseX, mouseY, button)) return true;

        var selected = waypointList.getSelected();
        if (selected == null) return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        if (!dragging && focusingEntry != null && focusingEntry.equals(selected)) this.dragging = true;

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (changeIcon && keyCode == InputConstants.KEY_E) return false;
        if (changeIcon && keyCode == InputConstants.KEY_RETURN) {
            this.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
            if (!isValidString || menu.getIconList().get(selectedTab).isEmpty()) return false;
            changeIcon = false;
        }
        if (!changeIcon && tabBar.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (!changeIcon && waypointList.keyPressed(keyCode, scanCode, modifiers)) {
            populateWaypointList();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // 渲染GUI背景纹理
//        guiGraphics.blit(
//                ResourceLocation.withDefaultNamespace("gui/menu_background"),
//                this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    public WaypointList.WaypointEntry getFocusingEntry() {
        return focusingEntry;
    }

    public List<MarkedPositionsManager.MarkedPositions> getMarkedPositionsCache() {
        return MarkedPositionsCache;
    }

}