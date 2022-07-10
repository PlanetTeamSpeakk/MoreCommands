package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;

public class EnderChestCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.getRoot().addChild(MoreCommands.createAlias("ec", dispatcher.register(literalReq("enderchest")
                .executes(ctx -> execute(ctx, null))
                .then(argument("player", EntityArgument.player())
                        .requires(hasPermissionOrOp("morecommands.enderchest.others"))
                        .executes(ctx -> execute(ctx, EntityArgument.getPlayer(ctx, "player")))))));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/ender-chest";
    }

    private int execute(CommandContext<CommandSourceStack> ctx, Player p) throws CommandSyntaxException {
        Player player = p == null ? ctx.getSource().getPlayerOrException() : p;
        ctx.getSource().getPlayerOrException().openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return literalText("")
                        .append(Compat.get().builderFromText(player.getDisplayName()))
                        .append(literalText("'" + (IMoreCommands.get().textToString(player.getDisplayName(), null, true).endsWith("s") ? "" : "s") + " enderchest"))
                        .build();
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                return ChestMenu.threeRows(syncId, inv, player.getEnderChestInventory());
            }
        });
        return 1;
    }
}
