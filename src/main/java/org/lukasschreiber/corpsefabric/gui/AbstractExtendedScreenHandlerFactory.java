package org.lukasschreiber.corpsefabric.gui;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractExtendedScreenHandlerFactory implements ExtendedScreenHandlerFactory {
    private final Text name;
    private final ScreenHandlerFactory baseFactory;


    protected AbstractExtendedScreenHandlerFactory(ScreenHandlerFactory baseFactory, Text name) {
        this.name = name;
        this.baseFactory = baseFactory;
    }

    @Override
    public Text getDisplayName() {
        return this.name;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return this.baseFactory.createMenu(syncId, playerInventory, player);
    }
}
