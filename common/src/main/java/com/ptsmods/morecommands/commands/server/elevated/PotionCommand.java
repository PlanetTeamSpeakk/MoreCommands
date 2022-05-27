package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.ptsmods.morecommands.arguments.HexIntegerArgumentType;
import com.ptsmods.morecommands.arguments.PotionArgumentType;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.argument.StatusEffectArgumentType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.registry.Registry;

import java.util.Iterator;
import java.util.List;

public class PotionCommand extends Command {
    private static final SimpleCommandExceptionType NO_POTION = new SimpleCommandExceptionType(literalText("The item you're holding is not a potion or tipped arrow.").build());

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
         dispatcher.register(literalReqOp("potion")
                 .then(literal("add")
                         .then(argument("effect", StatusEffectArgumentType.statusEffect())
                                 .executes(ctx -> executeAdd(ctx, 60*20, (byte) 0, false, true))
                                 .then(argument("duration", IntegerArgumentType.integer(1))
                                         .executes(ctx -> executeAdd(ctx, ctx.getArgument("duration", Integer.class), (byte) 0, false, true))
                                         .then(argument("amplifier", IntegerArgumentType.integer(0, 255))
                                                 .executes(ctx -> executeAdd(ctx, ctx.getArgument("duration", Integer.class), ctx.getArgument("amplifier", Integer.class).byteValue(), false, true))
                                                 .then(argument("ambient", BoolArgumentType.bool())
                                                         .executes(ctx -> executeAdd(ctx, ctx.getArgument("duration", Integer.class), ctx.getArgument("amplifier", Integer.class).byteValue(),
                                                                 ctx.getArgument("ambient", Boolean.class), true))
                                                         .then(argument("showParticles", BoolArgumentType.bool())
                                                                 .executes(ctx -> executeAdd(ctx, ctx.getArgument("duration", Integer.class), ctx.getArgument("amplifier", Integer.class).byteValue(),
                                                                         ctx.getArgument("ambient", Boolean.class), ctx.getArgument("showParticles", Boolean.class)))))))))
                 .then(literal("remove")
                         .then(argument("index", IntegerArgumentType.integer(1))
                                 .executes(ctx -> {
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
                                })))
                 .then(literal("settype")
                         .then(argument("type", PotionArgumentType.potion())
                                 .executes(ctx -> {
                                    ItemStack stack = checkHeldItem(ctx);
                                    Potion old = PotionUtil.getPotion(stack);
                                    PotionUtil.setPotion(stack, PotionArgumentType.getPotion(ctx, "type"));
                                    sendMsg(ctx, "The type of the potion has been set from " + SF + Registry.POTION.getId(old) + DF + " to " + SF + Registry.POTION.getId(PotionUtil.getPotion(stack)) + DF + ".");
                                    return 1;
                                })))
                 .then(literal("setcolour")
                         .then(argument("colour", HexIntegerArgumentType.hexInt())
                                 .executes(this::executeSetColour)))
                 .then(literal("setcolor")
                         .then(argument("colour", HexIntegerArgumentType.hexInt())
                                 .executes(this::executeSetColour))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/potion";
    }

    private int executeSetColour(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int colour = HexIntegerArgumentType.getHexInt(ctx, "colour");
        ItemStack stack = checkHeldItem(ctx);
        stack.getOrCreateNbt().putInt("CustomPotionColor", colour);
        sendMsg(ctx, literalText("The potion's colour has been set to ", DS)
                .append(literalText(String.format("#%06X", colour))
                        .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(colour))))
                .append(literalText(".")));
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
            else throw NO_POTION.create();
        }
        return stack;
    }
}
