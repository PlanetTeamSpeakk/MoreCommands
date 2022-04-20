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

import java.util.Random;

public class BarrierCommand extends Command {
    private static final Random random = new Random();

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literalReqOp("barrier")
                .executes(ctx -> execute(ctx, ctx.getSource().getPlayer(), 1))
				.then(argument("amount", IntegerArgumentType.integer(0))
                        .executes(ctx -> execute(ctx, ctx.getSource().getPlayer(), ctx.getArgument("count", Integer.class)))
				.then(argument("player", EntityArgumentType.player())
                        .executes(ctx -> execute(ctx, EntityArgumentType.getPlayer(ctx, "player"), ctx.getArgument("amount", Integer.class))))));
	}

	private int execute(CommandContext<ServerCommandSource> ctx, PlayerEntity player, int count) {
		if (player.getInventory().insertStack(new ItemStack(Items.BARRIER, count)))
			player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
                    ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);

		sendMsg(ctx, (player == ctx.getSource().getEntity() ? "You have" : SF + MoreCommands.textToString(player.getDisplayName(), SS, true) + Formatting.RESET + " has") +
                " been given " + SF + count + " barrier" + (count == 1 ? "" : "s") + Formatting.RESET + ".");
		return count;
	}

}
