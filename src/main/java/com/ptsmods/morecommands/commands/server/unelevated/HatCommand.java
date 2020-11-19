package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;

public class HatCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("hat").executes(ctx -> {
            PlayerEntity player = ctx.getSource().getPlayer();
            ItemStack stack = player.getEquippedStack(EquipmentSlot.HEAD);
            player.inventory.armor.set(EquipmentSlot.HEAD.getEntitySlotId(), player.getMainHandStack());
            if (PlayerInventory.isValidHotbarIndex(player.inventory.selectedSlot)) player.inventory.main.set(player.inventory.selectedSlot, stack);
            return 1;
        }));
    }
}
