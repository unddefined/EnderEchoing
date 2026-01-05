package com.unddefined.enderechoing.client.gui;

import com.unddefined.enderechoing.blocks.entity.EnderEchoTunerBlockEntity;
import com.unddefined.enderechoing.network.packet.GivePlayerPearlPacket;
import com.unddefined.enderechoing.network.packet.SetSelectedPositionPacket;
import com.unddefined.enderechoing.util.MarkedPositionsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

import static com.unddefined.enderechoing.EnderEchoing.TUNER_MENU;
import static com.unddefined.enderechoing.server.registry.BlockRegistry.ENDER_ECHO_TUNER;
import static com.unddefined.enderechoing.server.registry.DataRegistry.*;
import static net.minecraft.core.registries.BuiltInRegistries.ITEM;

public class TunerMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    public int ee_pearl_amount;
    public int selected_tuner_tab;
    private List<ItemStack> iconList = new ArrayList<>();
    private List<MarkedPositionsManager.MarkedPositions> markedPositionsCache;
    private BlockPos tunerPos;

    public TunerMenu(int c, Inventory i, FriendlyByteBuf buf) {this(c, i, ContainerLevelAccess.NULL, buf);}

    public TunerMenu(int containerId, Inventory playerInv, ContainerLevelAccess A, FriendlyByteBuf buf) {
        super(TUNER_MENU.get(), containerId);
        this.access = A;
        this.selected_tuner_tab = buf.readInt();
        this.ee_pearl_amount = buf.readInt();
        this.tunerPos = buf.readBlockPos();
        this.markedPositionsCache = buf.readList(MarkedPositionsManager.STREAM_CODEC);
        for (int i = 0; i < 10; i++) this.iconList.add(new ItemStack(ITEM.get(buf.readResourceLocation())));
    }

    public TunerMenu(int containerId, Inventory playerInv, ContainerLevelAccess A) {
        super(TUNER_MENU.get(), containerId);
        this.access = A;
        A.execute((level, pos) -> {
            this.selected_tuner_tab = playerInv.player.getData(SELECTED_TUNER_TAB.get());
            this.ee_pearl_amount = playerInv.player.getData(EE_PEARL_AMOUNT.get());
            this.iconList = playerInv.player.getData(ICON_LIST.get()).icons();
            this.markedPositionsCache = playerInv.player.getData(MARKED_POSITIONS_CACHE.get()).markedPositions();
        });
    }

    public void setSelectedPosition(MarkedPositionsManager.MarkedPositions M) {
        if (M == null) PacketDistributor.sendToServer(new SetSelectedPositionPacket(tunerPos, BlockPos.ZERO, Level.OVERWORLD, ""));
        else PacketDistributor.sendToServer(new SetSelectedPositionPacket(tunerPos, M.pos(), M.Dimension(), M.name()));
    }

    public void givePlayerPearl(ItemStack itemStack) {
        if (itemStack.isEmpty()) return;
        if (itemStack.get(POSITION) == null) ee_pearl_amount -= itemStack.getCount();
        PacketDistributor.sendToServer(new GivePlayerPearlPacket(itemStack));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {return ItemStack.EMPTY;}

    @Override
    public boolean stillValid(Player player) {return AbstractContainerMenu.stillValid(this.access, player, ENDER_ECHO_TUNER.get());}

    public List<ItemStack> getIconList() {return iconList;}

    public BlockPos getTunerPos() {return tunerPos;}

    public EnderEchoTunerBlockEntity getTuner() {
        if (Minecraft.getInstance().level == null) return null;
        var blockEntity = Minecraft.getInstance().level.getBlockEntity(tunerPos);
        if (blockEntity instanceof EnderEchoTunerBlockEntity tuner) return tuner;
        return null;
    }

    public List<MarkedPositionsManager.MarkedPositions> getMarkedPositionsCache() {return markedPositionsCache;}

    public void writeClientSideData(RegistryFriendlyByteBuf buf, BlockPos pos) {
        buf.writeInt(selected_tuner_tab);
        buf.writeInt(ee_pearl_amount);
        buf.writeBlockPos(pos);
        buf.writeCollection(markedPositionsCache, MarkedPositionsManager.STREAM_CODEC);
        for (ItemStack stack : iconList) buf.writeResourceLocation(ITEM.getKey(stack.getItem()));
    }
}
