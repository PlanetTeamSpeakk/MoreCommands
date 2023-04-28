package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.miscellaneous.InvSeeScreenHandler;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public class InvseeCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("invsee")
                .then(argument("player", EntityArgument.player())
                        .executes(ctx -> {
                            Player p = EntityArgument.getPlayer(ctx, "player");
                            if (p == ctx.getSource().getPlayerOrException()) sendMsg(ctx, "You can just press E, you know?");
                            else openInventory(ctx.getSource().getPlayerOrException(), p);
                            return 1;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/invsee";
    }

    public static void openInventory(Player player, Player target) {
        player.openMenu(new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return emptyText()
                        .append(Compat.get().builderFromText(target.getDisplayName()))
                        .append(literalText("'" + (IMoreCommands.get().textToString(target.getDisplayName(), null, false).endsWith("s") ? "" : "s") + " inventory"))
                        .build();
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                return new InvSeeScreenHandler(syncId, inv, player.getInventory(), player);
            }
        });
    }

}
