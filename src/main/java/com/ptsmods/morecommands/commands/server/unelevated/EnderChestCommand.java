package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class EnderChestCommand extends Command {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.getRoot().addChild(MoreCommands.createAlias("ec", dispatcher.register(literal("enderchest").executes(ctx -> execute(ctx, null)).then(argument("player", EntityArgumentType.player()).requires(IS_OP).executes(ctx -> execute(ctx, EntityArgumentType.getPlayer(ctx, "player")))))));
    }

    private int execute(CommandContext<ServerCommandSource> ctx, PlayerEntity p) throws CommandSyntaxException {
        PlayerEntity player = p == null ? ctx.getSource().getPlayer() : p;
        ctx.getSource().getPlayer().openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                LiteralText text = new LiteralText("");
                text.getSiblings().add(player.getDisplayName());
                text.getSiblings().add(new LiteralText("'" + (MoreCommands.textToString(player.getDisplayName(), null).endsWith("s") ? "" : "s") + " enderchest"));
                return text;
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return GenericContainerScreenHandler.createGeneric9x3(syncId, inv, player.getEnderChestInventory());
            }
        });
        return 1;
    }

}
