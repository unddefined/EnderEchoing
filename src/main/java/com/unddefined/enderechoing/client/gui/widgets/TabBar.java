package com.unddefined.enderechoing.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unddefined.enderechoing.client.gui.TunerMenu;
import com.unddefined.enderechoing.client.gui.screen.TunerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import static com.unddefined.enderechoing.server.registry.ItemRegistry.ENDER_ECHOING_PEARL;
import static net.minecraft.client.gui.screens.Screen.hasShiftDown;
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
    private boolean dragging = false;
    private boolean tabClicked = false;

    public TabBar(int x, int y, TunerScreen screen) {
        this.x = x;
        this.y = y;
        this.contextMenu = new ContextMenu();
        this.screen = screen;
        this.menu = screen.getMenu();
    }

    public void render(GuiGraphics G, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        G.pose().pushPose();

        G.blitSprite(HOTBAR_SPRITE, x, y, 182, 22);
        G.blitSprite(HOTBAR_OFFHAND_LEFT_SPRITE, x - 29, y - 1, 29, 24);
        G.blitSprite(HOTBAR_SELECTION_SPRITE, x - 30 + screen.selectedTab * 20 + (screen.selectedTab > 0 ? 9 : 0), y - 1, 24, 23);
        G.pose().popPose();

        this.renderSlot(G, x - 26, y + 3, partialTick, menu.getIconList().getFirst());
        for (int i1 = 1; i1 <= 9; i1++) {
            int j1 = x - 20 + i1 * 20 + 3;
            this.renderSlot(G, j1, y + 3, partialTick, menu.getIconList().get(i1));
        }
        RenderSystem.disableBlend();
        // 渲染右键菜单
        contextMenu.render(G, 0, 0, partialTick);
        if (!dragging) return;
        G.renderFakeItem(menu.getIconList().get(screen.selectedTab), mouseX - 8, mouseY - 8);
    }

    private void renderSlot(GuiGraphics guiGraphics, int x, int y, float partialTick, ItemStack stack) {
        if (stack.isEmpty()) return;
        float f = (float) stack.getPopTime() - partialTick;
        if (f > 0.0F) {
            float f1 = 1.0F + f / 5.0F;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate((float) (x + 8), (float) (y + 12), 0.0F);
            guiGraphics.pose().scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
            guiGraphics.pose().translate((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
        }
        var air = dragging && menu.getIconList().get(screen.selectedTab).equals(stack) ? ItemStack.EMPTY : stack;
        guiGraphics.renderFakeItem(air, x, y);
        if (f > 0.0F) guiGraphics.pose().popPose();
        ItemStack fakepearl = stack.is(ENDER_ECHOING_PEARL) ? new ItemStack(STONE, menu.ee_pearl_amount) : stack;
        guiGraphics.renderItemDecorations(Minecraft.getInstance().font, fakepearl, x, y);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 0.7F));
        for (int i = 0; i <= 9; i++) {
            double slotSize = 20;
            double tx = x - 30 + i * slotSize + (i > 0 ? 9 : 0);
            double ty = y;

            if (!(mouseX >= tx && mouseX < tx + slotSize && mouseY >= ty && mouseY < ty + slotSize)) continue;

            if (button == 0) {
                screen.selectedTab = i;
                tabClicked = true;
                screen.populateWaypointList();
                return true;
            }

            if (button == 1) {
                // 右键 → 弹出菜单
                if (i == 0) {
                    menu.givePlayerPearl(new ItemStack(ENDER_ECHOING_PEARL.get(), Math.min(menu.ee_pearl_amount, 8)));
                    return true;
                }
                contextMenu.clear();
                contextMenu.addItem(  "screen.enderechoing.lock", () -> {

                });

                contextMenu.addItem("screen.enderechoing.change_icon", () -> {
                    // 打开图标选择界面
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

    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        if (screen.selectedTab < 1) return false;
        if (!dragging && tabClicked) {
            this.dragging = true;
            return true;
        }
        return false;
    }

    public ContextMenu getContextMenu() {return contextMenu;}

}

