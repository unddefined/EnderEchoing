package com.unddefined.enderechoing.client.gui.widgets;

import com.unddefined.enderechoing.blocks.entity.EnderEchoTunerBlockEntity;
import com.unddefined.enderechoing.client.gui.screen.PositionEditScreen;
import com.unddefined.enderechoing.client.gui.screen.TunerScreen;
import com.unddefined.enderechoing.server.DataComponents.PositionData;
import com.unddefined.enderechoing.util.MarkedPositionsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static com.unddefined.enderechoing.server.registry.DataRegistry.POSITION;
import static com.unddefined.enderechoing.server.registry.ItemRegistry.ENDER_ECHOING_PEARL;
import static net.minecraft.client.gui.screens.Screen.hasShiftDown;
import static net.minecraft.core.component.DataComponents.CUSTOM_NAME;

public class WaypointList extends ContainerObjectSelectionList<WaypointList.WaypointEntry> {
    private final TunerScreen screen;
    private final ContextMenu contextMenu;
    private MarkedPositionsManager.MarkedPositions selectedPosition;

    public WaypointList(Minecraft minecraft, int width, int height, int x, int y, int itemHeight, TunerScreen screen) {
        super(minecraft, width, height, y, itemHeight);
        this.setX(x);
        this.screen = screen;
        this.contextMenu = new ContextMenu();
        var blockEntity = Minecraft.getInstance().level.getBlockEntity(screen.getMenu().getTunerPos());
        if (blockEntity instanceof EnderEchoTunerBlockEntity tuner)
            selectedPosition = screen.getMarkedPositionsCache().stream().filter(M -> M.pos().equals(tuner.getPos())).findFirst().orElse(null);
    }

    public void addWaypoint(MarkedPositionsManager.MarkedPositions M) {this.addEntry(new WaypointEntry(this, M));}

    public WaypointEntry getEntryFromMouse(double mouseX, double mouseY) {return this.getEntryAtPosition(mouseX, mouseY);}

    public ContextMenu getContextMenu() {return contextMenu;}

