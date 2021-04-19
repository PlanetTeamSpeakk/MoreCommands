package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.ServerCommandSource;

public class UnlimitedCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("unlimited").requires(IS_OP).executes(ctx -> {
			ItemStack stack = ctx.getSource().getEntityOrThrow().getItemsHand().iterator().next();
			if (stack != null && stack.getItem() != Items.AIR) {
				CompoundTag tag = stack.getOrCreateTag();
				if (tag.contains("Unlimited")) tag.remove("Unlimited");
				else tag.putByte("Unlimited", (byte) 1);
				stack.setCount(1);
				sendMsg(ctx, "Your itemstack will now " + formatFromBool(tag.contains("Unlimited"), "never run out", "run out again") + DF + ".");
				return tag.contains("Unlimited") ? 2 : 1;
			}
			sendMsg(ctx, "You must be holding an item.");
			return 0;
		}));
	}

	public static boolean isUnlimited(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains("Unlimited");
	}
}
