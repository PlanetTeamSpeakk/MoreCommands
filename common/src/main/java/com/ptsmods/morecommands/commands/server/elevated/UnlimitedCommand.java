package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Objects;

public class UnlimitedCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literalReqOp("unlimited").executes(ctx -> {
			ItemStack stack = ctx.getSource().getEntityOrThrow().getItemsHand().iterator().next();
			if (stack != null && stack.getItem() != Items.AIR) {
				NbtCompound tag = stack.getOrCreateNbt();
				if (tag.contains("Unlimited")) tag.remove("Unlimited");
				else tag.putByte("Unlimited", (byte) 1);
				stack.setCount(1);
				sendMsg(ctx, "Your itemstack will now " + Util.formatFromBool(tag.contains("Unlimited"), "never run out", "run out again") + DF + ".");
				return tag.contains("Unlimited") ? 2 : 1;
			}
			sendMsg(ctx, "You must be holding an item.");
			return 0;
		}));
	}

	public static boolean isUnlimited(ItemStack stack) {
		return stack.hasNbt() && Objects.requireNonNull(stack.getNbt()).contains("Unlimited");
	}
}
