package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.arguments.HexIntegerArgumentType;
import com.ptsmods.morecommands.arguments.RegistryArgumentType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

import java.util.Iterator;
import java.util.List;

public class PotionCommand extends Command {

	private static final SimpleCommandExceptionType exc = new SimpleCommandExceptionType(new LiteralText("The item you're holding is not a potion or tipped arrow.").setStyle(Style.EMPTY.withFormatting(Formatting.RED)));

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		 dispatcher.register(literalReqOp("potion").then(literalReqOp("add").then(argument("effect", new RegistryArgumentType<>(Registry.STATUS_EFFECT)).executes(ctx -> executeAdd(ctx, 60*20, (byte) 0, false, true)).then(argument("duration", IntegerArgumentType.integer(1)).executes(ctx -> executeAdd(ctx, ctx.getArgument("duration", Integer.class), (byte) 0, false, true)).then(argument("amplifier", IntegerArgumentType.integer(0, 255)).executes(ctx -> executeAdd(ctx, ctx.getArgument("duration", Integer.class), ctx.getArgument("amplifier", Integer.class).byteValue(), false, true)).then(argument("ambient", BoolArgumentType.bool()).executes(ctx -> executeAdd(ctx, ctx.getArgument("duration", Integer.class), ctx.getArgument("amplifier", Integer.class).byteValue(), ctx.getArgument("ambient", Boolean.class), true)).then(argument("showParticles", BoolArgumentType.bool()).executes(ctx -> executeAdd(ctx, ctx.getArgument("duration", Integer.class), ctx.getArgument("amplifier", Integer.class).byteValue(), ctx.getArgument("ambient", Boolean.class), ctx.getArgument("showParticles", Boolean.class)))))))))
		.then(literalReqOp("remove").then(argument("index", IntegerArgumentType.integer(1)).executes(ctx -> {
			ItemStack stack = checkHeldItem(ctx);
			List<StatusEffectInstance> effects = PotionUtil.getCustomPotionEffects(stack);
			int index = ctx.getArgument("index", Integer.class)-1;
			if (index >= effects.size()) sendError(ctx, "The given index was greater than the amount of custom potion effects this potion has (" + SF + effects.size() + DF + ").");
			else {
				effects.remove(index);
				PotionUtil.setCustomPotionEffects(stack, effects);
				sendMsg(ctx, "Your potion now has " + SF + effects.size() + DF + " custom potion effect" + (effects.size() == 1 ? "" : "s") + ".");
				return effects.size()+1;
			}
			return 0;
		}))).then(literalReqOp("settype").then(argument("type", new RegistryArgumentType<>(Registry.POTION)).executes(ctx -> {
			ItemStack stack = checkHeldItem(ctx);
			Potion old = PotionUtil.getPotion(stack);
			PotionUtil.setPotion(stack, ctx.getArgument("type", Potion.class));
			sendMsg(ctx, "The type of the potion has been set from " + SF + Registry.POTION.getId(old) + DF + " to " + SF + Registry.POTION.getId(PotionUtil.getPotion(stack)) + DF + ".");
			return 0;
		}))).then(literalReqOp("setcolour").then(argument("colour", new HexIntegerArgumentType()).executes(this::executeSetColour)))
		.then(literalReqOp("setcolor").then(argument("colour", new HexIntegerArgumentType()).executes(this::executeSetColour))));
	}

	private int executeSetColour(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		int colour = ctx.getArgument("colour", Integer.class);
		ItemStack stack = checkHeldItem(ctx);
		stack.getOrCreateTag().putInt("CustomPotionColor", colour);
		sendMsg(ctx, new LiteralText("The potion's colour has been set to ").setStyle(DS).append(new LiteralText(String.format("#%06X", colour)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(colour))).append(new LiteralText(".").setStyle(DS))));
		return colour;
	}

	private int executeAdd(CommandContext<ServerCommandSource> ctx, int duration, byte amplifier, boolean ambient, boolean showParticles) throws CommandSyntaxException {
		StatusEffect effect = ctx.getArgument("effect", StatusEffect.class);
		ItemStack stack = checkHeldItem(ctx);
		List<StatusEffectInstance> effects = PotionUtil.getCustomPotionEffects(stack);
		effects.add(new StatusEffectInstance(effect, duration, amplifier, ambient, showParticles));
		PotionUtil.setCustomPotionEffects(stack, effects);
		sendMsg(ctx, "Your potion now has " + SF + effects.size() + DF + " custom potion effect" + (effects.size() == 1 ? "" : "s") + ".");
		return amplifier;
	}

	private ItemStack checkHeldItem(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		Iterator<ItemStack> stacks = ctx.getSource().getEntityOrThrow().getItemsHand().iterator();
		ItemStack stack = ItemStack.EMPTY;
		while (stack.getItem() != Items.POTION && stack.getItem() != Items.TIPPED_ARROW) {
			if (stacks.hasNext()) stack = stacks.next();
			else throw exc.create();
		}
		return stack;
	}
}
