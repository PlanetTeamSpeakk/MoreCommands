package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HatCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("hat")
                .executes(ctx -> {
                    Player player = ctx.getSource().getPlayerOrException();
                    ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
                    Compat.get().getInventory(player).armor.set(EquipmentSlot.HEAD.getIndex(), player.getMainHandItem());
                    if (Inventory.isHotbarSlot(Compat.get().getInventory(player).selected)) Compat.get().getInventory(player).items.set(Compat.get().getInventory(player).selected, stack);
                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/hat";
    }
}
