package com.unddefined.enderechoing.client.gui.widgets;

import com.unddefined.enderechoing.client.gui.TunerMenu;
import com.unddefined.enderechoing.client.gui.screen.TunerScreen;
import com.unddefined.enderechoing.network.packet.SetTunerSelectedTabPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.unddefined.enderechoing.server.registry.ItemRegistry.ENDER_ECHOING_PEARL;
import static net.minecraft.client.gui.screens.Screen.hasShiftDown;
import static net.minecraft.core.registries.BuiltInRegistries.ITEM;
import static net.minecraft.world.item.Items.STONE;

public class TabBar {
    private static final ResourceLocation HOTBAR_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar");
    private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_selection");
    private static final ResourceLocation HOTBAR_OFFHAND_LEFT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_left");
    private final int x;
    private final int y;
    private final ContextMenu contextMenu;
    private final TunerScreen screen;
    private final TunerMenu menu;
    private boolean tabLocked = false;
    private boolean dragging = false;
    private boolean tabClicked = false;

    public TabBar(int x, int y, TunerScreen screen) {
        this.x = x;
        this.y = y;
        this.contextMenu = new ContextMenu();
        this.screen = screen;
        this.menu = screen.getMenu();
        if (menu.selected_tuner_tab > 0) tabLocked = true;
    }

    public void render(GuiGraphics G, int mouseX, int mouseY, float partialTick) {

        G.blitSprite(HOTBAR_SPRITE, x, y, 182, 22);
        G.blitSprite(HOTBAR_OFFHAND_LEFT_SPRITE, x - 29, y - 1, 29, 24);
        G.blitSprite(HOTBAR_SELECTION_SPRITE, x - 30 + screen.selectedTab * 20 + (screen.selectedTab > 0 ? 9 : 0), y - 1, 24, 23);

        this.renderSlot(G, x - 26, y + 3, menu.getIconList().getFirst());
        for (int i1 = 1; i1 <= 9; i1++) this.renderSlot(G, x - 20 + i1 * 20 + 3, y + 3, menu.getIconList().get(i1));

        if (dragging) G.renderFakeItem(menu.getIconList().get(screen.selectedTab), mouseX - 8, mouseY - 8);

        contextMenu.render(G, 0, 0, partialTick);
    }

    private void renderSlot(GuiGraphics guiGraphics, int x, int y, ItemStack stack) {
        if (stack.isEmpty() || (contextMenu.isVisible() && !stack.equals(menu.getIconList().get(screen.selectedTab)) && !stack.equals(menu.getIconList().getFirst())))
            return;

        guiGraphics.renderFakeItem(dragging && menu.getIconList().get(screen.selectedTab).equals(stack) ? ItemStack.EMPTY : stack, x, y);
        ItemStack pearl = stack.is(ENDER_ECHOING_PEARL) ? new ItemStack(ENDER_ECHOING_PEARL.get(), menu.ee_pearl_amount) : stack;
        guiGraphics.renderItemDecorations(screen.getMinecraft().font, pearl, x, y);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = 0; i <= 9; i++) {
            double slotSize = 20;
            double tx = x - 30 + i * slotSize + (i > 0 ? 9 : 0);
            double ty = y;

            if (!(mouseX >= tx && mouseX < tx + slotSize && mouseY >= ty && mouseY < ty + slotSize)) continue;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 0.7F));
            screen.selectedTab = i;
            tabClicked = true;
            screen.populateWaypointList();

            if (button == 1) {
                // 右键 → 弹出菜单
                if (i == 0) {
                    menu.givePlayerPearl(new ItemStack(ENDER_ECHOING_PEARL.get(), Math.min(menu.ee_pearl_amount, 8)));
                    return true;
                }
                contextMenu.clear();

                contextMenu.addItem("screen.enderechoing.lock", () -> {
                    PacketDistributor.sendToServer(new SetTunerSelectedTabPacket(screen.selectedTab));
                    tabLocked = true;
                });

                if (tabLocked) contextMenu.addItem("screen.enderechoing.unlock", () -> {
                    PacketDistributor.sendToServer(new SetTunerSelectedTabPacket(0));
                    tabLocked = false;
                    screen.selectedTab = 0;
                    screen.populateWaypointList();
                });

                int finalI = i;
                contextMenu.addItem("screen.enderechoing.change_icon", () -> {
                    screen.changeIcon = true;
                    screen.previousIcon = menu.getIconList().get(finalI).isEmpty() ? new ItemStack(STONE) : menu.getIconList().get(finalI);
                    screen.nameField.setValue(ITEM.getKey(menu.getIconList().get(finalI).getItem()).toString());
                });

                contextMenu.open((int) mouseX, (int) mouseY, (idx, item) -> {
                    // 回调：菜单项被点击
                });

                return true;
            }
        }
        // 检查是否点击了上下文菜单
        return contextMenu.isVisible() && contextMenu.mouseClicked(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        var currentIcon = menu.getIconList().get(screen.selectedTab);
        if (screen.selectedTab == 0) return false;
        // 左右方向键交换选中tab位置
        if (keyCode == 263) { // 左箭头键
            if (screen.selectedTab > 1) {
                // 交换选中的tab与其左边的tab
                var leftIcon = menu.getIconList().get(hasShiftDown() ? 1 : screen.selectedTab - 1);
                menu.getIconList().set(hasShiftDown() ? 1 : screen.selectedTab - 1, currentIcon);
                menu.getIconList().set(screen.selectedTab, leftIcon);
                screen.selectedTab = hasShiftDown() ? 1 : screen.selectedTab - 1;
                return true;
            }
        } else if (keyCode == 262) { // 右箭头键
            if (screen.selectedTab < 9) {
                // 交换选中的tab与其右边的tab
                var rightIcon = menu.getIconList().get(hasShiftDown() ? 9 : screen.selectedTab + 1);
                menu.getIconList().set(hasShiftDown() ? 9 : screen.selectedTab + 1, currentIcon);
                menu.getIconList().set(screen.selectedTab, rightIcon);
                screen.selectedTab = hasShiftDown() ? 9 : screen.selectedTab + 1;
                return true;
            }
        }

        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        tabClicked = false;
        if (!dragging) return false;
        dragging = false;

        for (int i = 1; i <= 9; i++) {
            double slotSize = 20;
            double tx = x - 30 + i * slotSize + 9;
            double ty = y;

            if (!(mouseX >= tx && mouseX < tx + slotSize && mouseY >= ty && mouseY < ty + slotSize)) continue;

            var currentIcon = menu.getIconList().get(screen.selectedTab);
            menu.getIconList().set(screen.selectedTab, menu.getIconList().get(i));
            menu.getIconList().set(i, currentIcon);

            return true;
        }
        return false;
    }

    public boolean mouseDragged(int button) {
        if (button != 0) return false;
        if (screen.selectedTab < 1) return false;
        if (!dragging && !tabClicked) return false;
        this.dragging = true;
        return true;
    }

    public ContextMenu getContextMenu() {
        return contextMenu;
    }

}

