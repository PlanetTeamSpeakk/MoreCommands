package com.ptsmods.morecommands.commands.unelevated;

import com.mojang.brigadier.CommandDispatcher;
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
                    player.getInventory().armor.set(EquipmentSlot.HEAD.getIndex(), player.getMainHandItem());
                    if (Inventory.isHotbarSlot(player.getInventory().selected)) player.getInventory().items.set(player.getInventory().selected, stack);
                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/hat";
    }
}
