package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class AnvilCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("anvil")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    ctx.getSource().getPlayerOrException().openMenu(new SimpleMenuProvider((i, playerInventory, playerEntity) ->
                            new AnvilMenu(i, playerInventory, ContainerLevelAccess.create(player.getLevel(), player.blockPosition())) {
                                public boolean stillValid(Player player) {
                                    return true;
                                }
                            },
                            translatableText("container.repair").build()));
                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/anvil";
    }
}