    public void openContextMenu(int mouseX, int mouseY, MarkedPositionsManager.MarkedPositions M, WaypointEntry entry) {
        contextMenu.clear();

        contextMenu.addItem("screen.enderechoing.rename", () -> Minecraft.getInstance().setScreen(new PositionEditScreen(screen, M.name(), M.pos())));

        contextMenu.addItem("screen.enderechoing.copy", () -> {
            screen.getMenu().ee_pearl_amount--;
            var pearl = new ItemStack(ENDER_ECHOING_PEARL.get(), 1);
            pearl.set(POSITION.get(), new PositionData(M.Dimension(), M.pos()));
            pearl.set(CUSTOM_NAME, Component.literal(M.name()));
            screen.getMenu().givePlayerPearl(pearl);
        });

        contextMenu.addItem("screen.enderechoing.remove", () -> {
            contextMenu.addItem("screen.enderechoing.confirm_remove", () -> {
                screen.getMarkedPositionsCache().remove(M);
                removeEntry(entry);
                screen.getMenu().ee_pearl_amount++;
                screen.getMenu().setSelectedPosition(null);
            });
            contextMenu.open(mouseX, mouseY, (idx, item) -> {});
        });
        contextMenu.open(mouseX, mouseY, (idx, item) -> {
            // 回调：菜单项被点击
        });
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selectedPosition == null) return false;
        List<MarkedPositionsManager.MarkedPositions> subList = screen.getMarkedPositionsCache().stream().filter(p -> p.iconIndex() == screen.selectedTab).toList();
        return switch (keyCode) {
            case 265 -> moveInSubList(subList, -1, hasShiftDown()); // ↑
            case 264 -> moveInSubList(subList, 1, hasShiftDown()); // ↓
            default -> super.keyPressed(keyCode, scanCode, modifiers);
        };
    }

    private boolean moveInSubList(List<MarkedPositionsManager.MarkedPositions> subList, int dir, boolean extreme) {
        if (subList.isEmpty()) return false;

        int i = subList.indexOf(selectedPosition);
        if (i == -1) return false;

        int targetSubIndex;
        if (extreme) targetSubIndex = (dir < 0) ? 0 : subList.size() - 1;
        else {
            targetSubIndex = i + dir;
            if (targetSubIndex < 0 || targetSubIndex >= subList.size()) return false;
        }

        MarkedPositionsManager.MarkedPositions target = subList.get(targetSubIndex);
        swapInMainList(selectedPosition, target);
        return true;
    }

    public void swapInMainList(MarkedPositionsManager.MarkedPositions a, MarkedPositionsManager.MarkedPositions b) {
        int ia = screen.getMarkedPositionsCache().indexOf(a);
        int ib = screen.getMarkedPositionsCache().indexOf(b);

        if (ia == -1 || ib == -1) return;

        screen.getMarkedPositionsCache().set(ia, b);
        screen.getMarkedPositionsCache().set(ib, a);
    }

    @Override
    protected int getScrollbarPosition() {return this.width + 277;}

    @Override
    public int getRowWidth() {return this.width - 12;}

    public static class WaypointEntry extends ContainerObjectSelectionList.Entry<WaypointEntry> {
        private static final WidgetSprites SPRITES = new WidgetSprites(
                ResourceLocation.withDefaultNamespace("widget/button"),
                ResourceLocation.withDefaultNamespace("widget/button_disabled"),
                ResourceLocation.withDefaultNamespace("widget/button_highlighted")
        );
        private final Minecraft mc = Minecraft.getInstance();
        private final WaypointList parent;
        private final MarkedPositionsManager.MarkedPositions markedPosition;
        private final boolean isSelf;
        public boolean selected;
        private boolean hovered;

        public WaypointEntry(WaypointList parent, MarkedPositionsManager.MarkedPositions M) {
            this.parent = parent;
            this.markedPosition = M;
            this.isSelf = markedPosition.pos().above(2).equals(parent.screen.getMenu().getTunerPos());
        }

        public MarkedPositionsManager.MarkedPositions getMarkedPosition() {return markedPosition;}

        @Override
        public void render(GuiGraphics gfx, int index, int top, int left, int entryWidth, int height,
                           int mouseX, int mouseY, boolean hovered, float partialTick) {
            this.hovered = hovered;
            selected = parent.selectedPosition == markedPosition;
            int width = entryWidth - 6;

            gfx.blitSprite(SPRITES.get(!isSelf, this.hovered || this.selected), left + 3, top, width - 4, height);

            // ---- 绘制文字 ----
            Component text = Component.literal(markedPosition.name());

            int color = selected ? 0xFFFFA0 : 0xE0E0E0;
            gfx.drawString(mc.font, text, left + width / 2 - mc.font.width(text) / 2 + 3, top + 6, color, false);
        }

        public void renderTooltip(GuiGraphics gfx, int mouseX, int mouseY) {
            if (!this.hovered) return;
            var posText = Component.translatable("item.enderechoing.ender_echoing_pearl.position", markedPosition.pos().toShortString(), Component.translationArg(markedPosition.Dimension().location()));
            var distanceText = Component.translatable("screen.enderechoing.distance", (int) Math.sqrt(parent.screen.getMenu().getTunerPos().distSqr(markedPosition.pos())));
            gfx.renderComponentTooltip(mc.font, List.of(posText, distanceText), mouseX, mouseY);
        }

        @Override
        public List<? extends GuiEventListener> children() {return List.of();}

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!hovered) return false;
            if (isSelf) return false;
            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            selected = !selected;

            if (button == 1) {
                selected = !selected;
                parent.openContextMenu((int) mouseX, (int) mouseY, markedPosition, this);
            }
            parent.selectedPosition = selected ? markedPosition : null;
            parent.setSelected(this);
            parent.screen.getMenu().setSelectedPosition(parent.selectedPosition);

            return true;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {return List.of();}
    }
}