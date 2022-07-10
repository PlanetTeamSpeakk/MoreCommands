package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.ptsmods.morecommands.api.Holder;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public class ReachCommand extends Command {
    public static final Attribute REACH_ATTRIBUTE = Holder.REACH_ATTRIBUTE;
    private static final UUID morecommandsModifier = UUID.nameUUIDFromBytes("morecommands".getBytes(StandardCharsets.UTF_8));

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (IMoreCommands.get().isServerOnly()) dispatcher.register(literalReqOp("reach")
                .executes(ctx -> {
                    sendError(ctx, "Reach cannot be used in server only mode.");
                    return 0;
                }));
        else dispatcher.register(literalReqOp("reach")
                .executes(ctx -> {
                    double reach = ctx.getSource().getPlayerOrException().getAttributeValue(REACH_ATTRIBUTE);
                    double base = ctx.getSource().getPlayerOrException().getAttributeBaseValue(REACH_ATTRIBUTE);
                    sendMsg(ctx, "Your reach is currently " + SF + reach + DF + (base != reach ? " (" + SF + base + " base" + DF + ")" : "") + ".");
                    return (int) reach;
                })
                .then(argument("reach", DoubleArgumentType.doubleArg(1d, 160d))
                        .executes(ctx -> {
                            double oldReach = ctx.getSource().getPlayerOrException().getAttributeBaseValue(REACH_ATTRIBUTE);
                            double reach = ctx.getArgument("reach", Double.class);
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            Objects.requireNonNull(player.getAttribute(REACH_ATTRIBUTE)).setBaseValue(reach);
                            addModifier("pehkui:reach", player, reach);
                            addModifier("reach-entity-attributes:reach", player, reach);
                            sendMsg(ctx, "Your reach has been set from " + SF + oldReach + DF + " to " + SF + reach + DF + ".");
                            return (int) reach;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/reach";
    }

    private void addModifier(String id, ServerPlayer player, double reach) {
        Optional.ofNullable(Registry.ATTRIBUTE.get(new ResourceLocation(id)))
                .map(player::getAttribute)
                .ifPresent(atr -> {
                    atr.removePermanentModifier(morecommandsModifier);
                    atr.addPermanentModifier(new AttributeModifier(morecommandsModifier, "MoreCommands Modifier", reach / 4.5D, AttributeModifier.Operation.MULTIPLY_TOTAL));
                });
    }

    public static double getReach(Player player, boolean squared) {
        return Math.pow(player.getAttributeValue(REACH_ATTRIBUTE) + (player instanceof ServerPlayer && squared ? 1.5d : 0), squared ? 2 : 1);
    }
}
