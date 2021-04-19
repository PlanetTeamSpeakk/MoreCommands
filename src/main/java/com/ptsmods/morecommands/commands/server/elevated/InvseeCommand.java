package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class InvseeCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("invsee").requires(IS_OP).then(argument("player", EntityArgumentType.player()).executes(ctx -> {
			PlayerEntity p = EntityArgumentType.getPlayer(ctx, "player");
			if (p == ctx.getSource().getPlayer()) sendMsg(ctx, "You can just press E, you know?");
			else openInventory(ctx.getSource().getPlayer(), p);
			return 1;
		})));
	}

	public static void openInventory(PlayerEntity player, PlayerEntity target) {
		player.openHandledScreen(new NamedScreenHandlerFactory() {
			@Override
			public Text getDisplayName() {
				LiteralText text = new LiteralText("");
				text.getSiblings().add(target.getDisplayName());
				text.getSiblings().add(new LiteralText("'" + (MoreCommands.textToString(target.getDisplayName(), null).endsWith("s") ? "" : "s") + " inventory"));
				return text;
			}

			@Override
			public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
				return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X4, syncId, inv, target.inventory, 4) {
					@Override
					public boolean canUse(PlayerEntity player) {
						return true; // Making sure the container does not close if the distance is or becomes too large.
					}

					@Override
					public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
						return target instanceof OtherClientPlayerEntity ? ItemStack.EMPTY : super.onSlotClick(i, j, actionType, playerEntity);
					}
				};
			}
		});
	}
}
