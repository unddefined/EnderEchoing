package com.unddefined.enderechoing.client.gui.widgets;

import com.unddefined.enderechoing.client.gui.screen.PositionEditScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;

import java.util.List;

public class DimensionSelecter extends AbstractWidget {
    private final PositionEditScreen screen;
    private final int x, y;
    public ResourceKey<Level> dimension;
    private List<ResourceKey<Level>> dimensionList;
    private boolean visible = false;
    private String selectedDimensionString;

    public DimensionSelecter(List<ResourceKey<Level>> dimensionList, PositionEditScreen screen, int x, int y, int width, String dimensionString) {
        super(x, y, width, 20, Component.literal(""));
        this.dimensionList = dimensionList;
        this.selectedDimensionString = dimensionString;
        this.screen = screen;
        this.x = x + 50 + 3;
        this.y = y;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        int dark = FastColor.ABGR32.color(255, 0, 0, 0);
        int grey = visible ? -1 : FastColor.ABGR32.color(255, 160, 160, 160);
        guiGraphics.fill(x, y, x - 30 + width, y + 20, dark);
        guiGraphics.hLine(x, x + width - 1 - 30, y, grey);
        guiGraphics.hLine(x, x + width - 1 - 30, y + height, grey);
        guiGraphics.drawString(screen.font, selectedDimensionString, x + 5, y + 6, -1, false);
        guiGraphics.drawString(screen.font, Component.translatable("screen.enderechoing.dimension") + ":", x - 50 - 3, y + 6, -1, false);
        if (!visible) return;
        for (int i = 1; i < dimensionList.size() + 1; i++) {
            ResourceKey<Level> dimension = dimensionList.get(i - 1);
            guiGraphics.fill(x, y + i * 20, x + width - 30, y + i * 20 + 20, dark);
            guiGraphics.hLine(x, x + width - 1 - 30, y + i * 20 + 20, grey);
            guiGraphics.drawString(screen.font, dimension.location().toString(), x + 5, y + i * 20 + 6, -1, false);
        }

    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (mouseX >= x && mouseX <= x + 280 && mouseY >= y && mouseY <= y + 20) {
            visible = !visible;
            height = visible ? dimensionList.size() * 20 + 20 : 20;
        }

        if (mouseX >= x && mouseX <= x + 280 && mouseY >= y + 20 && mouseY <= y + dimensionList.size() * 20 + 20) {
            dimension = dimensionList.get((int) ((mouseY - y - 20) / 20));
            selectedDimensionString = dimension.location().toString();
            visible = false;
            height = 20;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    public void updateDimensionList(List<ResourceKey<Level>> newDimensionList) {this.dimensionList = newDimensionList;}
}
