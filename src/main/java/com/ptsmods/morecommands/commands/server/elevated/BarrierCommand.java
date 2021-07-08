package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

public class BarrierCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literalReqOp("barrier").executes(ctx -> giveBarrier(ctx, ctx.getSource().getPlayer(), 1))
				.then(argument("amount", IntegerArgumentType.integer(0)).executes(ctx -> giveBarrier(ctx, ctx.getSource().getPlayer(), ctx.getArgument("amount", Integer.class)))
				.then(argument("player", EntityArgumentType.player()).executes(ctx -> giveBarrier(ctx, EntityArgumentType.getPlayer(ctx, "player"), ctx.getArgument("amount", Integer.class))))));
	}

	private int giveBarrier(CommandContext<ServerCommandSource> ctx, PlayerEntity player, int amount) {
		if (player.giveItemStack(new ItemStack(Items.BARRIER, amount)))
			player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
		sendMsg(ctx, (player == ctx.getSource().getEntity() ? "You have" : SF + MoreCommands.textToString(player.getDisplayName(), SS, true) + Formatting.RESET + " has") + " been given " + SF + amount + " barrier" + (amount == 1 ? "" : "s") + Formatting.RESET + ".");
		return amount;
	}

}
