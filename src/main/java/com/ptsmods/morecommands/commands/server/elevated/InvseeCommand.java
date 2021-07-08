package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class InvseeCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literalReqOp("invsee").then(argument("player", EntityArgumentType.player()).executes(ctx -> {
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
				return new LiteralText("")
						.append(target.getDisplayName())
						.append(new LiteralText("'" + (MoreCommands.textToString(target.getDisplayName(), null, false).endsWith("s") ? "" : "s") + " inventory"));
			}

			@Override
			public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
				return new InvSeeScreenHandler(syncId, inv, Compat.getCompat().getInventory(target), target);
			}
		});
	}

	// Cancelling slot clicks is handled in MixinScreenHandler in the compat package.
	public static class InvSeeScreenHandler extends GenericContainerScreenHandler {
		public final PlayerEntity target;

		public InvSeeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PlayerEntity target) {
			super(ScreenHandlerType.GENERIC_9X4, syncId, playerInventory, inventory, 4);
			this.target = target;
		}
	}
}
