package com.unddefined.enderechoing.client.gui;

import com.unddefined.enderechoing.blocks.entity.EnderEchoTunerBlockEntity;
import com.unddefined.enderechoing.network.packet.GivePlayerPearlPacket;
import com.unddefined.enderechoing.network.packet.SetSelectedPositionPacket;
import com.unddefined.enderechoing.server.DataComponents.MarkedPositionsManager;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

import static com.unddefined.enderechoing.EnderEchoing.GZERO;
import static com.unddefined.enderechoing.EnderEchoing.TUNER_MENU;
import static com.unddefined.enderechoing.blocks.EnderEchoTunerBlock.CHARGED;
import static com.unddefined.enderechoing.blocks.EnderEchoTunerBlock.FACING;
import static com.unddefined.enderechoing.server.registry.BlockRegistry.ENDER_ECHO_TUNER;
import static com.unddefined.enderechoing.server.registry.DataRegistry.*;
import static net.minecraft.core.registries.BuiltInRegistries.ITEM;

public class TunerMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    public int ee_pearl_amount;
    public int selected_tuner_tab;
    private boolean multi_blocked = false;
    private boolean tuner_charged = false;
    private boolean facing_down = false;
    private List<ItemStack> iconList = new ArrayList<>();
    private List<MarkedPositionsManager.MarkedPositions> markedPositionsCache;
    private GlobalPos tunerPos;
    private GlobalPos selectedPos;

    public TunerMenu(int c, Inventory i, FriendlyByteBuf buf) {this(c, i, ContainerLevelAccess.NULL, buf);}

    public TunerMenu(int containerId, Inventory playerInv, ContainerLevelAccess A, FriendlyByteBuf buf) {
        super(TUNER_MENU.get(), containerId);
        this.access = A;
        this.selected_tuner_tab = buf.readInt();
        this.ee_pearl_amount = buf.readInt();
        this.tunerPos = buf.readGlobalPos();
        this.selectedPos = buf.readGlobalPos();
        this.multi_blocked = buf.readBoolean();
        this.tuner_charged = buf.readBoolean();
        this.facing_down = buf.readBoolean();
        for (int i = 0; i < 10; i++) this.iconList.add(new ItemStack(ITEM.get(buf.readResourceLocation())));
        this.markedPositionsCache = buf.readList(MarkedPositionsManager.MarkedPositions.STREAM_CODEC);
    }

    public TunerMenu(int containerId, Inventory playerInv, ContainerLevelAccess A) {
        super(TUNER_MENU.get(), containerId);
        this.access = A;
        A.execute((level, pos) -> {
            this.selected_tuner_tab = playerInv.player.getData(SELECTED_TUNER_TAB.get());
            this.ee_pearl_amount = playerInv.player.getData(EE_PEARL_AMOUNT.get());
            this.iconList = playerInv.player.getData(ICON_LIST.get());
            var M = playerInv.player.getData(MARKED_POSITIONS_CACHE.get());
            M.checkBounds();
            this.markedPositionsCache = M.markedPositions();
            if (level.getBlockEntity(pos) instanceof EnderEchoTunerBlockEntity E){
                this.multi_blocked = E.checkMultiblock();
                tuner_charged = E.getBlockState().getValue(CHARGED);
                facing_down = E.getBlockState().getValue(FACING).equals(Direction.DOWN);
                selectedPos = E.getSelectedPos();
            }
        });
    }

    public void setSelectedPosition(MarkedPositionsManager.MarkedPositions M) {
        if (M == null) PacketDistributor.sendToServer(new SetSelectedPositionPacket(tunerPos.pos(), GZERO, ""));
        else PacketDistributor.sendToServer(new SetSelectedPositionPacket(tunerPos.pos(), new GlobalPos(M.dimension(), M.pos()), M.name()));
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

    public GlobalPos getTunerPos() {return tunerPos;}

    public GlobalPos getSelectedPos() {return selectedPos;}

    public Boolean isMultiBlocked() {return multi_blocked;}

    public Boolean isCharged(){return tuner_charged;}

    public boolean isFacing_down() {return facing_down;}

    public List<MarkedPositionsManager.MarkedPositions> getMarkedPositionsCache() {return markedPositionsCache;}

    public void writeClientSideData(RegistryFriendlyByteBuf buf, GlobalPos pos) {
        buf.writeInt(selected_tuner_tab);
        buf.writeInt(ee_pearl_amount);
        buf.writeGlobalPos(pos);
        buf.writeGlobalPos(selectedPos);
        buf.writeBoolean(multi_blocked);
        buf.writeBoolean(tuner_charged);
        buf.writeBoolean(facing_down);
        for (ItemStack stack : iconList) buf.writeResourceLocation(ITEM.getKey(stack.getItem()));
        buf.writeCollection(markedPositionsCache, MarkedPositionsManager.MarkedPositions.STREAM_CODEC);
    }
}
