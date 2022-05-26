package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.miscellaneous.InvSeeScreenHandler;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class InvseeCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReqOp("invsee")
                .then(argument("player", EntityArgumentType.player())
                        .executes(ctx -> {
                            PlayerEntity p = EntityArgumentType.getPlayer(ctx, "player");
                            if (p == ctx.getSource().getPlayerOrThrow()) sendMsg(ctx, "You can just press E, you know?");
                            else openInventory(ctx.getSource().getPlayerOrThrow(), p);
                            return 1;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/server/elevated/invsee";
    }

    public static void openInventory(PlayerEntity player, PlayerEntity target) {
        player.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return emptyText()
                        .append(Compat.get().builderFromText(target.getDisplayName()))
                        .append(literalText("'" + (IMoreCommands.get().textToString(target.getDisplayName(), null, false).endsWith("s") ? "" : "s") + " inventory"))
                        .build();
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new InvSeeScreenHandler(syncId, inv, Compat.get().getInventory(target), player);
            }
        });
    }

}
