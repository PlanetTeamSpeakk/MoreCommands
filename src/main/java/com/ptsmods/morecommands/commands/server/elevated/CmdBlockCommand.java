package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

// Looks a little similar to BarrierCommand, doesn't it?
public class CmdBlockCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literalReqOp("cmdblock").executes(ctx -> giveCmdBlock(ctx, ctx.getSource().getPlayer(), 1))
				.then(argument("amount", IntegerArgumentType.integer(0)).executes(ctx -> giveCmdBlock(ctx, ctx.getSource().getPlayer(), ctx.getArgument("amount", Integer.class)))
				.then(argument("player", EntityArgumentType.player()).executes(ctx -> giveCmdBlock(ctx, EntityArgumentType.getPlayer(ctx, "player"), ctx.getArgument("amount", Integer.class))))));
	}

	private int giveCmdBlock(CommandContext<ServerCommandSource> ctx, PlayerEntity player, int amount) throws CommandSyntaxException {
		if (player.giveItemStack(new ItemStack(Items.COMMAND_BLOCK, amount)))
			player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
		sendMsg(ctx, (player == ctx.getSource().getPlayer() ? "You have" : SF + MoreCommands.textToString(player.getDisplayName(), SS, true) + Formatting.RESET + " has") + " been given " + SF + amount + " command block" + (amount == 1 ? "" : "s") + Formatting.RESET + ".");
		return amount;
	}

}
