package com.unddefined.enderechoing.client.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ContextMenu {
    private final List<Item> items = new ArrayList<>();
    private final List<Button> buttons = new ArrayList<>();
    private boolean visible;
    private int x, y;
    private MenuHandler handler;

    public void addItem(String name, Runnable action) {items.add(new Item(name, action));}

    public void clear() {
        items.clear();
        buttons.clear();
    }

    public void open(int x, int y, MenuHandler handler) {
        this.x = x;
        this.y = y;
        this.handler = handler;
        this.visible = true;

        // 创建按钮
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            int finalI = i;
            Button button = Button.builder(Component.translatable(item.name), btn -> {
                item.action.run();
                if (this.handler != null) this.handler.onClick(finalI, Component.translatable(item.name));

                if (!item.name.equals("screen.enderechoing.remove")) close();
            }).bounds(x, y + i * 20, 100, 20).build();
            buttons.add(button);
        }
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;

        // 绘制背景
        guiGraphics.fill(x, y, x + 100, y + items.size() * 20, 0xFF000000);
        guiGraphics.fill(x, y, x + 100, y + items.size() * 20, 0x80FFFFFF);

        // 渲染按钮
        for (Button button : buttons) button.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;

        for (Button btn : buttons) if (btn.mouseClicked(mouseX, mouseY, button)) return true;

        // 点击菜单外区域关闭菜单
        if (mouseX < x || mouseX > x + 100 || mouseY < y || mouseY > y + items.size() * 20) {
            close();
            return true;
        }

        return false;
    }

    public void close() {
        visible = false;
        clear();
    }

    public boolean isVisible() {return visible;}

    public interface MenuHandler { void onClick(int index, Component item);}

    record Item(String name, Runnable action) {}
}