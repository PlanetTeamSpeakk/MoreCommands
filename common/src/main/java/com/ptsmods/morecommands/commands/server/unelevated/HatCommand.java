package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;

public class HatCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReq("hat")
                .executes(ctx -> {
                    PlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    ItemStack stack = player.getEquippedStack(EquipmentSlot.HEAD);
                    Compat.get().getInventory(player).armor.set(EquipmentSlot.HEAD.getEntitySlotId(), player.getMainHandStack());
                    if (PlayerInventory.isValidHotbarIndex(Compat.get().getInventory(player).selectedSlot)) Compat.get().getInventory(player).main.set(Compat.get().getInventory(player).selectedSlot, stack);
                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/server/unelevated/hat";
    }
}
