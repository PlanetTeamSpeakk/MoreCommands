package com.ptsmods.morecommands.commands.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;

public class CraftCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("craft")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    ctx.getSource().getPlayerOrException().openMenu(new SimpleMenuProvider((i, playerInventory, playerEntity) ->
                            new CraftingMenu(i, playerInventory, ContainerLevelAccess.create(player.getCommandSenderWorld(), Compat.get().blockPosition(player))) {
                                public boolean stillValid(Player player) {
                                    return true;
                                }
                            }, translatableText("container.crafting").build()));
                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/craft";
    }
}
