package com.ptsmods.morecommands.api.miscellaneous;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

// Cancelling slot clicks is handled in MixinScreenHandler in the compat package.
public class InvSeeScreenHandler extends ChestMenu {
    public final Player target;

    public InvSeeScreenHandler(int syncId, Inventory playerInventory, Container inventory, Player target) {
        super(MenuType.GENERIC_9x4, syncId, playerInventory, inventory, 4);
        this.target = target;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
