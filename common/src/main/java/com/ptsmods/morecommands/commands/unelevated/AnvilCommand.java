package com.ptsmods.morecommands.commands.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.jetbrains.annotations.NotNull;

public class AnvilCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("anvil")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    ctx.getSource().getPlayerOrException().openMenu(new SimpleMenuProvider((i, playerInventory, playerEntity) ->
                            new AnvilMenu(i, playerInventory, ContainerLevelAccess.create(player.getCommandSenderWorld(), Compat.get().blockPosition(player))) {
                                public boolean stillValid(@NotNull Player player) {
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
