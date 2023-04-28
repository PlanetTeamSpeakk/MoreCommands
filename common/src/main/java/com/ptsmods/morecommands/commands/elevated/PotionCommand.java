package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.arguments.HexIntegerArgumentType;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.MappedRegistry;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.Iterator;
import java.util.List;

public class PotionCommand extends Command {
    private static final SimpleCommandExceptionType NO_POTION = new SimpleCommandExceptionType(literalText("The item you're holding is not a potion or tipped arrow.").build());

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("potion")
                .then(literal("add")
                        .then(this.<CommandSourceStack>newResourceArgument("effect", "mob_effect")
                                .executes(ctx -> executeAdd(ctx, 60 * 20, (byte) 0, false, true))
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
                                    List<MobEffectInstance> effects = PotionUtils.getCustomEffects(stack);

                                    int index = ctx.getArgument("index", Integer.class) - 1;
                                    if (index >= effects.size())
                                        sendError(ctx, "The given index was greater than the amount of custom potion effects this potion has (" + SF + effects.size() + DF + ").");
                                    else {
                                        effects.remove(index);
                                        PotionUtils.setCustomEffects(stack, effects);
                                        sendMsg(ctx, "Your potion now has " + SF + effects.size() + DF + " custom potion effect" + (effects.size() == 1 ? "" : "s") + ".");
                                        return effects.size() + 1;
                                    }

                                    return 0;
                                })))
                .then(literal("settype")
                        .then(this.<CommandSourceStack>newResourceArgument("type", "potion")
                                .executes(ctx -> {
                                    MappedRegistry<Potion> potionRegistry = Compat.get().getBuiltInRegistry("potion");
                                    Potion potion = getResource(ctx, "type", "potion");

                                    ItemStack stack = checkHeldItem(ctx);
                                    Potion old = PotionUtils.getPotion(stack);
                                    PotionUtils.setPotion(stack, potion);

                                    sendMsg(ctx, "The type of the potion has been set from " + SF + potionRegistry.getKey(old) +
                                            DF + " to " + SF + potionRegistry.getKey(potion) + DF + ".");
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

    private int executeSetColour(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        int colour = HexIntegerArgumentType.getHexInt(ctx, "colour");
        ItemStack stack = checkHeldItem(ctx);
        stack.getOrCreateTag().putInt("CustomPotionColor", colour);
        sendMsg(ctx, literalText("The potion's colour has been set to ", DS)
                .append(literalText(String.format("#%06X", colour))
                        .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(colour))))
                .append(literalText(".")));
        return colour;
    }

    private int executeAdd(CommandContext<CommandSourceStack> ctx, int duration, byte amplifier, boolean ambient, boolean showParticles) throws CommandSyntaxException {
        MobEffect effect = getResource(ctx, "effect", "mob_effect");
        ItemStack stack = checkHeldItem(ctx);
        List<MobEffectInstance> effects = PotionUtils.getCustomEffects(stack);
        effects.add(new MobEffectInstance(effect, duration, amplifier, ambient, showParticles));
        PotionUtils.setCustomEffects(stack, effects);
        sendMsg(ctx, "Your potion now has " + SF + effects.size() + DF + " custom potion effect" + (effects.size() == 1 ? "" : "s") + ".");
        return amplifier;
    }

    private ItemStack checkHeldItem(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Iterator<ItemStack> stacks = ctx.getSource().getEntityOrException().getHandSlots().iterator();
        ItemStack stack = ItemStack.EMPTY;
        while (stack.getItem() != Items.POTION && stack.getItem() != Items.TIPPED_ARROW) {
            if (stacks.hasNext()) stack = stacks.next();
            else throw NO_POTION.create();
        }
        return stack;
    }
}
