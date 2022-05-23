package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class AnvilCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReq("anvil").executes(ctx -> {
            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
            ctx.getSource().getPlayerOrThrow().openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) -> new AnvilScreenHandler(i, playerInventory, ScreenHandlerContext.create(player.getWorld(), player.getBlockPos())) {
                public boolean canUse(PlayerEntity player) {
                    return true;
                }
            }, translatableText("container.repair").build()));
            return 1;
        }));
    }
}
