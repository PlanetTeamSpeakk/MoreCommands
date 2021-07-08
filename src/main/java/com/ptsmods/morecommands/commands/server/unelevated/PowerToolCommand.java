package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.callbacks.MouseCallback;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class PowerToolCommand extends Command {
	public void preinit() {
		registerCallback(MouseCallback.EVENT, (button, action, mods) -> checkPowerTool(action));
	}

	@Environment(EnvType.CLIENT)
	private boolean checkPowerTool(int action) {
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null && action == 1 && MinecraftClient.getInstance().currentScreen == null) {
			String cmd = Optional.ofNullable(getPowerTool(player, player.getStackInHand(Hand.MAIN_HAND))).orElse(getPowerTool(player, player.getStackInHand(Hand.OFF_HAND)));
			if (cmd != null) {
				MinecraftClient.getInstance().player.sendChatMessage("/" + cmd);
				return true;
			}
		}
		return false;
	}

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.getRoot().addChild(MoreCommands.createAlias("pt", dispatcher.register(literalReq("powertool").executes(ctx -> {
			PlayerEntity player = ctx.getSource().getPlayer();
			ItemStack stack = getPowerToolStack(player);
			if (stack == null) sendError(ctx, "The item you're holding is not a powertool.");
			else if (!getPowerToolOwner(stack).equals(player.getUuid())) sendError(ctx, "This is not your powertool.");
			else {
				setPowerTool(player, stack, null);
				sendMsg(ctx, "The item you're holding is no longer a powertool.");
				return 1;
			}
			return 0;
		}).then(argument("cmd", StringArgumentType.greedyString()).executes(ctx -> {
			PlayerEntity player = ctx.getSource().getPlayer();
			ItemStack stack = player.getMainHandStack();
			if (isPowerTool(stack) && !getPowerToolOwner(stack).equals(player.getUuid())) sendError(ctx, "This is not your powertool.");
			else {
				setPowerTool(player, stack, ctx.getArgument("cmd", String.class));
				sendMsg(ctx, "Your item is now a powertool!");
				return 1;
			}
			return 0;
		})))));
	}

	public static String getPowerTool(PlayerEntity player, ItemStack stack) {
		return isPowerTool(stack) && getPowerToolOwner(stack).equals(player.getUuid()) ? Objects.requireNonNull(stack.getTag()).getCompound("PowerTool").getString("Command") : null;
	}

	public static void setPowerTool(PlayerEntity owner, ItemStack stack, String command) {
		NbtCompound tag = stack.getOrCreateTag();
		if (command == null && tag.contains("PowerTool", 10)) tag.remove("PowerTool");
		else if (command != null) {
			if (command.startsWith("/")) command = command.substring(1);
			if (!tag.contains("PowerTool", 10)) tag.put("PowerTool", new NbtCompound());
			tag.getCompound("PowerTool").putUuid("Owner", owner.getUuid());
			tag.getCompound("PowerTool").putString("Command", command);
		}
	}

	public static UUID getPowerToolOwner(ItemStack stack) {
		return stack.getOrCreateTag().getCompound("PowerTool").getUuid("Owner");
	}

	public static ItemStack getPowerToolStack(PlayerEntity player) {
		ItemStack stack = player.getMainHandStack();
		if (stack.getTag() == null || !stack.getTag().contains("PowerTool", 10)) stack = player.getOffHandStack();
		return stack.getTag() == null || !stack.getTag().contains("PowerTool", 10) ? null : stack;
	}

	public static boolean isPowerTool(ItemStack stack) {
		return stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("PowerTool", 10);
	}
}
