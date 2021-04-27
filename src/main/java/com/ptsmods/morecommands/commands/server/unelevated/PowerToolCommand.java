package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.UUID;

public class PowerToolCommand extends Command {

	public void preinit() {
		registerCallback(AttackEntityCallback.EVENT, (player, world, hand, entity, hitResult) -> toResult(checkPowerTool(player, world, hand), world.isClient));
		registerCallback(AttackBlockCallback.EVENT, (player, world, hand, pos, direction) -> toResult(checkPowerTool(player, world, hand), world.isClient));
		registerCallback(UseBlockCallback.EVENT, (player, world, hand, hitResult) -> toResult(checkPowerTool(player, world, hand), false));
		registerCallback(UseEntityCallback.EVENT, (player, world, hand, entity, hitResult) -> toResult(checkPowerTool(player, world, hand), world.isClient));
		registerCallback(UseItemCallback.EVENT, (player, world, hand) -> toTypedResult(checkPowerTool(player, world, hand), world.isClient, player, hand));
	}

	private ActionResult toResult(boolean b, boolean isClient) {
		return b ? isClient ? ActionResult.SUCCESS : ActionResult.CONSUME : ActionResult.PASS;
	}

	private TypedActionResult<ItemStack> toTypedResult(boolean b, boolean isClient, PlayerEntity player, Hand hand) {
		return b && !isClient ? TypedActionResult.success(player.getStackInHand(hand)) : TypedActionResult.pass(player.getStackInHand(hand));
	}

	private boolean checkPowerTool(PlayerEntity player, World world, Hand hand) {
		String cmd = getPowerTool(player, player.getStackInHand(hand));
		if (cmd != null) {
			if (!world.isClient) player.getServer().getCommandManager().execute(player.getCommandSource(), "/" + cmd);
			return true;
		}
		return false;
	}

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.getRoot().addChild(MoreCommands.createAlias("pt", dispatcher.register(literal("powertool").executes(ctx -> {
			PlayerEntity player = ctx.getSource().getPlayer();
			ItemStack stack = getPowerToolStack(player);
			if (stack == null) sendMsg(ctx, Formatting.RED + "The item you're holding is not a powertool.");
			else if (!getPowerToolOwner(stack).equals(player.getUuid())) sendMsg(ctx, Formatting.RED + "This is not your powertool.");
			else {
				setPowerTool(player, stack, null);
				sendMsg(ctx, "The item you're holding is no longer a powertool.");
				return 1;
			}
			return 0;
		}).then(argument("cmd", StringArgumentType.greedyString()).executes(ctx -> {
			PlayerEntity player = ctx.getSource().getPlayer();
			ItemStack stack = player.getMainHandStack();
			if (isPowerTool(stack) && !getPowerToolOwner(stack).equals(player.getUuid())) sendMsg(ctx, Formatting.RED + "This is not your powertool.");
			else {
				setPowerTool(player, stack, ctx.getArgument("cmd", String.class));
				sendMsg(ctx, "Your item is now a powertool!");
				return 1;
			}
			return 0;
		})))));
	}

	public static String getPowerTool(PlayerEntity player, ItemStack stack) {
		return isPowerTool(stack) && getPowerToolOwner(stack).equals(player.getUuid()) ? stack.getTag().getCompound("PowerTool").getString("Command") : null;
	}

	public static void setPowerTool(PlayerEntity owner, ItemStack stack, String command) {
		NbtCompound tag = stack.getOrCreateTag();
		if (command == null && tag.contains("PowerTool", 10)) tag.remove("PowerTool");
		else {
			if (command.startsWith("/")) command = command.substring(1);
			if (!tag.contains("PowerTool", 10)) tag.put("PowerTool", new NbtCompound());
			tag.getCompound("PowerTool").putUuid("Owner", owner.getUuid());
			tag.getCompound("PowerTool").putString("Command", command);
		}
	}

	public static UUID getPowerToolOwner(ItemStack stack) {
		return stack.getTag().getCompound("PowerTool").getUuid("Owner");
	}

	public static ItemStack getPowerToolStack(PlayerEntity player) {
		ItemStack stack = player.getMainHandStack();
		if (stack.getTag() == null || !stack.getTag().contains("PowerTool", 10)) stack = player.getOffHandStack();
		return stack.getTag() == null || !stack.getTag().contains("PowerTool", 10) ? null : stack;
	}

	public static boolean isPowerTool(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains("PowerTool", 10);
	}
}
