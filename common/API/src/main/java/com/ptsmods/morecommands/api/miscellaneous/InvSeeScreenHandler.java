package com.ptsmods.morecommands.api.miscellaneous;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

// Cancelling slot clicks is handled in MixinScreenHandler in the compat package.
public class InvSeeScreenHandler extends GenericContainerScreenHandler {
    public final PlayerEntity target;

    public InvSeeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PlayerEntity target) {
        super(ScreenHandlerType.GENERIC_9X4, syncId, playerInventory, inventory, 4);
        this.target = target;
    }
}
